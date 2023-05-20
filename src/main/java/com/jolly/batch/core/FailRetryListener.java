package com.jolly.batch.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jolly
 */
@Slf4j
public final class FailRetryListener implements JobExecutionListener {
    private final Duration repeatInterval;
    private final int retryCount;
    private final Cache<String, AtomicInteger> jobRetryCount;
    private JobOperator jobOperator;
    private ScheduledExecutorService scheduledExecutorService;

    public FailRetryListener(Duration repeatInterval, int retryCount) {
        this(null, repeatInterval, retryCount);
    }

    public FailRetryListener(JobOperator jobOperator, Duration repeatInterval, int retryCount) {
        this.jobOperator = jobOperator;
        this.repeatInterval = repeatInterval;
        this.retryCount = retryCount;
        this.jobRetryCount = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofDays(7))
                .build();
    }

    @Autowired
    void setJobOperator(JobOperator jobOperator) {
        if (this.jobOperator == null) {
            log.warn("autowired setJobOperator");
            this.jobOperator = jobOperator;
        }
    }

    @Autowired
    void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        if (this.scheduledExecutorService == null || this.scheduledExecutorService.isShutdown()) {
            log.warn("autowired setScheduleExecutorService");
            this.scheduledExecutorService = scheduledExecutorService;
        }
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        if (jobOperator == null) {
            throw new IllegalStateException("jobOperator not allow to be null");
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        final Long execId = jobExecution.getId();
        if (jobExecution.getStatus() != BatchStatus.FAILED) {
            return;
        }

        try {
            final StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(jobExecution.getJobInstance().getJobName());
            final String key = jobExecution.getJobParameters().getParameters().entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .reduce(keyBuilder,
                            (stringBuilder, s) -> stringBuilder.append(':').append(s),
                            ((stringBuilder, stringBuilder2) -> stringBuilder))
                    .toString();

            final AtomicInteger aTried = jobRetryCount.get(key, AtomicInteger::new);
            final int failTried = aTried.getAndIncrement();
            final String jobName = jobExecution.getJobInstance().getJobName();
            if (failTried > retryCount) {
                log.error("ALERT code=job.abandon jobName={} jobExecutionKey={} execId={}", jobName, key, execId);
                scheduledExecutorService.schedule(() -> jobOperator.abandon(execId), 5, TimeUnit.SECONDS);
                return;
            }
            log.error("ALERT code=job.restart jobName={} jobExecutionKey={} execId={} restartInterval={} tried={}", jobName, key, execId, repeatInterval.toString().substring(2).toLowerCase(), failTried);
            scheduledExecutorService.schedule(() -> {
                try {
                    jobOperator.restart(execId);
                    log.info("complete restarted");
                } catch (Exception ignored) {
                    log.error("ALERT code=job.restart.fail jobName={} execId={} jobExecutionKey={}", jobName, execId, key, ignored);
                }
            }, repeatInterval.getSeconds(), TimeUnit.SECONDS);
        } catch (ExecutionException ignored) {
            // jobRetryCount wont throw exceptions
        }
    }
}
