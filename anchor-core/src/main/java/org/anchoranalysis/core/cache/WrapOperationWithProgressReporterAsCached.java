package org.anchoranalysis.core.cache;

import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;

/**
 * Given an {@link Operation}, it wraps it to make it a cached-operation with progress-reporter
 * 
 * @author Owen Feehan
 *
 * @param <T> return-type of operation
 * @param <E> exception that is thrown if something goes wrong during execution
 */
public class WrapOperationWithProgressReporterAsCached<T, E extends Throwable> extends CachedOperationWithProgressReporter<T, E> {

	private OperationWithProgressReporter<T, E> operation;
	
	public WrapOperationWithProgressReporterAsCached(Operation<T, E> operation) {
		this(
			progressReporter -> operation.doOperation()
		);
	}
	
	public WrapOperationWithProgressReporterAsCached(OperationWithProgressReporter<T, E> operation) {
		super();
		this.operation = operation;
	}

	@Override
	protected T execute(ProgressReporter progressReporter) throws E {
		return operation.doOperation(progressReporter); 
	}

}
