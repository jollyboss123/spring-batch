package com.jolly.leader.step;

import com.jolly.leader.domain.GameByYear;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author jolly
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class CsvToDbStepConfig {
    private final DataSource dataSource;
    private final Resource resource;
    private final JdbcTemplate jdbcTemplate;
    private final JobRepository repository;
    private final PlatformTransactionManager transactionManager;
    public static final String EMPTY_CSV_STATUS = "EMPTY";

    @Bean
    @StepScope
    FlatFileItemReader<GameByYear> gameByYearReader() {
        return new FlatFileItemReaderBuilder<GameByYear>()
                .resource(resource)
                .name("item.reader.game-by-year")
                .delimited().delimiter(",")
                .names("rank,name,platform,year,genre,publisher,na,eu,jp,other,global".split(","))
                .linesToSkip(1)
                .fieldSetMapper(fieldSet -> new GameByYear(
                        fieldSet.readInt("rank"),
                        fieldSet.readString("name"),
                        fieldSet.readString("platform"),
                        parseYearToInt(fieldSet.readString("year")),
                        fieldSet.readString("genre"),
                        fieldSet.readString("publisher"),
                        fieldSet.readFloat("na"),
                        fieldSet.readFloat("eu"),
                        fieldSet.readFloat("jp"),
                        fieldSet.readFloat("other"),
                        fieldSet.readFloat("global")
                ))
                .build();
    }

    @Bean
    JdbcBatchItemWriter<GameByYear> gameByYearWriter() {
        String sql = """
                insert into video_game_sales(
                    rank          ,
                    name          ,
                    platform      ,
                    year          ,
                    genre         ,
                    publisher     ,
                    na_sales      ,
                    eu_sales      ,
                    jp_sales      ,
                    other_sales   ,
                    global_sales
                )
                 values (
                    :rank,        
                    :name,        
                    :platform,    
                    :year,        
                    :genre,       
                    :publisher,   
                    :na_sales,    
                    :eu_sales,    
                    :jp_sales,    
                    :other_sales, 
                    :global_sales
                 ) 
                 ON CONFLICT ON CONSTRAINT video_game_sales_name_platform_year_genre_key  
                 DO UPDATE  
                 SET 
                    rank=excluded.rank ,        
                    na_sales=excluded.na_sales ,    
                    eu_sales=excluded.eu_sales ,    
                    jp_sales=excluded.jp_sales ,    
                    other_sales=excluded.other_sales , 
                    global_sales=excluded.global_sales
                 ; 
                """;
        return new JdbcBatchItemWriterBuilder<GameByYear>()
                .sql(sql)
                .dataSource(this.dataSource)
                .itemSqlParameterSourceProvider(item -> {
                    Map<String, Object> map = new HashMap<>() {{
                        put("rank", item.rank());
                        put("name", item.name().trim());
                        put("platform", item.platform().trim());
                        put("year", item.year());
                        put("genre", item.genre().trim());
                        put("publisher", item.publisher().trim());
                        put("na_sales", item.na());
                        put("eu_sales", item.eu());
                        put("jp_sales", item.jp());
                        put("other_sales", item.other());
                        put("global_sales", item.global());
                    }};
                    return new MapSqlParameterSource(map);
                })
                .build();
    }

    @Bean
    public Step gameByYearStep() {
        return new StepBuilder("csv-to-db", repository)
                .<GameByYear, GameByYear>chunk(100, transactionManager)
                .reader(gameByYearReader())
                .writer(gameByYearWriter())
                .listener(new StepExecutionListener() {
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        Integer count = Objects.requireNonNull(
                                jdbcTemplate.queryForObject("select coalesce(count(*) ,0) from video_game_sales", Integer.class)
                        );
                        ExitStatus status = count == 0 ? new ExitStatus(EMPTY_CSV_STATUS) : ExitStatus.COMPLETED;
                        log.info("the status is {}", status);
                        return status;
                    }
                })
                .build();
    }

    private static int parseYearToInt(String year) {
        if (year != null &&
        !year.contains("NA") &&
        !year.contains("N/A")) return Integer.parseInt(year);
        return 0;
    }
}
