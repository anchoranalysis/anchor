package org.anchoranalysis.core.functional.function;

/**
 * Consumes a value and throws an exception
 *
 * @author Owen Feehan
 * @param <S> source-type
 * @param <E> exception that can be thrown during apply
 */
@FunctionalInterface
public interface CheckedConsumer<S, E extends Exception> {
    void accept(S in) throws E;
}