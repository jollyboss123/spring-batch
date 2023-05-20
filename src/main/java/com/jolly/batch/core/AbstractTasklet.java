package com.jolly.batch.core;

import org.springframework.batch.core.step.tasklet.Tasklet;

/**
 * @author jolly
 */
public abstract class AbstractTasklet implements Tasklet {
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
