package com.softspace.batch.core.batch.reader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Iterator;
import java.util.function.Function;

/**
 * @author Jolly
 */
@Getter
@AllArgsConstructor
public class JpaRepositoryItemReader<T, R> implements ItemReader<R> {

    @NonNull
    private final JpaSpecificationExecutor<T> repository;
    @NonNull
    private final Specification<T> specification;
    private final int pageSize;
    private final Function<T, R> transformer;

    private int currentPage;
    private Iterator<R> dataIterator;

    public JpaRepositoryItemReader(@NonNull JpaSpecificationExecutor<T> repository, @NonNull Specification<T> specification, int pageSize, Function<T, R> transformer) {
        this.repository = repository;
        this.specification = specification;
        this.pageSize = pageSize;
        this.currentPage = 0;
        this.transformer = transformer;
    }

    @Override
    public R read() {
        if (dataIterator == null || !dataIterator.hasNext()) {
            Page<T> page = repository.findAll(specification, PageRequest.of(currentPage++, pageSize));
            if (transformer != null) {
                dataIterator = page.stream().map(transformer).distinct().iterator();
            }
            if (!dataIterator.hasNext()) {
                return null;
            }
        }
        return dataIterator.next();
    }
}
