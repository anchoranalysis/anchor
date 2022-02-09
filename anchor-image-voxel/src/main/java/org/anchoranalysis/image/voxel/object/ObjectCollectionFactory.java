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

package org.anchoranalysis.image.voxel.object;

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
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.CheckedStream;
import org.anchoranalysis.core.functional.checked.CheckedBiFunction;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.functional.checked.CheckedIntFunction;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Creates {@link ObjectCollection} using various utility and helper methods.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectCollectionFactory {

    /**
     * Creates a newly created object-collection that is empty.
     *
     * @return a newly-created empty object collection.
     */
    public static ObjectCollection empty() {
        return new ObjectCollection();
    }

    /**
     * Creates a new collection with elements from the parameter-list.
     *
     * @param object object-mask to add to collection.
     * @return the newly-created collection, reusing {@code object}.
     */
    @SafeVarargs
    public static ObjectCollection of(ObjectMask... object) {
        return new ObjectCollection(Arrays.stream(object));
    }

    /**
     * Creates a new collection with elements copied from existing collections.
     *
     * @param collection existing collections to copy from.
     * @return the newly-created collection, reusing the objects from {@code objects}.
     */
    @SafeVarargs
    public static ObjectCollection of(ObjectCollection... collection) {
        Stream<ObjectMask> stream =
                Arrays.stream(collection).flatMap(objects -> objects.streamStandardJava());
        return new ObjectCollection(stream);
    }

    /**
     * Creates a new collection with elements copied from existing collections, if they exist.
     *
     * @param collections existing collections to copy from.
     * @return the newly-created collection, reusing the objects from {@code objects}.
     */
    @SafeVarargs
    public static ObjectCollection of(Optional<ObjectCollection>... collections) {
        Stream<ObjectMask> stream =
                Arrays.stream(collections)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .flatMap(objects -> objects.streamStandardJava());
        return new ObjectCollection(stream);
    }

    /**
     * Creates a new collection with elements copied from existing collections.
     *
     * @param collections one or more collections to add items from.
     * @return a newly created {@link ObjectCollection}, reusing the objects in {@code collections}.
     */
    @SafeVarargs
    public static ObjectCollection of(Collection<ObjectMask>... collections) {
        Stream<ObjectMask> stream = Arrays.stream(collections).flatMap(objects -> objects.stream());
        return new ObjectCollection(stream);
    }

    /**
     * Creates a new collection by mapping an {@link Iterable} to {@link ObjectMask}.
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}.
     * @param iterable source of entities to be mapped.
     * @param mapFunction function for mapping.
     * @return a newly created {@link ObjectCollection}.
     */
    public static <T> ObjectCollection mapFrom(
            Iterable<T> iterable, Function<T, ObjectMask> mapFunction) {
        Stream<T> streamIn = StreamSupport.stream(iterable.spliterator(), false);
        return new ObjectCollection(streamIn.map(mapFunction));
    }

    /**
     * Creates a new collection by mapping an {@link Iterable} to {@link ObjectMask}.
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}.
     * @param <E> exception-type that can be thrown during mapping.
     * @param iterable source of entities to be mapped.
     * @param throwableClass the class of the exception that might be thrown during mapping.
     * @param mapFunction function for mapping.
     * @return a newly created {@link ObjectCollection}.
     * @throws E exception if it occurs during mapping.
     */
    public static <T, E extends Exception> ObjectCollection mapFrom(
            Iterable<T> iterable,
            Class<? extends E> throwableClass,
            CheckedFunction<T, ObjectMask, E> mapFunction)
            throws E {
        Stream<T> streamIn = StreamSupport.stream(iterable.spliterator(), false);
        Stream<ObjectMask> streamConverted =
                CheckedStream.map(streamIn, throwableClass, mapFunction);
        return new ObjectCollection(streamConverted);
    }

    /**
     * Creates a new collection by mapping an {@link Iterable} to {@link Optional}.
     *
     * <p>The object is only included in the outgoing collection if {@link Optional#isPresent()}.
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}.
     * @param <E> exception-type that can be thrown during mapping.
     * @param iterable iterable to be mapped.
     * @param mapFunction function for mapping.
     * @return a newly created ObjectCollection.
     * @throws E exception if it occurs during mapping.
     */
    public static <T, E extends Exception> ObjectCollection mapFromOptional(
            Iterable<T> iterable, CheckedFunction<T, Optional<ObjectMask>, E> mapFunction)
            throws E {
        return mapFromOptional(iterable.iterator(), mapFunction);
    }

    /**
     * Creates a new collection by mapping an {@link Iterator} to {@link Optional}.
     *
     * <p>The object is only included in the outgoing collection if {@link Optional#isPresent()}.
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}.
     * @param <E> exception-type that can be thrown during mapping.
     * @param iterator to be mapped.
     * @param mapFunction function for mapping.
     * @return a newly created {@link ObjectCollection}.
     * @throws E exception if it occurs during mapping.
     */
    public static <T, E extends Exception> ObjectCollection mapFromOptional(
            Iterator<T> iterator, CheckedFunction<T, Optional<ObjectMask>, E> mapFunction)
            throws E {
        ObjectCollection out = new ObjectCollection();
        while (iterator.hasNext()) {
            mapFunction.apply(iterator.next()).ifPresent(out::add);
        }
        return out;
    }

    /**
     * Creates a new collection with elements from the parameter-list of {@link BinaryVoxels}
     * converting the voxels in their entirety to an object-mask at the origin.
     *
     * @param masks object-mask to add to collection.
     * @return a newly created {@link ObjectCollection}.
     */
    @SafeVarargs
    public static ObjectCollection of(BinaryVoxels<UnsignedByteBuffer>... masks) {
        Stream<ObjectMask> stream = Arrays.stream(masks).map( mask -> new ObjectMask(mask) );
        return new ObjectCollection(stream);
    }

    /**
     * Creates a new collection from a set of {@link ObjectMask}.
     *
     * @param set set.
     * @return the newly created collection.
     */
    public static ObjectCollection fromSet(Set<ObjectMask> set) {
        return new ObjectCollection(set.stream());
    }

    /**
     * Creates a new collection by repeatedly calling a function to create a single {@link
     * ObjectMask}.
     *
     * @param repeats the number of objects created.
     * @param createObjectMask creates a new object-mask.
     * @return a newly created {@link ObjectCollection}.
     */
    public static ObjectCollection fromRepeated(
            int repeats, Supplier<ObjectMask> createObjectMask) {
        return mapFromRange(0, repeats, index -> createObjectMask.get());
    }

    /**
     * Creates a new collection by mapping integers (from a range) each to a {@link ObjectMask}.
     *
     * @param startInclusive start index for the integer range (inclusive).
     * @param endExclusive end index for the integer range (exclusive).
     * @param mapFunction function for mapping.
     * @return a newly created {@link ObjectCollection}.
     */
    public static ObjectCollection mapFromRange(
            int startInclusive, int endExclusive, IntFunction<ObjectMask> mapFunction) {
        return new ObjectCollection(
                IntStream.range(startInclusive, endExclusive).mapToObj(mapFunction));
    }

    /**
     * Creates a new collection by mapping integers (from a range) each to a {@link ObjectMask}.
     *
     * @param startInclusive start index for the integer range (inclusive).
     * @param endExclusive end index for the integer range (exclusive).
     * @param throwableClass the class of the exception that might be thrown during mapping.
     * @param mapFunction function for mapping.
     * @return a newly created {@link ObjectCollection}.
     * @throws E if the exception is thrown during mapping.
     */
    public static <E extends Exception> ObjectCollection mapFromRange(
            int startInclusive,
            int endExclusive,
            Class<? extends Exception> throwableClass,
            CheckedIntFunction<ObjectMask, E> mapFunction)
            throws E {
        return new ObjectCollection(
                CheckedStream.mapIntStream(
                        IntStream.range(startInclusive, endExclusive),
                        throwableClass,
                        mapFunction));
    }

    /**
     * Creates a new collection by flat-mapping integers (from a range) each to a {@link
     * ObjectCollection}.
     *
     * @param startInclusive start index for the integer range (inclusive).
     * @param endExclusive end index for the integer range (exclusive).
     * @param mapFunction function for flat-mapping.
     * @return a newly created {@link ObjectCollection}.
     */
    public static ObjectCollection flatMapFromRange(
            int startInclusive, int endExclusive, IntFunction<ObjectCollection> mapFunction) {
        return new ObjectCollection(
                IntStream.range(startInclusive, endExclusive)
                        .mapToObj(mapFunction)
                        .flatMap(ObjectCollection::streamStandardJava));
    }

    /**
     * Creates a new collection by flat-mapping integers (from a range) each to a {@link
     * ObjectCollection}.
     *
     * @param startInclusive start index for the integer range (inclusive).
     * @param endExclusive end index for the integer range (exclusive).
     * @param throwableClass the class of the exception that might be thrown during mapping.
     * @param mapFunction function for flat-mapping.
     * @return a newly created {@link ObjectCollection}.
     * @throws E exception if it occurs during mapping.
     */
    public static <E extends Exception> ObjectCollection flatMapFromRange(
            int startInclusive,
            int endExclusive,
            Class<? extends Exception> throwableClass,
            CheckedIntFunction<ObjectCollection, E> mapFunction)
            throws E {
        return new ObjectCollection(
                CheckedStream.mapIntStream(
                                IntStream.range(startInclusive, endExclusive),
                                throwableClass,
                                mapFunction)
                        .flatMap(ObjectCollection::streamStandardJava));
    }

    /**
     * Creates a new collection by filtering an iterable and then mapping it to {@link ObjectMask}.
     *
     * @param <T> type that will be mapped to {@link ObjectMask}.
     * @param collection incoming collection to be mapped.
     * @param predicate only elements from the iterable that satisfy this predicate are added.
     * @param mapFunction function for mapping.
     * @return a newly created {@link ObjectCollection}.
     */
    public static <T> ObjectCollection filterAndMapFrom(
            Collection<T> collection, Predicate<T> predicate, Function<T, ObjectMask> mapFunction) {
        Stream<ObjectMask> stream = collection.stream().filter(predicate).map(mapFunction);
        return new ObjectCollection(stream);
    }

    /**
     * Creates a new collection by filtering an iterable and then mapping it to {@link ObjectMask}.
     *
     * @param <T> type that will be mapped to {@link ObjectMask}.
     * @param <E> exception-type that may be thrown during mapping.
     * @param collection incoming collection to be mapped.
     * @param predicate only elements from the iterable that satisfy this predicate are added.
     * @param throwableClass the class of {@code E}.
     * @param mapFunction function for mapping.
     * @return a newly created {@link ObjectCollection}.
     * @throws E if thrown by <code>mapFunction</code>
     */
    public static <T, E extends Exception> ObjectCollection filterAndMapFrom(
            Collection<T> collection,
            Predicate<T> predicate,
            Class<? extends E> throwableClass,
            CheckedFunction<T, ObjectMask, E> mapFunction)
            throws E {
        Stream<ObjectMask> stream =
                CheckedStream.map(
                        collection.stream().filter(predicate), throwableClass, mapFunction);
        return new ObjectCollection(stream);
    }

    /**
     * Creates a new collection by filtering a list and then mapping from it to {@link ObjectMask}.
     *
     * @param <T> type that will be mapped to {@link ObjectCollection}.
     * @param <E> exception that be thrown during mapping.
     * @param list incoming list to be mapped.
     * @param predicate only elements from the list that satisfy this predicate are added.
     * @param mapFunctionWithIndex function for mapping, also including an index (the original
     *     position in the bounding-box).
     * @return a newly created {@link ObjectCollection}.
     * @throws E if an exception is thrown during mapping.
     */
    public static <T, E extends Exception> ObjectCollection filterAndMapWithIndexFrom(
            List<T> list,
            Predicate<T> predicate,
            CheckedBiFunction<T, Integer, ObjectMask, E> mapFunctionWithIndex)
            throws E {
        ObjectCollection out = new ObjectCollection();
        for (int i = 0; i < list.size(); i++) {

            T item = list.get(i);

            if (predicate.test(item)) {
                out.add(mapFunctionWithIndex.apply(item, i));
            }
        }
        return out;
    }

    /**
     * Creates a new collection by flat-mapping an incoming stream to {@link ObjectCollection}.
     *
     * @param <T> type that will be flatMapped to {@link ObjectCollection}.
     * @param collection incoming collection to be flat-mapped.
     * @param mapFunction function for mapping.
     * @return a newly created {@link ObjectCollection}.
     */
    public static <T> ObjectCollection flatMapFrom(
            Collection<T> collection, Function<T, ObjectCollection> mapFunction) {
        return new ObjectCollection(
                collection.stream().flatMap(t -> mapFunction.apply(t).streamStandardJava()));
    }

    /**
     * Creates a new collection by flat-mapping an incoming stream to {@link ObjectCollection} and
     * rethrowing any exception during mapping.
     *
     * @param <T> type that will be flatMapped to {@link ObjectCollection}.
     * @param stream incoming stream to be flat-mapped.
     * @param throwableClass the class of the exception that might be thrown during mapping.
     * @param mapFunction function for flat-mapping.
     * @return a newly created {@link ObjectCollection}.
     * @throws E exception of it occurs during mapping.
     */
    public static <T, E extends Exception> ObjectCollection flatMapFrom(
            Stream<T> stream,
            Class<? extends Exception> throwableClass,
            CheckedFunction<T, ObjectCollection, E> mapFunction)
            throws E {
        return flatMapFromCollection(
                stream, throwableClass, source -> mapFunction.apply(source).asList());
    }

    /**
     * Creates a new {@link ObjectCollection} by flatMapping an incoming stream to {@code
     * Collection<ObjectMask>} <i>and</i> rethrowing any exception during mapping.
     *
     * @param <T> type that will be flatMapped to {@link ObjectCollection}.
     * @param stream incoming stream to be flat-mapped.
     * @param throwableClass the class of the exception that might be thrown during mapping.
     * @param mapFunction function for flat-mapping.
     * @return a newly created {@link ObjectCollection}.
     * @throws E exception of it occurs during mapping.
     */
    public static <T, E extends Exception> ObjectCollection flatMapFromCollection(
            Stream<T> stream,
            Class<? extends Exception> throwableClass,
            CheckedFunction<T, Collection<? extends ObjectMask>, E> mapFunction)
            throws E {
        return new ObjectCollection(CheckedStream.flatMap(stream, throwableClass, mapFunction));
    }
}
