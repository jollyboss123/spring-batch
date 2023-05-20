package com.jolly.batch.core.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;

import java.util.Map;

/**
 * @author jolly
 */
@Slf4j
public class ProtocolListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {}

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String protocol = "step=" + stepExecution.getStepName() +
                " readCount=" + stepExecution.getReadCount() +
                " writeCount=" + stepExecution.getWriteCount() +
                " commits=" + stepExecution.getCommitCount() +
                " skipCount=" + stepExecution.getSkipCount() +
                " rollbacks=" + stepExecution.getRollbackCount() +
                " filter=" + stepExecution.getFilterCount();
        log.info(protocol);
        return null;
    }
}
