package com.phonecompany.model.proxy;

import org.springframework.core.GenericTypeResolver;

import java.sql.ResultSet;
import java.util.function.Function;

/**
 * Defines a basic structure for the class used to provide
 * mappers for the {@code DynamicProxy} instances
 *
 * @param <T> type of the entity to be mapped
 */
public abstract class SourceMapper<S, T> {

    private Class<T> proxyTargetType;

    /**
     * As soon as Java does not allow type parameters to
     * be easily obtained (due to type erasure) constructor
     * writes a generic type parameter to the private field
     * of the class enabling it to be returned by the
     * {@code getType()} method
     *
     * @see GenericTypeResolver
     */
    public SourceMapper(Class<T> proxyTargetType) {
        this.proxyTargetType = proxyTargetType;
    }

    public Class<?> getType() {
        return this.proxyTargetType;
    }

    public abstract Function<S, T> getMapper();
}

