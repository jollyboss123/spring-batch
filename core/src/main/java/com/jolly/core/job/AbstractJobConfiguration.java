package com.jolly.core.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractJobConfiguration {

    private final JobProperties props;

    @Autowired
    protected JobLauncher jobLauncher;

    @Autowired
    protected JobBuilderFactory jobBuilderFactory;

    @Autowired
    protected StepBuilderFactory stepBuilderFactory;

    @Autowired
    protected FailRetryListener failRetryListener;

    @Autowired
    private TaskScheduler scheduler;

    public AbstractJobConfiguration(JobProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void postConstruct() {
        // if not enable. just ignore cronjob registration and trigger on start
        if (!props.isEnable()) {
            return;
        }
        if (StringUtils.hasText(props.getCron())) {
            scheduler.schedule(() -> jobTrigger().run(), new CronTrigger(props.getCron()));
        }
        if (props.isTriggerOnStart()) {
            jobTrigger().run();
        }
    }

    /**
     * job definition
     *
     * @return
     */
    public abstract Job job();

    public final JobBuilder createJob() {
        return jobBuilderFactory.get(props.getJobName());
    }

    /**
     * @param jobParametersSupplier defaultValueProvider if
     * @return
     */
    public final JobTrigger createJobTrigger(Supplier<JobParameters> jobParametersSupplier) {
        Objects.requireNonNull(jobParametersSupplier);
        return new JobTrigger(jobLauncher, job(), jobParametersSupplier);
    }

    public final Step createStep(AbstractTasklet tasklet) {
        Objects.requireNonNull(tasklet);
        return stepBuilderFactory.get(tasklet.getName()).tasklet(tasklet).build();
    }

    /**
     * create a executable jobTrigger for yesterday date.
     *
     * @return
     */
    public JobTrigger jobTrigger() {
        if (!props.isEnable()) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is not enabled.");
        }
        return createJobTrigger(() -> new JobParametersBuilder()
                .addString("date", LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .toJobParameters());
    }
}
