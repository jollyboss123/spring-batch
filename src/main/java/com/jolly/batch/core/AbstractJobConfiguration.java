package com.jolly.batch.core;

import com.jolly.batch.core.listener.ProtocolListener;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author jolly
 */
public abstract class AbstractJobConfiguration {
    private final JobProperties props;
    @Autowired
    protected JobLauncher jobLauncher;
    @Autowired
    protected JobBuilderFactory jobBuilderFactory;
    @Autowired
    protected StepBuilderFactory stepBuilderFactory;
    @Autowired
    private TaskScheduler scheduler;
    @Autowired
    private ProtocolListener protocolListener;

    public AbstractJobConfiguration(JobProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void postConstruct() {
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

    public abstract Job job();

    public final JobBuilder createJob() {
        return jobBuilderFactory.get(props.getJobName());
    }

    public final JobTrigger createJobTrigger(Supplier<JobParameters> jobParametersSupplier) {
        Objects.requireNonNull(jobParametersSupplier);
        return new JobTrigger(jobLauncher, job(), jobParametersSupplier);
    }

    public final Step createStep(AbstractTasklet tasklet) {
        Objects.requireNonNull(tasklet);
        return stepBuilderFactory.get(tasklet.getName())
                .tasklet(tasklet)
                .build();
    }

    public final <I, O> Step createStep(AbstractBatchTask<I, O> task) {
        Objects.requireNonNull(task);
        SimpleStepBuilder<I, O> builder = stepBuilderFactory.get(task.getName())
                .<I, O>chunk(task.getCompletionPolicy())
                .reader(task.getItemReader())
                .processor(task.getItemProcessor())
                .writer(task.getItemWriter());

        builder.listener(protocolListener);

        for (StepListener listener : task.getStepListeners()) {
            if (listener instanceof ChunkListener) {
                ChunkListener chunkListener = (ChunkListener) listener;
                builder.listener(chunkListener);
            } else if (listener instanceof StepExecutionListener) {
                StepExecutionListener stepExecutionListener = (StepExecutionListener) listener;
                builder.listener(stepExecutionListener);
            } else if (listener instanceof ItemReadListener<?>) {
                @SuppressWarnings("unchecked")
                ItemReadListener<I> itemReadListener = (ItemReadListener<I>) listener;
                builder.listener(itemReadListener);
            } else if (listener instanceof ItemProcessListener<?, ?>) {
                @SuppressWarnings("unchecked")
                ItemProcessListener<I, O> itemProcessListener = (ItemProcessListener<I, O>) listener;
                builder.listener(itemProcessListener);
            } else if (listener instanceof ItemWriteListener<?>) {
                @SuppressWarnings("unchecked")
                ItemWriteListener<O> itemWriteListener = (ItemWriteListener<O>) listener;
                builder.listener(itemWriteListener);
            }
        }

        return builder.build();
    }

    public JobTrigger jobTrigger() {
        if (!props.isEnable()) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is not enabled");
        }

        return createJobTrigger(() -> new JobParametersBuilder()
                .addString("date", LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .toJobParameters());
    }
}
