package com.jolly.starter.remotechunking.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.item.SimpleChunkProcessor;
import org.springframework.batch.integration.chunk.ChunkProcessorChunkHandler;
import org.springframework.batch.integration.chunk.ChunkRequest;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author jolly
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "jolly.batch.chunk.worker", havingValue = "true")
class WorkerChunkAutoConfiguration {
    @Bean
    @WorkerInboundChunkChannel
    DirectChannel workerRequestsMessageChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    @WorkerOutboundChunkChannel
    DirectChannel workerRepliesMessageChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("unchecked")
    ChunkProcessorChunkHandler<?> workerChunkProcessorChunkHandler(
            @WorkerItemProcessor ItemProcessor<?, ?> processor,
            @WorkerItemWriter ItemWriter<?> writer
            ) {
        ChunkProcessorChunkHandler<?> chunkProcessorChunkHandler = new ChunkProcessorChunkHandler<>();
        chunkProcessorChunkHandler.setChunkProcessor(new SimpleChunkProcessor(processor, writer));
        return chunkProcessorChunkHandler;
    }

    @Bean
    @SuppressWarnings("unchecked")
    IntegrationFlow chunkProcessorChunkHandlerIntegrationFlow(
            ChunkProcessorChunkHandler<Object> chunkProcessorChunkHandler
    ) {
        return IntegrationFlows
                .from(workerRequestsMessageChannel())
                .handle((GenericHandler<Object>) (payload, headers) -> {
                    try {
                        if (payload instanceof ChunkRequest) {
                            ChunkRequest<?> cr = (ChunkRequest<?>) payload;
                            Object chunkResponse = chunkProcessorChunkHandler.handleChunk((ChunkRequest<Object>) cr);
                            workerRepliesMessageChannel().send(MessageBuilder.withPayload(chunkResponse).build());
                        } else {
                            throw new IllegalStateException("the payload must be an instance of ChunkRequest!");
                        }
                        return null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .get();
    }
}
