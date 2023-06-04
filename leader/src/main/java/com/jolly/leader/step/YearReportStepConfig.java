package com.jolly.leader.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jolly.leader.domain.YearPlatformSales;
import com.jolly.leader.domain.YearReport;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.RemoteChunkHandlerFactoryBean;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.boot.autoconfigure.batch.JobExecutionEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jolly
 */
@Configuration
public
class YearReportStepConfig {
    private final Map<Integer, YearReport> reportMap = new ConcurrentHashMap<>();
    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ObjectMapper objectMapper;

    YearReportStepConfig(DataSource dataSource, JobRepository jobRepository, PlatformTransactionManager transactionManager, ObjectMapper objectMapper, ItemWriter<String> itemWriter) {
        this.dataSource = dataSource;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.objectMapper = objectMapper;
    }

    @EventListener
    public void batchJobCompleted(JobExecutionEvent event) {
        Map<String, ?> running = new HashMap<>() {{
            put("running", event.getJobExecution().getStatus().isRunning());
            put("finished", event.getJobExecution().getExitStatus().getExitCode());
        }};
        System.out.println("jobExecutionEvent: [" + running + "]");
        this.reportMap.clear();
    }

    private final RowMapper<YearReport> rowMapper = (rs, rowNum) -> {
        int year = rs.getInt("year");
        if (!this.reportMap.containsKey(year)) {
            this.reportMap.put(year, new YearReport(
                    year,
                    new ArrayList<>()
            ));
        }
        YearReport yr = this.reportMap.get(year);
        yr.breakout().add(new YearPlatformSales(year, rs.getString("platform"), rs.getFloat("sales")));
        return yr;
    };

    @Bean
    ItemReader<YearReport> yearPlatformSalesItemReader() {
        String sql = """
                select year   ,
                       ypr.platform,
                       ypr.sales,
                       (select count(yps.year) from year_platform_report yps where yps.year = ypr.year ) 
                from year_platform_report ypr
                where ypr.year != 0
                order by year
                """;
        return new JdbcCursorItemReaderBuilder<YearReport>()
                .sql(sql)
                .name("item.reader.year-platform-sales")
                .dataSource(this.dataSource)
                .rowMapper(this.rowMapper)
                .build();
    }

    @Bean
    public TaskletStep yearReportStep() {
        return new StepBuilder("step.year-report", jobRepository)
                .<YearReport, String>chunk(1000, this.transactionManager)
                .reader(yearPlatformSalesItemReader())
                .processor(objectMapper::writeValueAsString)
                .writer(chunkMessageChannelItemWriter())
                .build();
    }

    @Bean
    IntegrationFlow replyFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlow
                .from(Amqp.inboundAdapter(connectionFactory, "replies"))
                .channel(replies())
                .get();
    }

    @Bean
    DirectChannel requests() {
        return MessageChannels.direct().get();
    }

    @Bean
    QueueChannel replies() {
        return MessageChannels.queue().get();
    }

    static class DedupingChunkMessageChannelItemWriter<T> extends ChunkMessageChannelItemWriter<T> {
        @Override
        public void write(Chunk<? extends T> items) throws Exception {
            List<? extends T> inputCollection = items.getItems();
            List<T> newList = new ArrayList<T>(new LinkedHashSet<>(inputCollection));
            super.write(new Chunk<T>(newList));
        }
    }

    @Bean
    @StepScope
    ChunkMessageChannelItemWriter<String> chunkMessageChannelItemWriter() {
        ChunkMessageChannelItemWriter<String> chunkMessageChannelItemWriter = new DedupingChunkMessageChannelItemWriter<String>();
        chunkMessageChannelItemWriter.setMessagingOperations(messagingTemplate());
        chunkMessageChannelItemWriter.setReplyChannel(replies());
        return chunkMessageChannelItemWriter;
    }

    @Bean
    RemoteChunkHandlerFactoryBean<String> chunkHandler() throws Exception {
        ChunkMessageChannelItemWriter<String> chunkMessageChannelItemWriterProxy = chunkMessageChannelItemWriter();
        RemoteChunkHandlerFactoryBean<String> remoteChunkHandlerFactoryBean = new RemoteChunkHandlerFactoryBean<>();
        remoteChunkHandlerFactoryBean.setChunkWriter(chunkMessageChannelItemWriterProxy);
        remoteChunkHandlerFactoryBean.setStep(this.yearReportStep());
        return remoteChunkHandlerFactoryBean;
    }

    @Bean
    MessagingTemplate messagingTemplate() {
        MessagingTemplate template = new MessagingTemplate();
        template.setDefaultChannel(requests());
        template.setReceiveTimeout(2000);
        return template;
    }

    @Bean
    IntegrationFlow outboundFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlow
                .from(requests())
                .handle(Amqp.outboundAdapter(amqpTemplate).routingKey("requests"))
                .get();
    }
}
