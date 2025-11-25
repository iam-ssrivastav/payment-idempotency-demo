package com.example.idempotency.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    /**
     * HTTP header name containing the idempotency key
     */
    String keyHeader() default "Idempotency-Key";

    /**
     * TTL for idempotency records in seconds (default: 24 hours)
     */
    long ttlSeconds() default 86400;
}
