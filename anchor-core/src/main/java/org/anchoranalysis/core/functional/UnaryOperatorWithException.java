package org.anchoranalysis.core.functional;

/**
 * Like a {@UnaryOperator} but allows an exception to be thrown
 * 
 * @author Owen Feehan
 *
 * @param <T> input and output-type of operator
 * @param <E> type of exception that may be thrown if something goes wrong
 */
@FunctionalInterface
public interface UnaryOperatorWithException<T,E extends Throwable> {
	T apply( T in ) throws E;
}
