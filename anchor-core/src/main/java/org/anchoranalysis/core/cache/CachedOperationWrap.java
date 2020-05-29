package org.anchoranalysis.core.cache;

/**
 * A helper class to defined a {@link CachedOperation} using a functional.
 * 
 * <p>As {@link CachedOperation} is an abstract-base-class, it can otherwise be cumbersome to quickly override.</p>
 * 
 * @author Owen Feehan
 *
 * @param <R>
 * @param <E>
 */
public class CachedOperationWrap<R, E extends Throwable> extends CachedOperation<R,E> {

	/** A functional to be wrapped */
	@FunctionalInterface
	public interface WrapFunctional<T, E extends Throwable> {
		T apply() throws E;
	}
	
	private WrapFunctional<R,E> delegate;
	
	public <S> CachedOperationWrap(WrapFunctional<R,E> delegate) {
		super();
		this.delegate = delegate;
	}
	
	@Override
	protected R execute() throws E {
		return delegate.apply();
	}
}
