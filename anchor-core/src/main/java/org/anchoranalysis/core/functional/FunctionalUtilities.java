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
	 * Performs a map on a stream, but accepts a function that can throw a checked-exception
	 * 
	 * <p>This uses some internal reflection trickery to suppress the checked exception, and then rethrow it.</p>
	 * 
	 * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be rethrown
	 * wrapped inside a {@link ConvertedToRuntimeException}</p>
	 * 
	 * @param <S> input-type to map
	 * @param <T> output-type of map
	 * @param <E> exception that can be thrown by {@link mapFunction}
	 * @param stream the stream to apply the map on
	 * @param throwableClass the class of the exception-type {@link E}
	 * @param mapFunction the function to use for mapping
	 * @return the output of the flatMap
	 * @throws E if the exception
	 */
	public static <S,T,E extends Throwable> Stream<T> mapWithException(
		Stream<S> stream,
		Class<?> throwableClass,
		FunctionWithException<S,T, E> mapFunction
	) throws E {
		try {
			return stream.map( item-> 
				suppressCheckedException(item, mapFunction)
			);
			
		} catch (ConvertedToRuntimeException e) {
			return throwException(e, throwableClass);
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
	 * @param <E> exception that can be thrown by {@link flatMapFunction}
	 * @param stream the stream to apply the flatMap on
	 * @param throwableClass the class of the exception-type {@link E}
	 * @param flatMapFunction the function to use for flatMapping
	 * @return the output of the flatMap
	 * @throws E if the exception
	 */
	public static <S,T,E extends Throwable> Stream<T> flatMapWithException(
		Stream<S> stream,
		Class<?> throwableClass,
		FunctionWithException<S, Collection<? extends T>, E> flatMapFunction
	) throws E {
		try {
			return stream.flatMap( item-> 
				suppressCheckedException(item, flatMapFunction).stream()
			);
			
		} catch (ConvertedToRuntimeException e) {
			return throwException(e, throwableClass);
		}
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Rethrows either the cause of a run-time exception (as a checked exception) or the run-time exception itself, depending if the cause matches the expected type. 
	 * 
	 * @param <T> return-type (nothing ever returned, this is just to keep types compatible in a nice way)
	 * @param <E> the exception type that may be the "cause" of the {@link ConvertedToRuntimeException}, in which case, it would be rethrown
	 * @param e the exception, which will be either rethrown as-is, or its cause will be rethrown.
	 * @param throwableClass a class to use to check if the cause matches the expected type (any class that is inheritable from this class will match)
	 * @return nothing, as an exception will always be thrown
	 * @throws E always, rethrowing either the run-time exception or its cause.
	 */
	private static <T, E extends Throwable> T throwException(ConvertedToRuntimeException e, Class<?> throwableClass) throws E {
		if (throwableClass.isAssignableFrom(e.getException().getClass())) {
			throw (E) e.getException();	
		} else {
			throw e;
		}
	}
	
	/**
	 * Catches any exceptions that occur around a function as it is executed and wraps them into a run-time exception.
	 * 
	 * @param <S> parameter-type for function
	 * @param <T> return-type for function
	 * @param <E> checked-exception that can be thrown by funcion
	 * @param param the parameter to apply to the funcion
	 * @param function the function
	 * @return the return-value of the function
	 * @throws ConvertedToRuntimeException a run-time exception if an exception is thrown by {@link function}
	 */
	private static <S,T,E extends Throwable> T suppressCheckedException(
		S param,
		FunctionWithException<S,T,E> function
	) {
		try {
			return function.apply(param);
		} catch (Throwable exc) {
			throw new ConvertedToRuntimeException(exc);
		}
	}
}
