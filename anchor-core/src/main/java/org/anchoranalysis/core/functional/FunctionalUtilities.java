package org.anchoranalysis.core.functional;

import java.util.ArrayList;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.progress.ProgressReporter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class FunctionalUtilities {
	
	/** An exception that wraps another exception, but exposes itself as a RuntimeException */
	public static class ConvertedToRuntimeException extends AnchorFriendlyRuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private final Throwable exception;
		
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
	 * @throws E if the exception is thrown during mapping
	 */
	public static <S,T,E extends Exception> Stream<T> mapWithException(
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
	 * Maps a list to new list, updating a progress-reporter for every element
	 * 
	 * @param <S> input-type to map
	 * @param <T> output-type of map
	 * @param <E> exception that can be thrown by {@link mapFunction}
	 * @param list the list to map
	 * @param progressReporter the progress-reporter to update
	 * @param mapFunction the function to use for mapping
	 * @return a newly-created list with the result of each mapped item
	 * @throws E if the exception is thrown during mapping 
	 */
	public static <S,T,E extends Exception> List<T> mapListWithProgress(
		List<S> list,
		ProgressReporter progressReporter,
		FunctionWithException<S,T,E> mapFunction
	) throws E {
		List<T> listOut = new ArrayList<>();
		
		progressReporter.setMin( 0 );
		progressReporter.setMax( list.size() );
		progressReporter.open();
		
		try {
			for(int i=0; i<list.size(); i++) {
				
				S item = list.get(i);
					
				listOut.add(
					mapFunction.apply(item)
				);
				
				progressReporter.update(i+1);
			}
			return listOut;
			
		} finally {
			progressReporter.close();
		}
	}
	
	
	/**
	 * Maps a list to a new list, including only certain items, updating a progress-reporter for every element
	 * 
	 * <p>Items where the mapping returns {@link Optional.empty()} are not included in the outputted list.</p>
	 * 
	 * @param <S> input-type to map
	 * @param <T> output-type of map
	 * @param <E> exception that can be thrown by {@link mapFunction}
	 * @param list the list to map
	 * @param progressReporter the progress-reporter to update
	 * @param mapFunction the function to use for mapping
	 * @return a newly-created list with the result of each mapped item
	 * @throws E if the exception is thrown during mapping 
	 */
	public static <S,T,E extends Exception> List<T> mapListOptionalWithProgress(
		List<S> list,
		ProgressReporter progressReporter,
		FunctionWithException<S,Optional<T>,E> mapFunction
	) throws E {
		List<T> listOut = new ArrayList<>();
		
		progressReporter.setMin( 0 );
		progressReporter.setMax( list.size() );
		progressReporter.open();
		
		try {
			for(int i=0; i<list.size(); i++) {
				
				S item = list.get(i);
				mapFunction.apply(item).ifPresent(listOut::add);
				progressReporter.update(i+1);
			}
			return listOut;
			
		} finally {
			progressReporter.close();
		}
	}
	
	/**
	 * Creates a new feature-list by mapping integers (from a range) each to a {@link Optional<Feature<T>>} accepting a checked-exception
	 * 
	 * <p>This uses some internal reflection trickery to suppress the checked exception, and then rethrow it.</p>
	 * 
	 * @param <T> end-type for mapping
	 * @param <E> an exception that be thrown during mapping
	 * @param stream stream of ints
	 * @param throwableClass the class of the exception-type {@link E}
	 * @param mapFunc function for mapping
	 * @return the stream after the mapping
	 */
	public static <T, E extends Exception> Stream<T> mapIntStreamWithException(
		IntStream stream,
		Class<?> throwableClass,
		IntFunctionWithException<T,E> mapFunc
	) throws E {
		try {
			return stream.mapToObj( index->
				suppressCheckedException(index, mapFunc)
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
	public static <S,T,E extends Exception> Stream<T> flatMapWithException(
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
	private static <T, E extends Exception> T throwException(ConvertedToRuntimeException e, Class<?> throwableClass) throws E {
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
	private static <S,T,E extends Exception> T suppressCheckedException(
		S param,
		FunctionWithException<S,T,E> function
	) {
		try {
			return function.apply(param);
		} catch (Exception exc) {
			throw new ConvertedToRuntimeException(exc);
		}
	}
	
	private static <T,E extends Exception> T suppressCheckedException(
		int param,
		IntFunctionWithException<T,E> function
	) {
		try {
			return function.apply(param);
		} catch (Exception exc) {
			throw new ConvertedToRuntimeException(exc);
		}
	}
}
