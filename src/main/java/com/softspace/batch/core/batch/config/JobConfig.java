package com.softspace.batch.core.batch.config;

import com.softspace.fasspos.common.batch.config.params.JdbcReaderParams;
import com.softspace.fasspos.common.batch.config.params.WriterParams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;

/**
 * @author Jolly
 */
@Getter
@Builder
@AllArgsConstructor
public class JobConfig<I, O> {
    public enum JobType {
        BACKOFFICE, BATCH
    }

    public enum ReaderType {
        JDBC_CURSOR, JPA_SPEC
    }

    @Builder.Default
    private String jobName = "defaultJobName";
    @Builder.Default
    private String stepName = "defaultStep";
    private JdbcReaderParams<I> jdbcReaderParams;
    private WriterParams<O> writerParams;
    private ItemProcessor<I, O> processor;
    private ItemProcessor<Map<String, Object>, O> dynamicProcessor;
    @NonNull
    private JobType jobType;
    @NonNull
    private ReaderType readerType;
}

