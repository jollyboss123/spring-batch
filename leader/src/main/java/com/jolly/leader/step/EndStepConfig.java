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
class EndStepConfig {
    private final JobRepository repository;
    private final PlatformTransactionManager transactionManager;

    EndStepConfig(JobRepository repository, PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Step endStep() {
        return new StepBuilder("end", repository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("the job is finished");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
