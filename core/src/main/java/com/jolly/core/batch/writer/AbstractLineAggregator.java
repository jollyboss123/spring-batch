package com.jolly.core.batch.writer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.transform.LineAggregator;

/**
 * @author Jolly
 */
@Getter
@Setter
public abstract class AbstractLineAggregator<T> implements LineAggregator<T> {
    private String delimiter = ",";
    private ExecutionContext jobContext;
}
