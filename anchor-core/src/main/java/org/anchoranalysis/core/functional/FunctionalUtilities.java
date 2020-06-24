package org.anchoranalysis.core.functional;

import java.util.Collection;
import java.util.stream.Stream;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

public class FunctionalUtilities {

	private FunctionalUtilities() {}
	
	/** An exception that wraps another exception, but exposes itself as a RuntimeException */
	public static class ConvertedToRuntimeException extends AnchorFriendlyRuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private Throwable exception;
		
		public ConvertedToRuntimeException(Throwable exception) {
			super(exception);
			this.exception = exception;
		}

		public Throwable getException() {
			return exception;
		}
	}
	
	/**
	 * Performs a flat-map on a stream, but accepts a function that can throw a checked-exception
	 * 
	 * <p>This uses some internal reflection trickery to suppress the checked exception, and then rethrow it.</p>
	 * 
	 * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be rethrown
	 * wrapped inside a {@link ConvertedToRuntimeException}</p>
	 * 
	 * @param <S> input-type to flatMap
	 * @param <T> output-type of flatMap
	 * @param <E> exception that can be thrown by <code>func</code>
	 * @param stream the stream to apply the flatMap on
	 * @param throwableClass the class of the exception-type <code>E</code>
	 * @param func the function to use for flatMapping
	 * @return the output of the flatMap
	 * @throws E if the exception
	 */
	@SuppressWarnings("unchecked")
	public static <S,T,E extends Throwable> Stream<T> flatMapWithException(
		Stream<S> stream,
		Class<?> throwableClass,
		FunctionWithException<S, Collection<? extends T>, E> func
	) throws E {
		try {
			return stream.flatMap( item-> 
				suppressCheckedException(item, func).stream()
			);
			
		} catch (ConvertedToRuntimeException e) {
			if (throwableClass.isAssignableFrom(e.getException().getClass())) {
				throw (E) e.getException();	
			} else {
				throw e;
			}
		}
	}
	
	private static <S,T,E extends Throwable> T suppressCheckedException(
		S item,
		FunctionWithException<S,T, E> func
	) {
		try {
			return func.apply(item);
		} catch (Throwable exc) {
			throw new ConvertedToRuntimeException(exc);
		}
	}
}
