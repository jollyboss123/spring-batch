package com.jolly.core.batch.writer;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Jolly
 */
@Slf4j
@Component
public class CsvItemWriter<T> implements ItemWriter<T> {
    private String filename;
    @Value("${local.file.directory}")
    private String localFileDirectory;
    private JobExecution jobExecution;
    private LineAggregator<T> lineAggregator;
    private FlatFileHeaderCallback flatFileHeaderCallback;

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setJobExecution(JobExecution jobExecution) {
        this.jobExecution = jobExecution;
    }

    public void setLineAggregator(LineAggregator<T> lineAggregator) {
        this.lineAggregator = lineAggregator;
    }

    public void setFlatFileHeaderCallback(FlatFileHeaderCallback flatFileHeaderCallback) {
        this.flatFileHeaderCallback = flatFileHeaderCallback;
    }

    private ItemWriter<T> writer(JobExecution jobExecution) {
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
        FlatFileItemWriter<T> writer = new FlatFileItemWriter<>();

        writer.setResource(new FileSystemResource(filePath.toFile()));
        writer.setLineAggregator(lineAggregator);
        writer.setHeaderCallback(flatFileHeaderCallback);

        writer.setShouldDeleteIfExists(true);
        writer.setAppendAllowed(true);
        writer.setEncoding("UTF-8");
        writer.setForceSync(true);
        writer.setTransactional(false);

        return writer;
    }
    @Override
    public void write(@NonNull List<? extends T> list) throws Exception {
        ItemWriter<T> fileWriter = writer(jobExecution);

        // Open the writer before writing
        ExecutionContext executionContext = new ExecutionContext();
        ((FlatFileItemWriter<T>) fileWriter).open(executionContext);

        fileWriter.write(list);

        // Close the writer after writing
        ((FlatFileItemWriter<T>) fileWriter).close();
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
