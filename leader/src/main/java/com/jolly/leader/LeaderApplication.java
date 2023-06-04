package com.jolly.leader;

import com.jolly.leader.step.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.jolly.leader.step.CsvToDbStepConfig.EMPTY_CSV_STATUS;

@SpringBootApplication
public class LeaderApplication {

    public static void main(String[] args) {
        System.setProperty("csvFile", "file:///Users/fusingvoon/IdeaProjects/batch/data/vgsales.csv");
        SpringApplication.run(LeaderApplication.class, args);
    }

    @Bean
    Job job(
            JobRepository jobRepository,
            ErrorStepConfig errorStepConfiguration,
            CsvToDbStepConfig csvToDbStepConfiguration,
            YearPlatformReportStepConfig yearPlatformReportStepConfiguration,
            YearReportStepConfig yearReportStepConfiguration,
            EndStepConfig endStepConfiguration) {
        var gameByYearStep = csvToDbStepConfiguration.gameByYearStep();
        return new JobBuilder("job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(gameByYearStep).on(EMPTY_CSV_STATUS).to(errorStepConfiguration.errorStep())
                .from(gameByYearStep).on("*").to(yearPlatformReportStepConfiguration.yearPlatformReportStep())
                .next(yearReportStepConfiguration.yearReportStep())
                .next(endStepConfiguration.endStep())
                .build()
                .build();

    }
}
