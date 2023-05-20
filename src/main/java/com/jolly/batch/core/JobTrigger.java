package com.jolly.batch.core;

import com.jolly.batch.core.util.BatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author jolly
 */
@Slf4j
public class JobTrigger {
    private final JobLauncher jobLauncher;
    private final Job job;
    private final Supplier<JobParameters> defaultValueProvider;
    private final String TAG;

    public JobTrigger(JobLauncher jobLauncher, Job job, Supplier<JobParameters> defaultValueProvider) {
        this.jobLauncher = Objects.requireNonNull(jobLauncher);
        this.job = Objects.requireNonNull(job);
        this.defaultValueProvider = Objects.requireNonNull(defaultValueProvider);
        this.TAG = "JobTrigger." + job.getName() + ": ";
    }

    public BatchStatus run() {
        return run(defaultValueProvider.get());
    }

    public BatchStatus run(boolean force) {
        if (!force) {
            return  run();
        }

        return run(new JobParametersBuilder(defaultValueProvider.get())
                .addDate("force", new Date())
                .toJobParameters());
    }

    public BatchStatus run(Map<String, String> jobParameter) {
        final Map<String, String> parameters = Objects.requireNonNull(jobParameter);
        final JobParametersBuilder builder = new JobParametersBuilder();
        for (Map.Entry<String, String> curr : parameters.entrySet()) {
            builder.addString(curr.getKey(), curr.getValue());
        }

        return run(builder.toJobParameters());
    }

    public BatchStatus run(JobParameters jobParameters) {
        final String paramDesc = BatchUtils.prettyString(jobParameters);
        try {
            final JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            final BatchStatus status = jobExecution.getStatus();
            log.info(TAG + "jobExecutionId={} executionStatus:{} {}", jobExecution.getId(), status.name(), paramDesc);
            return status;
        } catch (JobInstanceAlreadyCompleteException e) {
            log.info(TAG + "completed " + paramDesc);
            return BatchStatus.COMPLETED;
        } catch (JobExecutionAlreadyRunningException e) {
            log.info(TAG + "running " + paramDesc);
            return BatchStatus.FAILED;
        } catch (Exception e) {
            log.error(TAG + "failed: " + e.getMessage() + " " + paramDesc, e);
            return BatchStatus.FAILED;
        }
    }
}
