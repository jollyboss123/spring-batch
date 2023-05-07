package com.jolly.core.job;

import org.springframework.batch.core.step.tasklet.Tasklet;

public abstract class AbstractTasklet implements Tasklet {
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
