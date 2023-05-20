package com.jolly.batch.core.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;

import java.util.Map;

/**
 * @author jolly
 */
@Slf4j
public class ProtocolListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        StringBuilder protocol = new StringBuilder();
        protocol.append("Protocol for ").append(jobExecution.getJobInstance().getJobName()).append("\n");
        protocol.append("  Started  : ").append(jobExecution.getStartTime()).append("\n");
        protocol.append("  Finished : ").append(jobExecution.getEndTime()).append("\n");
        protocol.append("  Exit-Code: ").append(jobExecution.getExitStatus().getExitCode()).append("\n");
        protocol.append("  Exit-Desc: ").append(jobExecution.getExitStatus().getExitDescription()).append("\n");
        protocol.append("  Status   : ").append(jobExecution.getStatus()).append("\n");

        protocol.append("Job-Parameter: \n");
        JobParameters jp = jobExecution.getJobParameters();
        for (Map.Entry<String, JobParameter> entry : jp.getParameters().entrySet()) {
            protocol.append("  ").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            protocol.append("Step ").append(stepExecution.getStepName()).append("\n");
            protocol.append("  ReadCount : ").append(stepExecution.getReadCount()).append("\n");
            protocol.append("  WriteCount: ").append(stepExecution.getWriteCount()).append("\n");
            protocol.append("  Commits   : ").append(stepExecution.getCommitCount()).append("\n");
            protocol.append("  SkipCount : ").append(stepExecution.getSkipCount()).append("\n");
            protocol.append("  Rollbacks : ").append(stepExecution.getRollbackCount()).append("\n");
            protocol.append("  Filter    : ").append(stepExecution.getFilterCount()).append("\n");
        }
        log.info(protocol.toString());
    }
}
