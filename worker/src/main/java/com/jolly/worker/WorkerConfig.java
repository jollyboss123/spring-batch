package com.jolly.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jolly.leader.domain.YearReport;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.step.item.SimpleChunkProcessor;
import org.springframework.batch.integration.chunk.ChunkProcessorChunkHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;

/**
 * @author jolly
 */
@Configuration
class WorkerConfig {
    private final ObjectMapper objectMapper;

    WorkerConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    DirectChannel requests() {
        return MessageChannels.direct().get();
    }

    @Bean
    DirectChannel replies() {
        return MessageChannels.direct().get();
    }

    @Bean
    IntegrationFlow messagesIn(ConnectionFactory connectionFactory) {
        return IntegrationFlow
                .from(Amqp.inboundAdapter(connectionFactory, "requests"))
                .channel(requests())
                .get();
    }

    @Bean
    IntegrationFlow outgoingReplies(AmqpTemplate template) {
        return IntegrationFlow
                .from(replies())
                .handle(Amqp.outboundAdapter(template).routingKey("replies"))
                .get();
    }

    private YearReport deserializeYearReportJson(String json) {
        try {
            return objectMapper.readValue(json, YearReport.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("couldn't parse the json",e);
        }
    }

    private static void doSomethingTimeIntensive(YearReport yearReport) {
        System.out.println("gotten year report");
        System.out.println(yearReport.toString());
    }

    @Bean
    @ServiceActivator(inputChannel = "requests", outputChannel = "replies", sendTimeout = "10000")
    ChunkProcessorChunkHandler<String> chunkProcessorChunkHandler() {
        ItemProcessor<String, YearReport> itemProcessor = yearReportJson -> {
            System.out.println("processing year report json: " + yearReportJson);
            Thread.sleep(5);
            return deserializeYearReportJson(yearReportJson);
        };

        ChunkProcessorChunkHandler<String> chunkProcessorChunkHandler = new ChunkProcessorChunkHandler<>();
        chunkProcessorChunkHandler.setChunkProcessor(new SimpleChunkProcessor<>(itemProcessor, this.writer()));
        return chunkProcessorChunkHandler;
    }

    @Bean
    ItemWriter<YearReport> writer() {
        return chunk -> chunk.getItems()
                .forEach(WorkerConfig::doSomethingTimeIntensive);
    }
}
