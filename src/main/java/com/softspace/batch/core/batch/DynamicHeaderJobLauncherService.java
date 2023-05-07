package com.softspace.batch.core.batch;

import com.softspace.fasspos.common.batch.config.JobConfig;
import com.softspace.fasspos.common.batch.writer.*;
import com.softspace.fasspos.common.util.SqlQueryLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.support.ListPreparedStatementSetter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import javax.sql.DataSource;
import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Jolly
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicHeaderJobLauncherService {

    private final JobLauncher jobLauncher;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final Provider<CsvItemWriter> itemWriterProvider;
    private final SqlQueryLoader sqlQueryLoader;
    private final DynamicHeaderDeterminationTasklet headerDeterminationTasklet;

    public <I, O> void runJob(JobConfig<I, O> jobConfig) throws JobExecutionException {
        Job job = createJob(jobConfig);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(job, jobParameters);
    }

    private <I, O> Job createJob(JobConfig<I, O> jobConfig) {
        CsvItemWriter<O> writer = itemWriterProvider.get();
        headerDeterminationTasklet.setSql(jobConfig.getWriterParams().getHeaderSql());
        Step headerDeterminationStep = stepBuilderFactory.get("headerDeterminationStep")
                .tasklet(headerDeterminationTasklet)
                .build();

        jobConfig.getWriterParams().getFlatFileHeaderCallback().setDelimiter(jobConfig.getWriterParams().getDelimiter());
        jobConfig.getWriterParams().getLineAggregator().setDelimiter(jobConfig.getWriterParams().getDelimiter());

        writer.setFilename(jobConfig.getWriterParams().getFilename());
        writer.setFlatFileHeaderCallback(jobConfig.getWriterParams().getFlatFileHeaderCallback());
        writer.setLineAggregator(jobConfig.getWriterParams().getLineAggregator());
        Step etlStep = stepBuilderFactory.get(jobConfig.getStepName())
                .<Map<String, Object>, O>chunk(100)
                .reader(reader(jobConfig.getJdbcReaderParams().getReaderParams()))
                .processor(jobConfig.getDynamicProcessor())
                .writer(writer)
                .listener(new ExecutionContextSettingListener(writer,
                        jobConfig.getWriterParams().getFlatFileHeaderCallback(),
                        jobConfig.getWriterParams().getLineAggregator()))
                .build();

        return jobBuilderFactory.get(jobConfig.getJobName())
                .incrementer(new RunIdIncrementer())
                .start(closeExistingDBConnections())
                .next(headerDeterminationStep)
                .next(etlStep)
                .build();
    }

    private Step closeExistingDBConnections() {
        return stepBuilderFactory.get("closeExistingDBConnections")
                .tasklet(closeExistingDBConnectionsTasklet())
                .build();
    }

    private Tasklet closeExistingDBConnectionsTasklet() {
        return (contribution, chunkContext) -> {
            // Get the DataSource from the chunkContext
            DataSource dataSource = (DataSource) chunkContext.getStepContext().getJobExecutionContext().get("dataSource");

            // Close any open database connections
            if (dataSource instanceof Closeable) {
                ((Closeable) dataSource).close();
            }

            return RepeatStatus.FINISHED;
        };
    }

    private ItemReader<Map<String, Object>> reader(Map<String, Object> readerParams) {
        List<String> categories = jdbcTemplate.queryForList(sqlQueryLoader.getQuery("cashbag-report.sql", "getColumns"), String.class);
        String columns = String.join(",", categories);

        // Create and configure ItemReader<T> instance using the readerParams and sql
        JdbcCursorItemReader<Map<String, Object>> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);

        log.debug("readerParams: {}", readerParams.toString());
        String sql = sqlQueryLoader.getQuery("cashbag-report.sql", "generateCashBagReport")
                .replace("{columns}", columns);
        log.debug(sql);

        // Set SQL and named parameters
        String preparedSql = NamedParameterUtils.substituteNamedParameters(
                sql,
                new MapSqlParameterSource(readerParams)
        );

        reader.setSql(preparedSql);
        reader.setPreparedStatementSetter(new ListPreparedStatementSetter(Arrays.asList(NamedParameterUtils.buildValueArray(
                sql,
                readerParams))));

        // Set the row mapper for the specific type
        reader.setRowMapper(new ColumnMapRowMapper());
        return reader;
    }
}
