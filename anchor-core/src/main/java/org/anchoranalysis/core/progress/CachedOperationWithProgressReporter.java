package org.anchoranalysis.core.progress;

import org.anchoranalysis.core.functional.Operation;

/**
 * 
 * @author Owen Feehan
 *
 * @param <R> result-type
 * @param <E> exception thrown during operation
 */
public abstract class CachedOperationWithProgressReporter<R, E extends Throwable> implements OperationWithProgressReporter<R,E>, Operation<R,E> {

	private R result = null;
	private boolean done = false;
	
	@Override
	public synchronized R doOperation( ProgressReporter progressReporter ) throws E {
		
		if (!done) {
			result = execute( progressReporter );
			done = true;
		}
		
		return result;
	}
	
	public synchronized void assignFrom( CachedOperationWithProgressReporter<R,E> src ) {
		this.result = src.result;
		this.done = src.done;
	}
	
	public synchronized void reset() {
		done = false;
		result = null;
	}
	
	public synchronized boolean isDone() {
		return done;
	}
	
	protected abstract R execute( ProgressReporter progressReporter ) throws E;

	protected synchronized R getResult() {
		return result;
	}

	@Override
	public R doOperation() throws E {
		return doOperation( ProgressReporterNull.get() );
	}
}
