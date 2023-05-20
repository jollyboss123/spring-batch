package com.jolly.batch.core;

import lombok.Getter;
import org.springframework.batch.core.StepListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;


/**
 * @author jolly
 */
@Getter
public abstract class AbstractBatchTask<I, O> {
    private CompletionPolicy completionPolicy;
    private ItemReader<I> itemReader;
    private ItemProcessor<I, O> itemProcessor;
    private ItemWriter<O> itemWriter;
    private StepListener[] stepListeners;
    private String name;

    public CompletionPolicy getCompletionPolicy() {
        return new SimpleCompletionPolicy(100);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public StepListener[] getStepListeners() {
        return new StepListener[0];
    }
}
