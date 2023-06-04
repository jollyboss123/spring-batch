package com.jolly.leader.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author jolly
 */
@Configuration
public
class ErrorStepConfig {
    private final JobRepository repository;
    private final PlatformTransactionManager transactionManager;

    ErrorStepConfig(JobRepository repository, PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Step errorStep() {
        return new StepBuilder("error-step", repository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("something went wrong");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
