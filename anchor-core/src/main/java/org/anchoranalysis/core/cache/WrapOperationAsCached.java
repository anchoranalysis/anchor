package org.anchoranalysis.core.cache;


/**
 * Given an {@link Operation}, it wraps it to make it a cached-operation
 * 
 * @author Owen Feehan
 *
 * @param <T> return-type of operation
 * @param <E> exception that is thrown if something goes wrong during execution
 */
public class WrapOperationAsCached<T, E extends Throwable> extends CachedOperation<T, E> {

	private Operation<T, E> operation;
	
	public WrapOperationAsCached(Operation<T, E> operation) {
		super();
		this.operation = operation;
	}

	@Override
	protected T execute() throws E {
		return operation.doOperation(); 
	}

}
