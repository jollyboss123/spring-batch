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
public class EndStepConfig {
    private final JobRepository repository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step endStep() {
        return new StepBuilder("end", repository)
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("the job is finished");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
