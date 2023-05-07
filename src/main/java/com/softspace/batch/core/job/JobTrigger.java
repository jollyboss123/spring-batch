package com.softspace.batch.core.job;

import com.softspace.batch.core.util.BatchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * wrapper of {@link JobLauncher} to launch a new job wth provider {@code defaultValue} supplier
 */
public class JobTrigger {
    private static final Logger log = LoggerFactory.getLogger(JobTrigger.class);
    private final JobLauncher jobLauncher;
    private final Job job;
    private final Supplier<JobParameters> defaultValueProvider;
    private final String TAG;

    /**
     * @param jobLauncher Spring Batch job launcher
     * @param job job template
     * @param defaultValueProvider job parameter provider if trigger run without {@link #run()} or {@link #run(boolean)}. Expected to be daily job
     */
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
            return run();
        }
        return run(new JobParametersBuilder(defaultValueProvider.get())
                .addDate("force", new Date())
                .toJobParameters());
    }

    public BatchStatus run(Map<String, String> jobParameter) {
        final Map<String, String> parameters = Objects.requireNonNull(jobParameter);
        final JobParametersBuilder builder = new JobParametersBuilder();
        for (Map.Entry<String, String> cur : parameters.entrySet()) {
            builder.addString(cur.getKey(), cur.getValue());
        }
        return run(builder.toJobParameters());
    }

    public BatchStatus run(JobParameters jobParameters) {
        final String paramDescription = BatchUtils.prettyString(jobParameters);
        try {
            final JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            final BatchStatus status = jobExecution.getStatus();
            log.info(TAG + "jobExecutionId={} executionStatus={} {}", jobExecution.getId(), status.name(), paramDescription);
            return status;
        } catch (JobExecutionAlreadyRunningException e) {
            log.info(TAG + "running " + paramDescription);
            return BatchStatus.FAILED;
        } catch (JobInstanceAlreadyCompleteException e) {
            log.info(TAG + "completed " + paramDescription);
            return BatchStatus.COMPLETED;
        } catch (Exception e) {
            log.error(TAG + "failed: " + e.getMessage() + " " + paramDescription, e);
            return BatchStatus.FAILED;
        }
    }
}