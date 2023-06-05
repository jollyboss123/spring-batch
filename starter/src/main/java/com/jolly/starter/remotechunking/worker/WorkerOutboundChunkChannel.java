package com.jolly.starter.remotechunking.worker;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author jolly
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier
public @interface WorkerOutboundChunkChannel {
    @AliasFor(annotation = Qualifier.class)
    String value() default "";
}
