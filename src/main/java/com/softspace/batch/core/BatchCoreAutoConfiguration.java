package com.softspace.batch.core;

import com.softspace.batch.core.job.FailRetryListener;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableBatchProcessing
public class BatchCoreAutoConfiguration {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobRegistry jobRegistry;


    @Bean
    JobOperator jobOperator() {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobLauncher(jobLauncher);
        return jobOperator;
    }

    /**
     * to register all job to the jobRegistry
     */
    @Bean
    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
        final JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }

    @Bean
    @ConditionalOnMissingBean
    FailRetryListener hourlyRetryListener() {
        return new FailRetryListener(Duration.ofHours(1), 5);
    }

    /**
     * required to
     */
    @Bean
    @ConditionalOnMissingBean
    ScheduledExecutorService scheduleExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

}
