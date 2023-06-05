package com.jolly.leader.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class ErrorStepConfig {
    private final JobRepository repository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step errorStep() {
        return new StepBuilder("error-step", repository)
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("something went wrong");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
