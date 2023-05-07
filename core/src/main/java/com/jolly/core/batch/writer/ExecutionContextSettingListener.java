package com.jolly.core.batch.writer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

/**
 * @author Jolly
 */
@AllArgsConstructor
@Component
@Getter
public class ExecutionContextSettingListener implements StepExecutionListener {
    private final CsvItemWriter writer;
    private final AbstractFlatFileHeaderCallback customHeaderCallback;
    private final AbstractLineAggregator customLineAggregator;
    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        writer.setJobExecution(jobExecution);
        customHeaderCallback.setJobContext(jobExecution.getExecutionContext());
        customLineAggregator.setJobContext(jobExecution.getExecutionContext());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
