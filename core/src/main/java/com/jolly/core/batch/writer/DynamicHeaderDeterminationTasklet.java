package com.jolly.core.batch.writer;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Jolly
 */
@Component
public class DynamicHeaderDeterminationTasklet implements Tasklet {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private String sql;

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        List<String> headers = jdbcTemplate.queryForList(sql, String.class);

        //TODO: change to for loop in case more than 1 dynamic header
        List<String> combinedHeaders = headers;

        ExecutionContext jobContext = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();
        jobContext.put("headers", combinedHeaders);
        return RepeatStatus.FINISHED;
    }
}
