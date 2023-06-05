package com.jolly.starter.remotechunking.leader;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author jolly
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier
public @interface LeaderInboundChunkChannel {
    @AliasFor(annotation = Qualifier.class)
    String value() default "";
}
