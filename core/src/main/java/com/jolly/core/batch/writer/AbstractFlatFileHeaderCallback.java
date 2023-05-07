package com.jolly.core.batch.writer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

/**
 * @author Jolly
 */
@Getter
@Setter
public abstract class AbstractFlatFileHeaderCallback implements FlatFileHeaderCallback {
    private String delimiter = ",";
    private ExecutionContext jobContext;
}
