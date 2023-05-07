package com.jolly.core.job;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * A Implementation of {@link org.springframework.batch.core.JobExecutionListener} which will schedule for retry after a job failed.
 * retry counter is store in memory. will be lose after restart.
 */
public final class FailRetryListener implements JobExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(FailRetryListener.class);
    private final Duration repeatInterval;
    private final int retryCount;
    private final Cache<String, AtomicInteger> jobRetryCount;
    private JobOperator jobOperator;
    private ScheduledExecutorService scheduleExecutorService;

    /**
     * @param repeatInterval repeat interval after fail
     * @param retryCount     how many try to retry before abandon the task
     */
    public FailRetryListener(Duration repeatInterval, int retryCount) {
        this(null, repeatInterval, retryCount);
    }

    /**
     * @param jobOperator    jobOperator. if this value is null, this object must return as Spring bean in order to autowired the jobOperator
     * @param repeatInterval repeat interval after fail
     * @param retryCount     how many try to retry before abandon the task
     */
    public FailRetryListener(JobOperator jobOperator, Duration repeatInterval, int retryCount) {
        this.jobOperator = jobOperator;
        this.repeatInterval = repeatInterval;
        this.retryCount = retryCount;
        // to auto house keeping the job
        this.jobRetryCount = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofDays(7))
                .build();
    }

    /**
     * using setter autowired to remove the exposure of the JobOperator
     *
     * @param jobOperator
     */
    @Autowired
    void setJobOperator(JobOperator jobOperator) {
        if (this.jobOperator == null) {
            log.warn("autowired setJobOperator");
            this.jobOperator = jobOperator;
        }
    }

    /**
     * using setter autowired to remove the exposure of the ScheduledExecutorService.
     * use to delay the after job execution
     *
     * @param scheduleExecutorService
     */
    @Autowired
    void setScheduleExecutorService(ScheduledExecutorService scheduleExecutorService) {
        if (this.scheduleExecutorService == null || this.scheduleExecutorService.isShutdown()) {
            log.warn("autowired setScheduleExecutorService");
            this.scheduleExecutorService = scheduleExecutorService;
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
                            (stringBuilder, stringBuilder2) -> stringBuilder)
                    .toString();

            final AtomicInteger aTried = jobRetryCount.get(key, AtomicInteger::new);
            final int failTried = aTried.getAndIncrement();
            final String jobName = jobExecution.getJobInstance().getJobName();
            if (failTried > retryCount) {
                log.error("ALERT code=job.abandon jobName={} jobExecutionKey={} execId={}", jobName, key, execId);
                scheduleExecutorService.schedule(() -> jobOperator.abandon(execId), 5, TimeUnit.SECONDS);
                return;
            }
            log.error("ALERT code=job.restart jobName={} jobExecutionKey={} execId={} restartInterval={} tried={}", jobName, key, execId, repeatInterval.toString().substring(2).toLowerCase(), failTried);
            scheduleExecutorService.schedule(() -> {
                try {
                    jobOperator.restart(execId);
                    log.info("complete restarted");
                } catch (Exception ignored) {
                    log.error("ALERT code=job.restart.fail jobName=" + jobName + " execId=" + execId + " jobExecutionKey=" + key, ignored);
                }
            }, repeatInterval.getSeconds(), TimeUnit.SECONDS);
        } catch (ExecutionException ignored) {
            // jobRetryCount wont throw exceptions.
        }
    }
}
