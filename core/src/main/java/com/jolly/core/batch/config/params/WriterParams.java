package com.jolly.core.batch.config.params;

import com.softspace.fasspos.common.batch.writer.AbstractFlatFileHeaderCallback;
import com.softspace.fasspos.common.batch.writer.AbstractLineAggregator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

/**
 * @author Jolly
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class WriterParams<T> {
    @Builder.Default
    private String filename = "test.csv";
    private LinkedHashMap<String, Function<T, String>> headerFieldMapper;
    private Function<T, List<String>> fieldMapper;
    private String delimiter;
    private AbstractLineAggregator<T> lineAggregator;
    private AbstractFlatFileHeaderCallback flatFileHeaderCallback;
    private String headerSql;
}
