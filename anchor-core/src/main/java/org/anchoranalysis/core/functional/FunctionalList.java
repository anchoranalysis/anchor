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

import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.functional.checked.CheckedFunctionWithInt;
import org.anchoranalysis.core.functional.checked.CheckedPredicate;
import org.anchoranalysis.core.functional.unchecked.FunctionWithInt;
import org.apache.commons.lang3.tuple.Pair;

/** Utility functions for manipulating or creating {@link java.util.List} in a functional way. */
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
     * @param collection the collection to be mapped.
     * @param mapFunction function to do the mapping.
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
     */
    public static <S, T> List<T> mapToList(Collection<S> collection, Function<S, T> mapFunction) {
        return mapToList(collection.stream(), mapFunction);
    }

    /**
     * Maps a collection to a list with each element derived from a corresponding element in the
     * original collection, <b>as well as a corresponding argument from a second list</b>.
     *
     * <p>This function's purpose is mostly an convenience utility to make source-code easier to
     * read, as the paradigm below (although very idiomatic) occurs frequently.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param  <U> type of second argument for function
     * @param collection the collection to be mapped.
     * @param arguments the argument to use together with element in {@code collection}.
     * @param mapFunction function to do the mapping.
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
     */
    public static <S, T, U> List<T> mapToList(
            Collection<S> collection, Collection<U> arguments, BiFunction<S, U, T> mapFunction) {
        checkCollectionSize(collection, arguments);
        List<Pair<S, U>> combined = zip(collection, arguments);
        return mapToList(combined, pair -> mapFunction.apply(pair.getLeft(), pair.getRight()));
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
     * @param array the array to be mapped.
     * @param mapFunction function to do the mapping.
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
     */
    public static <S, T> List<T> mapToList(S[] array, Function<S, T> mapFunction) {
        return Arrays.stream(array).map(mapFunction).collect(Collectors.toList());
    }

    /**
     * Like {@link #mapToList(Object[], Function)} but tolerates exceptions in the mapping function.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param  <E> exception that can be thrown by @{@code mapFunction}
     * @param collection the collection to be mapped
     * @param throwableClass class type of exception that may be thrown by {@code mapFunction}
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
     * @param  <E> exception that can be thrown by {@code mapFunction}
     * @param array the array to be mapped
     * @param throwableClass class type of exception that may be thrown by {@code mapFunction}.
     * @param mapFunction function to do the mapping.
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
     * @throws E if the exception is thrown during mapping.
     */
    public static <S, T, E extends Exception> List<T> mapToList(
            S[] array,
            Class<? extends Exception> throwableClass,
            CheckedFunction<S, T, E> mapFunction)
            throws E {
        return mapToList(Arrays.stream(array), throwableClass, mapFunction);
    }

    /**
     * Like {@link #mapToList(Stream, Function)} but tolerates exceptions in the mapping function.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param  <E> exception that can be thrown by @{@code mapFunction}
     * @param stream the stream to be mapped.
     * @param throwableClass class type of exception that may be thrown by {@code mapFunction}.
     * @param mapFunction function to do the mapping.
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
     * @throws E if the exception is thrown during mapping.
     */
    public static <S, T, E extends Exception> List<T> mapToList(
            Stream<S> stream,
            Class<? extends Exception> throwableClass,
            CheckedFunction<S, T, E> mapFunction)
            throws E {
        return CheckedStream.map(stream, throwableClass, mapFunction).collect(Collectors.toList());
    }

    /**
     * Maps a collection to a list with each element derived from a corresponding element in the
     * original collection - and also letting the map function use an index.
     *
     * @param <S> parameter-type for function
     * @param <T> return-type for function
     * @param list the list to be mapped.
     * @param mapFunction function to do the mapping.
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
     */
    public static <S, T> List<T> mapToListWithIndex(
            List<S> list, FunctionWithInt<S, T> mapFunction) {
        return IntStream.range(0, list.size())
                .mapToObj(index -> mapFunction.apply(list.get(index), index))
                .collect(Collectors.toList());
    }

    /**
     * Maps a collection to a list with each element derived from a corresponding element in the
     * original collection - and also letting the map function use an index.
     *
     * @param <S> parameter-type for function
     * @param <T> return-type for function
     * @param  <E> exception that can be thrown by @{@code mapFunction}
     * @param list the list to be mapped.
     * @param throwableClass class type of exception that may be thrown by {@code mapFunction}.
     * @param mapFunction function to do the mapping.
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
     * @throws E if the exception is thrown during mapping.
     */
    public static <S, T, E extends Exception> List<T> mapToListWithIndex(
            List<S> list,
            Class<? extends Exception> throwableClass,
            CheckedFunctionWithInt<S, T, E> mapFunction)
            throws E {
        return CheckedStream.mapToObj(
                        IntStream.range(0, list.size()),
                        throwableClass,
                        index -> mapFunction.apply(list.get(index), index))
                .collect(Collectors.toList());
    }

    /**
     * Maps a collection to a list with each element in the original collection maybe producing an
     * element in the output.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param collection the collection to be mapped.
     * @param mapFunction function to do the mapping to an Optional (the item is included in the
     *     output if the optional is defined).
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
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
     * Maps a collection to a list with each element in the original collection maybe producing an
     * element in the output.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param  <E> an exception that may be thrown by an {@code mapFunction}
     * @param collection the collection to be mapped.
     * @param throwableClass class type of exception that may be thrown by {@code mapFunction}.
     * @param mapFunction function to do the mapping to an Optional (the item is included in the
     *     output if the optional is defined).
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
     * @throws E if it is thrown by any call to {@code mapFunction}
     */
    public static <S, T, E extends Exception> List<T> mapToListOptional(
            Collection<S> collection,
            Class<? extends Exception> throwableClass,
            CheckedFunction<S, Optional<T>, E> mapFunction)
            throws E {
        return mapToListOptional(collection.stream(), throwableClass, mapFunction);
    }

    /**
     * Maps a stream to a list with each element in the original collection maybe producing an
     * element in the output.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param  <E> an exception that may be thrown by an {@code mapFunction}
     * @param stream the stream to be mapped.
     * @param throwableClass class type of exception that may be thrown by {@code mapFunction}.
     * @param mapFunction function to do the mapping to an Optional (the item is included in the
     *     output if the optional is defined).
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
     * @throws E if it is thrown by any call to {@code mapFunction}
     */
    public static <S, T, E extends Exception> List<T> mapToListOptional(
            Stream<S> stream,
            Class<? extends Exception> throwableClass,
            CheckedFunction<S, Optional<T>, E> mapFunction)
            throws E {
        return CheckedStream.map(stream, throwableClass, mapFunction)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Maps a list to a new list with each element in the original collection maybe producing an
     * element in the output.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param  <E> an exception that may be thrown by an {@code mapFunction}
     * @param list the list to be mapped.
     * @param throwableClass class type of exception that may be thrown by {@code mapFunction}.
     * @param mapFunction function to do the mapping to an Optional (the item is included in the
     *     output if the optional is defined).
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping.
     * @throws E if it is thrown by any call to {@code mapFunction}
     */
    public static <S, T, E extends Exception> List<T> mapToListOptionalWithIndex(
            List<S> list,
            Class<? extends Exception> throwableClass,
            CheckedFunctionWithInt<S, Optional<T>, E> mapFunction)
            throws E {
        IntStream range = IntStream.range(0, list.size());
        return CheckedStream.mapIntStream(
                        range, throwableClass, index -> mapFunction.apply(list.get(index), index))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Flat-maps a collection to a list where in the original collection can produce many elements
     * in the outgoing list.
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
     * Flat-maps a collection to a list where in the original collection can produce many elements
     * in the outgoing list.
     *
     * @param  <S> parameter-type for function
     * @param  <T> return-type for function
     * @param  <E> exception that may be thrown by {@code collection}
     * @param collection the collection to be mapped
     * @param throwableClass the class of {@code E}.
     * @param mapFunction function to do the mapping
     * @return a list with the same size and same order, but using derived elements that are a
     *     result of the mapping
     * @throws E if thrown by {@code mapFunction}
     */
    public static <S, T, E extends Exception> List<T> flatMapToList(
            Collection<S> collection,
            Class<? extends Exception> throwableClass,
            CheckedFunction<S, Collection<? extends T>, E> mapFunction)
            throws E {
        return CheckedStream.flatMap(collection.stream(), throwableClass, mapFunction)
                .collect(Collectors.toList());
    }

    /**
     * Creates a list of elements, where each element corresponds to an index in a range.
     *
     * @param <T> element-type in the list
     * @param startInclusive minimum-element in range (inclusive).
     * @param endExclusive maximum-element in range (exclusive).
     * @param mapFunction function to do the mapping.
     * @return a list with an element for every item in the range.
     */
    public static <T> List<T> mapRangeToList(
            int startInclusive, int endExclusive, IntFunction<T> mapFunction) {
        return IntStream.range(startInclusive, endExclusive)
                .mapToObj(mapFunction)
                .collect(Collectors.toList());
    }

    /**
     * Creates a list of elements, by repeatedly calling the same operation.
     *
     * @param <T> element-type in the list
     * @param numberRepeats the number of repeats.
     * @param mapFunction function to create each element.
     * @return a list with an element for every iteration.
     */
    public static <T> List<T> repeat(int numberRepeats, Supplier<T> mapFunction) {
        return IntStream.rangeClosed(0, numberRepeats)
                .mapToObj(index -> mapFunction.get())
                .collect(Collectors.toList());
    }

    /**
     * Filters a collection and maps the result to a list.
     *
     * <p>This function's purpose is mostly an convenience utility to make source-code easier to
     * read, as the paradigm below (although idiomatic) occurs in multiple places.
     *
     * @param <T> list item-type
     * @param predicate predicate to first filter the input collection before mapping.
     * @param collection the collection to be filtered.
     * @return a list with only the elements that pass the filter.
     */
    public static <T> List<T> filterToList(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * Filters a collection and maps the result to a list.
     *
     * <p>This function's purpose is mostly an convenience utility to make source-code easier to
     * read, as the paradigm below (although idiomatic) occurs in multiple places.
     *
     * @param <T> list item-type
     * @param <E> exception that may be thrown during evaluating the predicate
     * @param predicate predicate to first filter the input collection before mapping.
     * @param collection the collection to be filtered.
     * @param throwableClass class type of exception that may be thrown by {@code mapFunction}.
     * @return a list with only the elements that pass the filter.
     * @throws E if an exception is thrown during evaluating the predicate.
     */
    public static <T, E extends Exception> List<T> filterToList(
            Collection<T> collection,
            Class<? extends Exception> throwableClass,
            CheckedPredicate<T, E> predicate)
            throws E {
        return CheckedStream.filter(collection.stream(), throwableClass, predicate)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new collection by filtering a list and then mapping to a list of another type.
     *
     * @param <S> type that will be mapped from
     * @param <T> type that will be mapped to
     * @param <E> exception that may be thrown during mapping
     * @param list incoming list to be mapped.
     * @param predicate a condition that must be fulfilled for elements in {@code list} to be
     *     included in the returned list.
     * @param mapFunction function for mapping.
     * @return a newly created list.
     * @throws E if an exception is thrown while calling {@code predicate} or {@code mapFunction}.
     */
    public static <S, T, E extends Exception> List<T> filterAndMapToList(
            List<S> list, CheckedPredicate<S, E> predicate, CheckedFunction<S, T, E> mapFunction)
            throws E {

        List<T> out = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {

            S item = list.get(i);

            if (predicate.test(item)) {
                out.add(mapFunction.apply(item));
            }
        }
        return out;
    }

    /**
     * Creates a new collection by filtering a list and then mapping (with an index) to a list of
     * another type.
     *
     * @param <S> type that will be mapped from
     * @param <T> type that will be mapped to
     * @param <E> exception that may be thrown during mapping
     * @param list incoming list to be mapped
     * @param predicate a condition that must be fulfilled for elements in {@code list} to be
     *     included in the returned list.
     * @param mapFuncWithIndex function for mapping, also including an index (the original position
     *     in the bounding-box).
     * @return a newly created list.
     * @throws E if an exception is thrown during mapping.
     */
    public static <S, T, E extends Exception> List<T> filterAndMapWithIndexToList(
            List<S> list, Predicate<S> predicate, CheckedFunctionWithInt<S, T, E> mapFuncWithIndex)
            throws E {
        List<T> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {

            S item = list.get(i);

            if (predicate.test(item)) {
                out.add(mapFuncWithIndex.apply(item, i));
            }
        }
        return out;
    }

    /**
     * Create a list of pairs of elements from two separate collections.
     *
     * <p>Elements are combined from each in the same order as iteration.
     *
     * @param <S> element-type in first collection
     * @param <T> element-type in second collection
     * @param first first collection.
     * @param second second collection.
     * @return a newly created list where each element is a pair comprising an element from {@code
     *     first} and the corresponding element from {@code second}.
     */
    public static <S, T> List<Pair<S, T>> zip(Collection<S> first, Collection<T> second) {
        return zip(first, second, Pair::of);
    }

    /**
     * Create a list of elements from two separate collections.
     *
     * <p>Elements are combined from each in the same order as iteration.
     *
     * @param <S> element-type in first collection
     * @param <T> element-type in second collection
     * @param <V> combined-element type
     * @param first first collection.
     * @param second second collection.
     * @param combine how to combine a first and second element.
     * @return a newly created list where each element is a pair comprising an element from {@code
     *     first} and the corresponding element from {@code second}.
     */
    public static <S, T, V> List<V> zip(
            Collection<S> first, Collection<T> second, BiFunction<S, T, V> combine) {
        checkCollectionSize(first, second);
        return Streams.zip(first.stream(), second.stream(), combine).collect(Collectors.toList());
    }

    /** Checks two collections have an identical number of elements. */
    private static <S, T> void checkCollectionSize(
            Collection<S> collection1, Collection<T> collection2) {
        if (collection1.size() != collection2.size()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Both collections must have the same size. %d != %d",
                            collection1.size(), collection2.size()));
        }
    }
}
