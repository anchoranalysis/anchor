/* (C)2020 */
package org.anchoranalysis.core.functional;

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
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.functional.function.IntFunctionWithException;
import org.anchoranalysis.core.functional.function.ToIntFunctionWithException;

/** Map operations for streams that can throw checked-exceptions */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckedStream {

    /** An exception that wraps another exception, but exposes itself as a RuntimeException */
    public static class ConvertedToRuntimeException extends AnchorFriendlyRuntimeException {

        /** */
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
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be
     * rethrown wrapped inside a {@link ConvertedToRuntimeException}.
     *
     * @param  <S> input-type to map
     * @param  <T> output-type of map
     * @param  <E> exception that can be thrown by {code mapFunction}
     * @param stream the stream to apply the map on
     * @param throwableClass the class of {@code E}
     * @param mapFunction the function to use for mapping
     * @return the output of the flatMap
     * @throws E if the exception is thrown during mapping
     */
    public static <S, T, E extends Exception> Stream<T> mapWithException(
            Stream<S> stream,
            Class<? extends Exception> throwableClass,
            FunctionWithException<S, T, E> mapFunction)
            throws E {
        try {
            return stream.map(item -> suppressCheckedException(item, mapFunction));

        } catch (ConvertedToRuntimeException e) {
            return throwException(e, throwableClass);
        }
    }

    /**
     * Performs a {@code mapToInt} on a stream, but accepts a function that can throw a
     * checked-exception.
     *
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be
     * rethrown wrapped inside a {@link ConvertedToRuntimeException}.
     *
     * @param  <S> input-type to map
     * @param  <E> exception that can be thrown by {code mapFunction}
     * @param stream the stream to apply the map on
     * @param throwableClass the class of {@code E}
     * @param mapFunction the function to use for mapping
     * @return the output of the flatMap
     * @throws E if the exception is thrown during mapping
     */
    public static <S, E extends Exception> IntStream mapToIntWithException(
            Stream<S> stream,
            Class<? extends Exception> throwableClass,
            ToIntFunctionWithException<S, E> mapFunction)
            throws E {
        try {
            return stream.mapToInt(item -> suppressCheckedException(item, mapFunction));

        } catch (ConvertedToRuntimeException e) {
            return throwException(e, throwableClass);
        }
    }

    /**
     * Creates a new feature-list by mapping integers (from a range) each to an optional feature
     * accepting a checked-exception
     *
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * @param <T> end-type for mapping
     * @param <E> an exception that be thrown during mapping
     * @param stream stream of ints
     * @param throwableClass the class of {@code E}
     * @param mapFunc function for mapping
     * @return the stream after the mapping
     */
    public static <T, E extends Exception> Stream<T> mapIntStreamWithException(
            IntStream stream,
            Class<? extends Exception> throwableClass,
            IntFunctionWithException<T, E> mapFunc)
            throws E {
        try {
            return stream.mapToObj(index -> suppressCheckedException(index, mapFunc));
        } catch (ConvertedToRuntimeException e) {
            return throwException(e, throwableClass);
        }
    }

    /**
     * Performs a {@code mapToObj} on an {@code IntStream} but accepts a function that can throw a
     * checked-exception.
     *
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be
     * rethrown wrapped inside a {@link ConvertedToRuntimeException}.
     *
     * @param  <T> object-type to map-to
     * @param  <E> exception that can be thrown by {code mapFunction}
     * @param stream the stream to apply the map on
     * @param throwableClass the class of {@code E}
     * @param mapFunction the function to use for mapping
     * @return the output of the flatMap
     * @throws E if the exception is thrown during mapping
     */
    public static <T, E extends Exception> Stream<T> mapToObjWithException(
            IntStream stream,
            Class<? extends Exception> throwableClass,
            IntFunctionWithException<T, E> mapFunction)
            throws E {
        try {
            return stream.mapToObj(item -> suppressCheckedException(item, mapFunction));

        } catch (ConvertedToRuntimeException e) {
            return throwException(e, throwableClass);
        }
    }

    /**
     * Performs a flat-map on a stream, but accepts a function that can throw a checked-exception
     *
     * <p>This uses some internal reflection trickery to suppress the checked exception, and then
     * rethrow it.
     *
     * <p>As a side-effect, any runtime exceptions that are thrown during the function, will be
     * rethrown wrapped inside a {@link ConvertedToRuntimeException}
     *
     * @param <S> input-type to flatMap
     * @param <T> output-type of flatMap
     * @param <E> exception that can be thrown by {@link flatMapFunction}
     * @param stream the stream to apply the flatMap on
     * @param throwableClass the class of {@code E}
     * @param flatMapFunction the function to use for flatMapping
     * @return the output of the flatMap
     * @throws E if the exception
     */
    public static <S, T, E extends Exception> Stream<T> flatMapWithException(
            Stream<S> stream,
            Class<? extends Exception> throwableClass,
            FunctionWithException<S, Collection<? extends T>, E> flatMapFunction)
            throws E {
        try {
            return stream.flatMap(item -> suppressCheckedException(item, flatMapFunction).stream());

        } catch (ConvertedToRuntimeException e) {
            return throwException(e, throwableClass);
        }
    }

    @SuppressWarnings("unchecked")
    /**
     * Rethrows either the cause of a run-time exception (as a checked exception) or the run-time
     * exception itself, depending if the cause matches the expected type.
     *
     * @param <T> return-type (nothing ever returned, this is just to keep types compatible in a
     *     nice way)
     * @param <E> the exception type that may be the "cause" of the {@link
     *     ConvertedToRuntimeException}, in which case, it would be rethrown
     * @param e the exception, which will be either rethrown as-is, or its cause will be rethrown.
     * @param throwableClass the class of {@code E}
     * @return nothing, as an exception will always be thrown
     * @throws E always, rethrowing either the run-time exception or its cause.
     */
    private static <T, E extends Exception> T throwException(
            ConvertedToRuntimeException e, Class<? extends Exception> throwableClass) throws E {
        if (throwableClass.isAssignableFrom(e.getException().getClass())) {
            throw (E) e.getException();
        } else {
            throw e;
        }
    }

    /**
     * Catches any exceptions that occur around a function as it is executed and wraps them into a
     * run-time exception.
     *
     * @param <S> parameter-type for function
     * @param <T> return-type for function
     * @param <E> checked-exception that can be thrown by function
     * @param param the parameter to apply to the function
     * @param function the function
     * @return the return-value of the function
     * @throws ConvertedToRuntimeException a run-time exception if an exception is thrown by {@link
     *     function}
     */
    private static <S, T, E extends Exception> T suppressCheckedException(
            S param, FunctionWithException<S, T, E> function) {
        try {
            return function.apply(param);
        } catch (Exception exc) {
            throw new ConvertedToRuntimeException(exc);
        }
    }

    /**
     * Like @link(#suppressCheckedException) but instead accepts {@link ToIntFunctionWithException}
     * functions
     */
    private static <S, E extends Exception> int suppressCheckedException(
            S param, ToIntFunctionWithException<S, E> function) {
        try {
            return function.apply(param);
        } catch (Exception exc) {
            throw new ConvertedToRuntimeException(exc);
        }
    }

    /**
     * Like @link(#suppressCheckedException) but instead accepts {@link IntFunctionWithException}
     * functions
     */
    private static <T, E extends Exception> T suppressCheckedException(
            int param, IntFunctionWithException<T, E> function) {
        try {
            return function.apply(param);
        } catch (Exception exc) {
            throw new ConvertedToRuntimeException(exc);
        }
    }
}
