/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
/* (C)2020 */
package org.anchoranalysis.image.object;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.CheckedStream;
import org.anchoranalysis.core.functional.function.BiFunctionWithException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.functional.function.IntFunctionWithException;
import org.anchoranalysis.image.binary.mask.Mask;

/**
 * Creates {@link ObjectCollection} using various utility and helper methods
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectCollectionFactory {

    /**
     * Creates a newly created object-collection that is empty
     *
     * @return a newly-created empty object collection
     */
    public static ObjectCollection empty() {
        return new ObjectCollection();
    }

    /**
     * Creates a new collection with elements from the parameter-list
     *
     * @param object object-mask to add to collection
     */
    @SafeVarargs
    public static ObjectCollection from(ObjectMask... object) {
        ObjectCollection out = new ObjectCollection();
        Arrays.stream(object).forEach(out::add);
        return out;
    }

    /**
     * Creates a new collection with elements copied from existing collections
     *
     * @param objects existing collections to copy from
     */
    @SafeVarargs
    public static ObjectCollection from(ObjectCollection... objects) {
        ObjectCollection out = new ObjectCollection();
        Arrays.stream(objects).forEach(out::addAll);
        return out;
    }

    /**
     * Creates a new collection with elements copied from existing collections (if they exist)
     *
     * @param objects existing collections to copy from
     */
    @SafeVarargs
    public static ObjectCollection from(Optional<ObjectCollection>... objects) {
        ObjectCollection out = new ObjectCollection();
        for (Optional<ObjectCollection> collection : objects) {
            collection.ifPresent(out::addAll);
        }
        return out;
    }

    /**
     * Creates a new collection with elements copied from existing collections
     *
     * @param collection one or more collections to add items from
     */
    @SafeVarargs
    public static ObjectCollection from(Collection<ObjectMask>... collection) {
        ObjectCollection out = new ObjectCollection();
        Arrays.stream(collection).forEach(out::addAll);
        return out;
    }

    /**
     * Creates a new collection by mapping an {@link Iterable} to {@link ObjectMask}
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}
     * @param <E> exception-type that can be thrown during mapping
     * @param iterable source of entities to be mapped
     * @param mapFunc function for mapping
     * @return a newly created ObjectCollection
     * @throws E exception if it occurs during mapping
     */
    public static <T, E extends Exception> ObjectCollection mapFrom(
            Iterable<T> iterable, FunctionWithException<T, ObjectMask, E> mapFunc) throws E {
        ObjectCollection out = new ObjectCollection();
        for (T item : iterable) {
            out.add(mapFunc.apply(item));
        }
        return out;
    }

    /**
     * Creates a new collection by mapping an {@link Iterable} to {@link Optional<ObjectMask>}
     *
     * <p>The object is only included in the outgoing collection if Optional.isPresent()
     *
     * <p>
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}
     * @param <E> exception-type that can be thrown during mapping
     * @param collection incoming collection to be mapped
     * @param mapFunc function for mapping
     * @return a newly created ObjectCollection
     * @throws E exception if it occurs during mapping
     */
    public static <T, E extends Exception> ObjectCollection mapFromOptional(
            Iterable<T> iterable, FunctionWithException<T, Optional<ObjectMask>, E> mapFunc)
            throws E {
        ObjectCollection out = new ObjectCollection();
        for (T item : iterable) {
            mapFunc.apply(item).ifPresent(out::add);
        }
        return out;
    }

    /**
     * Creates a new collection with elements from the parameter-list of {@link Mask} converting
     * each channel to an object-mask
     *
     * @param obj object-mask to add to collection
     */
    @SafeVarargs
    public static ObjectCollection from(Mask... chnl) {
        ObjectCollection out = new ObjectCollection();
        for (Mask bc : chnl) {
            out.add(new ObjectMask(bc.binaryVoxelBox()));
        }
        return out;
    }

    /**
     * Creates a new collection from a set of {@link ObjectMask}
     *
     * @param set set
     * @return the newly created collection
     */
    public static ObjectCollection fromSet(Set<ObjectMask> set) {
        return new ObjectCollection(set.stream());
    }

    /**
     * Creates a new collection by repeatedly calling a function to create a single {@link
     * ObjectMask}
     *
     * @param repeats the number of objects created
     * @param createObjectMask creates a new object-mask
     * @return a newly created ObjectCollection
     */
    public static ObjectCollection fromRepeated(
            int repeats, Supplier<ObjectMask> createObjectMask) {
        return mapFromRange(0, repeats, index -> createObjectMask.get());
    }

    /**
     * Creates a new collection by mapping integers (from a range) each to a {@link ObjectMask}
     *
     * @param startInclusive start index for the integer range (inclusive)
     * @param endExclusive end index for the integer range (exclusive)
     * @param mapFunc function for mapping
     * @return a newly created ObjectCollection
     */
    public static ObjectCollection mapFromRange(
            int startInclusive, int endExclusive, IntFunction<ObjectMask> mapFunc) {
        return new ObjectCollection(
                IntStream.range(startInclusive, endExclusive).mapToObj(mapFunc));
    }

    /**
     * Creates a new collection by mapping integers (from a range) each to a {@link ObjectMask}
     *
     * @param startInclusive start index for the integer range (inclusive)
     * @param endExclusive end index for the integer range (exclusive)
     * @param throwableClass the class of the exception that might be thrown during mapping
     * @param mapFunc function for mapping
     * @return a newly created ObjectCollection
     * @throws E if the exception is thrown during mapping
     */
    public static <E extends Exception> ObjectCollection mapFromRange(
            int startInclusive,
            int endExclusive,
            Class<? extends Exception> throwableClass,
            IntFunctionWithException<ObjectMask, E> mapFunc)
            throws E {
        return new ObjectCollection(
                CheckedStream.mapIntStreamWithException(
                        IntStream.range(startInclusive, endExclusive), throwableClass, mapFunc));
    }

    /**
     * Creates a new collection by flat-mapping integers (from a range) each to a {@link
     * ObjectMaskCollection}
     *
     * @param startInclusive start index for the integer range (inclusive)
     * @param endExclusive end index for the integer range (exclusive)
     * @param throwableClass the class of the exception that might be thrown during mapping
     * @param mapFunc function for flat-mmapping
     * @return a newly created ObjectCollection
     */
    public static ObjectCollection flatMapFromRange(
            int startInclusive, int endExclusive, IntFunction<ObjectCollection> mapFunc) {
        return new ObjectCollection(
                IntStream.range(startInclusive, endExclusive)
                        .mapToObj(mapFunc)
                        .flatMap(ObjectCollection::streamStandardJava));
    }

    /**
     * Creates a new collection by flat-mapping integers (from a range) each to a {@link
     * ObjectMaskCollection}
     *
     * @param startInclusive start index for the integer range (inclusive)
     * @param endExclusive end index for the integer range (exclusive)
     * @param mapFunc function for flat-mapping
     * @return a newly created ObjectCollection
     * @throws E exception if it occurs during mapping
     */
    public static <E extends Exception> ObjectCollection flatMapFromRange(
            int startInclusive,
            int endExclusive,
            Class<? extends Exception> throwableClass,
            IntFunctionWithException<ObjectCollection, E> mapFunc)
            throws E {
        return new ObjectCollection(
                CheckedStream.mapIntStreamWithException(
                                IntStream.range(startInclusive, endExclusive),
                                throwableClass,
                                mapFunc)
                        .flatMap(ObjectCollection::streamStandardJava));
    }

    /**
     * Creates a new collection by filtering an iterable and then mapping it to {@link ObjectMask}
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}
     * @param <E> exception-type that may be thrown during mapping
     * @param iterable incoming collection to be mapped
     * @param mapFunc function for mapping
     * @return a newly created {@link ObjectCollection}
     * @throws E if thrown by <code>mapFunc</code>
     */
    public static <T, E extends Exception> ObjectCollection filterAndMapFrom(
            Iterable<T> iterable,
            Predicate<T> predicate,
            FunctionWithException<T, ObjectMask, E> mapFunc)
            throws E {
        ObjectCollection out = new ObjectCollection();
        for (T item : iterable) {

            if (!predicate.test(item)) {
                continue;
            }

            out.add(mapFunc.apply(item));
        }
        return out;
    }

    /**
     * Creates a new collection by filtering and a list and then mapping it to {@link ObjectMask}
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}
     * @param <E> exception that be thrown during mapping
     * @param list incoming list to be mapped
     * @param mapFuncWithIndex function for mapping, also including an index (the original position
     *     in the bounding-box)
     * @return a newly created ObjectCollection
     * @throws E
     * @throw E if an exception is thrown during mapping
     */
    public static <T, E extends Exception> ObjectCollection filterAndMapWithIndexFrom(
            List<T> list,
            Predicate<T> predicate,
            BiFunctionWithException<T, Integer, ObjectMask, E> mapFuncWithIndex)
            throws E {
        ObjectCollection out = new ObjectCollection();
        for (int i = 0; i < list.size(); i++) {

            T item = list.get(i);

            if (!predicate.test(item)) {
                continue;
            }

            out.add(mapFuncWithIndex.apply(item, i));
        }
        return out;
    }

    /**
     * Creates a new collection by flatMapping an incoming stream to {@link ObjectCollection}
     *
     * @param <T> type that will be flatMapped to {@link ObjectCollection}
     * @param collection incoming collection to be flat-mapped
     * @param mapFunc function for mapping
     * @return a newly created ObjectCollection
     */
    public static <T> ObjectCollection flatMapFrom(
            Collection<T> collection, Function<T, ObjectCollection> mapFunc) {
        return new ObjectCollection(
                collection.stream().flatMap(t -> mapFunc.apply(t).streamStandardJava()));
    }

    /**
     * Creates a new collection by flatMapping an incoming stream to {@link ObjectCollection} AND
     * rethrowing any exception during mapping
     *
     * @param <T> type that will be flatMapped to {@link ObjectCollection}
     * @param stream incoming stream to be flat-mapped
     * @param throwableClass the class of the exception that might be thrown during mapping
     * @param mapFunc function for flat-mapping
     * @return a newly created ObjectCollection
     * @throws E exception of it occurs during mapping
     */
    public static <T, E extends Exception> ObjectCollection flatMapFrom(
            Stream<T> stream,
            Class<? extends Exception> throwableClass,
            FunctionWithException<T, ObjectCollection, E> mapFunc)
            throws E {
        return flatMapFromCollection(
                stream, throwableClass, source -> mapFunc.apply(source).asList());
    }

    /**
     * Creates a new {@link ObjectCollection} by flatMapping an incoming stream to {@link
     * Collection<ObjectMask>} AND rethrowing any exception during mapping
     *
     * @param <T> type that will be flatMapped to {@link ObjectCollection}
     * @param stream incoming stream to be flat-mapped
     * @param throwableClass the class of the exception that might be thrown during mapping
     * @param mapFunc function for flat-mapping
     * @return a newly created ObjectCollection
     * @throws E exception of it occurs during mapping
     */
    public static <T, E extends Exception> ObjectCollection flatMapFromCollection(
            Stream<T> stream,
            Class<? extends Exception> throwableClass,
            FunctionWithException<T, Collection<? extends ObjectMask>, E> mapFunc)
            throws E {
        return new ObjectCollection(
                CheckedStream.flatMapWithException(stream, throwableClass, mapFunc));
    }
}
