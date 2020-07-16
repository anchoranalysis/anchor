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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.function.FunctionWithException;

/** Utilities functions for manipulating or creating {@link java.util.List} in a functional way */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FunctionalList {

    /**
     * Filters a collection and maps the result to a list
     *
     * <p>This function's purpose is mostly an convenience utility to make source-code easier to
     * read, as the paradigm below (although idiomatic) occurs in multiple places.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param predicate predicate to first filter the input collection before mapping
     * @param collection the collection to be filtered
     * @return a list with only the elements that pass the filter
     */
    public static <T> List<T> filterToList(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Maps a collection to a list with each element derived from a corresponding element in the
     * original collection.
     *
     * <p>This function's purpose is mostly an convenience utility to make source-code easier to
     * read, as the paradigm below (although very idiomatic) occurs frequently.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param collection the collection to be mapped
     * @param mapFunction function to do the mapping
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping
     */
    public static <S, T> List<T> mapToList(Collection<S> collection, Function<S, T> mapFunction) {
        return collection.stream().map(mapFunction).collect(Collectors.toList());
    }

    /**
     * Maps a collection to a list with each element in the original collection maybe producing an
     * element in the output
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param collection the collection to be mapped
     * @param mapFunction function to do the mapping to an Optional (the item is included in the
     *     output if the optional is defined)
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping
     */
    public static <S, T> List<T> mapToListOptional(
            Collection<S> collection, Function<S, Optional<T>> mapFunction) {
        return collection.stream()
                .map(mapFunction)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Flat-maps a collection to a list where in the original collection can produce many elements
     * in the outging list.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param collection the collection to be mapped
     * @param mapFunction function to do the mapping
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping
     */
    public static <S, T> List<T> flatMapToList(
            Collection<S> collection, Function<S, Stream<T>> mapFunction) {
        return collection.stream().flatMap(mapFunction).collect(Collectors.toList());
    }

    /**
     * Maps an array to a list with each element derived from a corresponding element in the
     * original array.
     *
     * <p>This function's purpose is mostly an convenience utility to make source-code easier to
     * read, as the paradigm below (although very idiomatic) occurs frequently.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param array the array to be mapped
     * @param mapFunction function to do the mapping
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping
     */
    public static <S, T> List<T> mapToList(S[] array, Function<S, T> mapFunction) {
        return Arrays.stream(array).map(mapFunction).collect(Collectors.toList());
    }

    /**
     * Like {@link #mapToList(Object[], Function)} but tolerates exceptions in the mapping function.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param  <E> exception that can be thrown by {code mapFunction}
     * @param collection the collection to be mapped
     * @param mapFunction function to do the mapping
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping
     * @throws E if the exception is thrown during mapping
     */
    public static <S, T, E extends Exception> List<T> mapToList(
            Collection<S> collection,
            Class<? extends Exception> throwableClass,
            FunctionWithException<S, T, E> mapFunction)
            throws E {
        return CheckedStream.mapWithException(collection.stream(), throwableClass, mapFunction)
                .collect(Collectors.toList());
    }
}
