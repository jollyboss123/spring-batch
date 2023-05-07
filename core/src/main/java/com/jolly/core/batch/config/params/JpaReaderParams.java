package com.jolly.core.batch.config.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.function.Function;

/**
 * @author Jolly
 */
@Getter
@Builder
@AllArgsConstructor
public class JpaReaderParams<I, U> {
    private JpaSpecificationExecutor<I> repository;
    private Specification<I> specification;
    private int pageSize;
    private Function<I, U> transformer;
}
