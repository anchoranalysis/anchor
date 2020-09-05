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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.function.CheckedFunction;

/** Utilities functions for manipulating or creating {@link java.util.List} in a functional way */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FunctionalList {

    /**
     * Creates a list from a stream.
     *
     * <p>This function's purpose is mostly an convenience utility to make source-code easier to
     * read, as the paradigm below (although very idiomatic) occurs frequently.
     *
     * @param <T> item-type
     * @param stream the stream to create the list from
     * @return the created list.
     */
    public static <T> List<T> of(Stream<T> stream) {
        return stream.collect(Collectors.toList());
    }

    /**
     * Maps a stream to a list with each element derived from a corresponding element in the
     * original collection.
     *
     * <p>This function's purpose is mostly an convenience utility to make source-code easier to
     * read, as the paradigm below (although very idiomatic) occurs frequently.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param stream the stream to be mapped
     * @param mapFunction function to do the mapping
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping
     */
    public static <S, T> List<T> mapToList(Stream<S> stream, Function<S, T> mapFunction) {
        return stream.map(mapFunction).collect(Collectors.toList());
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
        return mapToList(collection.stream(), mapFunction);
    }

    /**
     * Maps a collection to a list with each element derived from a corresponding element in the
     * original collection - and also letting the map function use an index.
     *
     * @param <S> parameter-type for function
     * @param <T> return-type for function
     * @param list the list to be mapped
     * @param mapFunction function to do the mapping
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping
     */
    public static <S, T> List<T> mapToListWithIndex(
            List<S> list, BiFunction<S, Integer, T> mapFunction) {
        return IntStream.range(0, list.size())
                .mapToObj(index -> mapFunction.apply(list.get(index), index))
                .collect(Collectors.toList());
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
            CheckedFunction<S, T, E> mapFunction)
            throws E {
        return mapToList(collection.stream(), throwableClass, mapFunction);
    }

    /**
     * Like {@link #mapToList(Object[], Function)} but tolerates exceptions in the mapping function.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param  <E> exception that can be thrown by {code mapFunction}
     * @param array the array to be mapped
     * @param mapFunction function to do the mapping
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping
     * @throws E if the exception is thrown during mapping
     */
    public static <S, T, E extends Exception> List<T> mapToList(
            S[] array,
            Class<? extends Exception> throwableClass,
            CheckedFunction<S, T, E> mapFunction)
            throws E {
        return mapToList(Arrays.stream(array), throwableClass, mapFunction);
    }

    /**
     * Filters a collection and maps the result to a list
     *
     * <p>This function's purpose is mostly an convenience utility to make source-code easier to
     * read, as the paradigm below (although idiomatic) occurs in multiple places.
     *
     * @param <T> list item-type
     * @param predicate predicate to first filter the input collection before mapping
     * @param collection the collection to be filtered
     * @return a list with only the elements that pass the filter
     */
    public static <T> List<T> filterToList(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }

    private static <S, T, E extends Exception> List<T> mapToList(
            Stream<S> stream,
            Class<? extends Exception> throwableClass,
            CheckedFunction<S, T, E> mapFunction)
            throws E {
        return CheckedStream.map(stream, throwableClass, mapFunction).collect(Collectors.toList());
    }
}
