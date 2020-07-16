/* (C)2020 */
package org.anchoranalysis.core.progress;

/**
 * @author Owen Feehan
 * @param <R> result-type
 * @param <E> exception throw if operation fails
 */
@FunctionalInterface
public interface OperationWithProgressReporter<R, E extends Exception> {

    R doOperation(ProgressReporter progressReporter) throws E;
}
