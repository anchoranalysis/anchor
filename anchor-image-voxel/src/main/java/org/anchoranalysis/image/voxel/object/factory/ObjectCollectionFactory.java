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

package org.anchoranalysis.image.voxel.object.factory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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
import org.anchoranalysis.core.functional.function.CheckedBiFunction;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.functional.function.CheckedIntFunction;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;

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
    public static ObjectCollection of(ObjectMask... object) {
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
    public static ObjectCollection of(ObjectCollection... objects) {
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
    public static ObjectCollection of(Optional<ObjectCollection>... objects) {
        ObjectCollection out = new ObjectCollection();
        for (Optional<ObjectCollection> collection : objects) {
            collection.ifPresent(out::addAll);
        }
        return out;
    }

    /**
     * Creates a new collection with elements copied from existing collections
     *
     * @param collections one or more collections to add items from
     */
    @SafeVarargs
    public static ObjectCollection of(Collection<ObjectMask>... collections) {
        ObjectCollection out = new ObjectCollection();
        Arrays.stream(collections).forEach(out::addAll);
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
            Iterable<T> iterable, CheckedFunction<T, ObjectMask, E> mapFunc) throws E {
        ObjectCollection out = new ObjectCollection();
        for (T item : iterable) {
            out.add(mapFunc.apply(item));
        }
        return out;
    }

    /**
     * Creates a new collection by mapping an {@link Iterable} to {@link Optional}
     *
     * <p>The object is only included in the outgoing collection if Optional.isPresent()
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}
     * @param <E> exception-type that can be thrown during mapping
     * @param iterable iterable to be mapped
     * @param mapFunc function for mapping
     * @return a newly created ObjectCollection
     * @throws E exception if it occurs during mapping
     */
    public static <T, E extends Exception> ObjectCollection mapFromOptional(
            Iterable<T> iterable, CheckedFunction<T, Optional<ObjectMask>, E> mapFunc) throws E {
        return mapFromOptional(iterable.iterator(), mapFunc);
    }

    /**
     * Creates a new collection by mapping an {@link Iterator} to {@link Optional}
     *
     * <p>The object is only included in the outgoing collection if Optional.isPresent()
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}
     * @param <E> exception-type that can be thrown during mapping
     * @param iterator to be mapped
     * @param mapFunc function for mapping
     * @return a newly created ObjectCollection
     * @throws E exception if it occurs during mapping
     */
    public static <T, E extends Exception> ObjectCollection mapFromOptional(
            Iterator<T> iterator, CheckedFunction<T, Optional<ObjectMask>, E> mapFunc) throws E {
        ObjectCollection out = new ObjectCollection();
        while (iterator.hasNext()) {
            mapFunc.apply(iterator.next()).ifPresent(out::add);
        }
        return out;
    }

    /**
     * Creates a new collection with elements from the parameter-list of {@link BinaryVoxels}
     * converting the voxels in their entireity to an object-mask at the origin.
     *
     * @param masks object-mask to add to collection
     */
    @SafeVarargs
    public static ObjectCollection of(BinaryVoxels<UnsignedByteBuffer>... masks) {
        ObjectCollection out = new ObjectCollection();
        Arrays.stream(masks).forEach(mask -> out.add(new ObjectMask(mask)));
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
            CheckedIntFunction<ObjectMask, E> mapFunc)
            throws E {
        return new ObjectCollection(
                CheckedStream.mapIntStream(
                        IntStream.range(startInclusive, endExclusive), throwableClass, mapFunc));
    }

    /**
     * Creates a new collection by flat-mapping integers (from a range) each to a {@link
     * ObjectCollection}
     *
     * @param startInclusive start index for the integer range (inclusive)
     * @param endExclusive end index for the integer range (exclusive)
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
     * ObjectCollection}
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
            CheckedIntFunction<ObjectCollection, E> mapFunc)
            throws E {
        return new ObjectCollection(
                CheckedStream.mapIntStream(
                                IntStream.range(startInclusive, endExclusive),
                                throwableClass,
                                mapFunc)
                        .flatMap(ObjectCollection::streamStandardJava));
    }

    /**
     * Creates a new collection by filtering an iterable and then mapping it to {@link ObjectMask}
     *
     * @param <T> type that will be mapped to {@link ObjectMask}
     * @param <E> exception-type that may be thrown during mapping
     * @param iterable incoming collection to be mapped
     * @param mapFunc function for mapping
     * @return a newly created {@link ObjectCollection}
     * @throws E if thrown by <code>mapFunc</code>
     */
    public static <T, E extends Exception> ObjectCollection filterAndMapFrom(
            Iterable<T> iterable, Predicate<T> predicate, CheckedFunction<T, ObjectMask, E> mapFunc)
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
     * Creates a new collection by filtering a list and then mapping from it to {@link ObjectMask}.
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}
     * @param <E> exception that be thrown during mapping
     * @param list incoming list to be mapped
     * @param mapFuncWithIndex function for mapping, also including an index (the original position
     *     in the bounding-box)
     * @return a newly created ObjectCollection
     * @throws E if an exception is thrown during mapping
     */
    public static <T, E extends Exception> ObjectCollection filterAndMapWithIndexFrom(
            List<T> list,
            Predicate<T> predicate,
            CheckedBiFunction<T, Integer, ObjectMask, E> mapFuncWithIndex)
            throws E {
        ObjectCollection out = new ObjectCollection();
        for (int i = 0; i < list.size(); i++) {

            T item = list.get(i);

            if (predicate.test(item)) {
                out.add(mapFuncWithIndex.apply(item, i));
            }
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
            CheckedFunction<T, ObjectCollection, E> mapFunc)
            throws E {
        return flatMapFromCollection(
                stream, throwableClass, source -> mapFunc.apply(source).asList());
    }

    /**
     * Creates a new {@link ObjectCollection} by flatMapping an incoming stream to {@code
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
            CheckedFunction<T, Collection<? extends ObjectMask>, E> mapFunc)
            throws E {
        return new ObjectCollection(CheckedStream.flatMap(stream, throwableClass, mapFunc));
    }
}
