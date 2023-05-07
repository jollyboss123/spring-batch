package com.jolly.core.batch.config.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.jdbc.core.RowMapper;

import java.util.Map;

/**
 * @author Jolly
 */
@Getter
@Builder
@AllArgsConstructor
public class JdbcReaderParams<T> {
    private Map<String, Object> readerParams;
    private String sql;
    private RowMapper<T> rowMapper;
    private String columnsSql;
    private String columnsPlaceholder;
}
