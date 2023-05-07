package com.jolly.core.util;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

import java.util.Iterator;
import java.util.Map;

public final class BatchUtils {

    public static Object getJobParameter(ChunkContext chunkContext, String key) {
        return getJobParameter(chunkContext.getStepContext(), key);
    }

    public static Object getJobParameter(StepContext stepContext, String key) {
        return stepContext.getJobParameters().get(key);
    }


    public static void putContextParameter(ChunkContext chunkContext, String key, Object value) {
        getExecutionContext(chunkContext).put(key, value);
    }

    public static void putContextParameter(StepContext stepContext, String key, Object value) {
        getExecutionContext(stepContext).put(key, value);
    }

    public static void putContextParameter(StepExecution stepExecution, String key, Object value) {
        getExecutionContext(stepExecution).put(key, value);
    }

    public static Object getContextParameter(ChunkContext chunkContext, String key) {
        return getExecutionContext(chunkContext).get(key);
    }

    public static Object getContextParameter(StepContext stepContext, String key) {
        return getExecutionContext(stepContext).get(key);
    }

    public static Object getContextParameter(StepExecution stepExecution, String key) {
        return getExecutionContext(stepExecution).get(key);
    }

    public static ExecutionContext getExecutionContext(ChunkContext chunkContext) {
        return getExecutionContext(chunkContext.getStepContext());
    }

    public static ExecutionContext getExecutionContext(StepContext stepContext) {
        return getExecutionContext(stepContext.getStepExecution());
    }

    public static ExecutionContext getExecutionContext(StepExecution stepExecution) {
        return stepExecution.getJobExecution().getExecutionContext();
    }

    private BatchUtils() {
    }

    public static String prettyString(JobParameters jobParameters) {
        if (jobParameters.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();

        final Iterator<Map.Entry<String, JobParameter>> it = jobParameters.getParameters().entrySet().iterator();
        append(sb, it.next());
        while (it.hasNext()) {
            sb.append(", ");
            append(sb, it.next());
        }
        return sb.toString();
    }

    private static void append(StringBuilder sb, Map.Entry<String, JobParameter> param) {
        final boolean optional = !param.getValue().isIdentifying();
        if (optional) {
            sb.append("[");
        }
        sb.append(param.getKey()).append("=").append(param.getValue().getValue());
        if (optional) {
            sb.append("]");
        }
    }
}
