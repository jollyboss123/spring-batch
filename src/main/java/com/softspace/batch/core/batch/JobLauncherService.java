package com.softspace.batch.core.batch;

import com.softspace.fasspos.common.batch.config.JobConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.support.ListPreparedStatementSetter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

/**
 * @author Jolly
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobLauncherService {

    private final JobLauncher jobLauncher;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    @Value("${local.file.directory}")
    private String localFileDirectory;

    public <I, O> void runJob(JobConfig<I, O> jobConfig) throws JobExecutionException {
        Job job = createJob(jobConfig);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(job, jobParameters);
    }

    private <I, O> Job createJob(JobConfig<I, O> jobConfig) {
        Step step = stepBuilderFactory.get(jobConfig.getStepName())
                .<I, O>chunk(100)
                .reader(reader(jobConfig.getJdbcReaderParams().getReaderParams(), jobConfig.getJdbcReaderParams().getSql(), jobConfig.getJdbcReaderParams().getRowMapper()))
                .processor(jobConfig.getProcessor())
                .writer(writer(jobConfig.getWriterParams().getFilename(), jobConfig.getWriterParams().getHeaderFieldMapper()))
                .build();

        return jobBuilderFactory.get(jobConfig.getJobName())
                .incrementer(new RunIdIncrementer())
                .start(closeExistingDBConnections())
                .next(step)
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

    private <I> ItemReader<I> reader(Map<String, Object> readerParams, String sql, RowMapper<I> rowMapper) {
        // Create and configure ItemReader<T> instance using the readerParams and sql
        JdbcCursorItemReader<I> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);

        log.debug("readerParams: {}", readerParams.toString());

        // Set SQL and named parameters
        String preparedSql = NamedParameterUtils.substituteNamedParameters(
                sql,
                new MapSqlParameterSource(readerParams));

        reader.setSql(preparedSql);
        reader.setPreparedStatementSetter(new ListPreparedStatementSetter(Arrays.asList(NamedParameterUtils.buildValueArray(sql, readerParams))));

        // Set the row mapper for the specific type
        reader.setRowMapper(rowMapper);

        return reader;
    }

    private <O> ItemWriter<O> writer(String filename, LinkedHashMap<String, Function<O, String>> headerFieldMapper) {

        String fileName = appendDateToFileName(filename);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = dtf.format(LocalDate.now());

        // Check if directory exists
        Path directoryPath = Paths.get(localFileDirectory, formattedDate);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                log.debug("Directory created: {}", directoryPath);
            } catch (IOException e) {
                log.error("Unable to create directory: {}", directoryPath, e);
                throw new RuntimeException("Unable to create directory: " + directoryPath, e);
            }
        }

        Path filePath = directoryPath.resolve(fileName);
        FlatFileItemWriter<O> writer = new FlatFileItemWriter<>();

        writer.setResource(new FileSystemResource(filePath.toFile()));
        writer.setLineAggregator(new DelimitedLineAggregator<O>() {{
            setDelimiter(",");
            setFieldExtractor(item -> headerFieldMapper.values().stream()
                    .map(fieldMapper -> fieldMapper.apply(item))
                    .toArray(String[]::new));
        }});
        writer.setHeaderCallback(itemWriter -> itemWriter.write(String.join(",", headerFieldMapper.keySet())));

        writer.setShouldDeleteIfExists(true);
        writer.setAppendAllowed(true);
        writer.setEncoding("UTF-8");
        writer.setForceSync(true);
        writer.setTransactional(false);

        return writer;
    }

    protected String appendDateToFileName(String fileName) {
        if (fileName != null && fileName.length() != 0) {
            String[] splitFileName = fileName.split("\\.");
            if (splitFileName.length > 1) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmm");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                return splitFileName[0] + "_" + formatter.format(new Date()) + "." + splitFileName[1];
            }
        }

        return fileName;
    }
}

