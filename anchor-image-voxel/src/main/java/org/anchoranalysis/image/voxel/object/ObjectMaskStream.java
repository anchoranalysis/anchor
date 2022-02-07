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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.CheckedStream;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.functional.checked.CheckedPredicate;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * A custom "stream" like class for various functional-programming operations on {@link
 * ObjectCollection}
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public final class ObjectMaskStream {

    private ObjectCollection delegate;

    /**
     * Creates a new {@link ObjectCollection} after mapping each item to another.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param  <E> exception-type that can occur during mapping
     * @param mapFunction performs mapping
     * @return a newly created object-collection
     * @throws E if an exception is thrown by the mapping function.
     */
    public <E extends Exception> ObjectCollection map(
            CheckedFunction<ObjectMask, ObjectMask, E> mapFunction) throws E {
        ObjectCollection out = new ObjectCollection();
        for (ObjectMask object : delegate) {
            out.add(mapFunction.apply(object));
        }
        return out;
    }

    /**
     * Creates a new {@link ObjectCollection} after mapping the bounding-box on each object (whose
     * extent should remain unchanged).
     *
     * <p>See {@link ObjectMask#mapBoundingBoxPreserveExtent(UnaryOperator)} for details on the
     * mapping.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param mapFunction maps the bounding-box to a new bounding-box
     * @return a newly created object-collection
     */
    public ObjectCollection mapBoundingBoxPreserveExtent(UnaryOperator<BoundingBox> mapFunction) {
        return map(object -> object.mapBoundingBoxPreserveExtent(mapFunction));
    }

    /**
     * Creates a new {@link ObjectCollection} after mapping the bounding-box on each object (while
     * maybe changing the extent).
     *
     * <p>See {@link ObjectMask#mapBoundingBoxChangeExtent} for details on the mapping.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * <p>Precondition: the new bounding-box's extent must be greater than or equal to the existing
     * extent in all dimensions.
     *
     * @param boxToAssign the bounding box to assign to the newly created object
     * @return a newly created object-collection
     */
    public ObjectCollection mapBoundingBoxChangeExtent(BoundingBox boxToAssign) {
        return map(object -> object.mapBoundingBoxChangeExtent(boxToAssign));
    }

    /**
     * Creates a new {@link List} after mapping each item to another type.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param <T> destination type for the mapping
     * @param <E> exception that can be thrown during mapping
     * @param mapFunction performs mapping
     * @return a newly created list contained the mapped objects
     * @throws E if an exception occurs during mapping
     */
    public <T, E extends Exception> List<T> mapToList(CheckedFunction<ObjectMask, T, E> mapFunction)
            throws E {
        List<T> out = new ArrayList<>();
        for (ObjectMask obj : delegate) {
            out.add(mapFunction.apply(obj));
        }
        return out;
    }

    /**
     * Creates a new {@link List} after mapping each item to another (optional) type
     *
     * <p>Items are only included if the output type is not-empty()
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param  <T> destination type for the mapping
     * @param  <E> exception that can be thrown during mapping
     * @param mapFunction performs mapping
     * @return a newly created list contained the mapped objects (which aren't Optional.empty())
     * @throws E if an exception occurs during mapping
     */
    public <T, E extends Exception> List<T> mapToListOptional(
            CheckedFunction<ObjectMask, Optional<T>, E> mapFunction) throws E {
        List<T> out = new ArrayList<>(delegate.size());
        for (ObjectMask obj : delegate) {
            Optional<T> result = mapFunction.apply(obj);
            result.ifPresent(out::add);
        }
        return out;
    }

    /**
     * Creates a new {@link SortedSet} after mapping each item to another type.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param <T> destination type for the mapping
     * @param <E> exception that can be thrown during mapping
     * @param mapFunction performs mapping
     * @return a newly created tree-set contained the mapped objects
     * @throws E if an exception occurs during mapping
     */
    public <T, E extends Exception> SortedSet<T> mapToSortedSet(
            CheckedFunction<ObjectMask, T, E> mapFunction) throws E {
        SortedSet<T> out = new TreeSet<>();
        for (ObjectMask obj : delegate) {
            out.add(mapFunction.apply(obj));
        }
        return out;
    }

    /**
     * Creates a new {@link ObjectCollection} after mapping each item to several others.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param mapFunction performs flat-mapping
     * @return a newly created object-collection
     */
    public ObjectCollection flatMap(Function<ObjectMask, ObjectCollection> mapFunction) {
        return new ObjectCollection(
                delegate.streamStandardJava()
                        .flatMap(element -> mapFunction.apply(element).streamStandardJava()));
    }

    /**
     * Like a typical {@code flatMap()} operation but accepts a mapping function that throws a
     * checked exception.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param  <E> exception-type that can be thrown by <code>mapFunction</code>.
     * @param throwableClass the class of {@code E}.
     * @param mapFunction performs flat-mapping.
     * @return a newly created object-collection.
     * @throws E if its thrown by <code>mapFunction</code>.
     */
    public <E extends Exception> ObjectCollection flatMap(
            Class<? extends Exception> throwableClass,
            CheckedFunction<ObjectMask, ObjectCollection, E> mapFunction)
            throws E {
        return new ObjectCollection(
                CheckedStream.flatMap(
                        delegate.streamStandardJava(),
                        throwableClass,
                        element -> mapFunction.apply(element).asList()));
    }

    /**
     * Filters a {@link ObjectCollection} to <b>include</b> certain items based on a predicate
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param predicate iff true object is included, otherwise excluded
     * @return a newly created object-collection, a filtered version of all objects
     */
    public ObjectCollection filter(Predicate<ObjectMask> predicate) {
        return new ObjectCollection(delegate.streamStandardJava().filter(predicate));
    }

    /**
     * Filters a {@link ObjectCollection} to <b>exclude</b> certain items based on a predicate
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param predicate iff true object is excluded, otherwise included
     * @return a newly created object-collection, a filtered version of all objects
     */
    public ObjectCollection filterExclude(Predicate<ObjectMask> predicate) {
        return filter(object -> !predicate.test(object));
    }

    /**
     * Filters a {@link ObjectCollection} to include certain items based on a predicate - and
     * optionally store rejected objects.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param  <E> exception-type that can be thrown by the predicate
     * @param predicate iff true object is included, otherwise excluded
     * @param objectsRejected iff true, any object rejected by the filter is added to this
     *     collection
     * @return a newly created object-collection, a filtered version of all objects
     * @throws E if thrown by the predicate
     */
    public <E extends Exception> ObjectCollection filter(
            CheckedPredicate<ObjectMask, E> predicate, Optional<ObjectCollection> objectsRejected)
            throws E {

        ObjectCollection out = new ObjectCollection();

        for (ObjectMask current : delegate) {

            if (predicate.test(current)) {
                out.add(current);
            } else {
                if (objectsRejected.isPresent()) {
                    objectsRejected.get().add(current);
                }
            }
        }
        return out;
    }

    /**
     * Performs a {@link #filter} and then a {@link #map}.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param mapFunction performs mapping
     * @param predicate iff true object is included, otherwise excluded
     * @return a newly created object-collection, a filtered version of all objects, then mapped
     */
    public ObjectCollection filterAndMap(
            Predicate<ObjectMask> predicate, UnaryOperator<ObjectMask> mapFunction) {
        return new ObjectCollection(
                delegate.streamStandardJava().filter(predicate).map(mapFunction));
    }

    /**
     * Like {@link #filter} but only operates on certain indices of the collection.
     *
     * <p>This is an <i>immutable</i> operation
     *
     * @param predicate iff true object is included, otherwise excluded
     * @param indices which indices of the collection to consider
     * @return a newly created object-collection, a filtered version of particular elements
     */
    public ObjectCollection filterSubset(
            Predicate<ObjectMask> predicate, Collection<Integer> indices) {
        return new ObjectCollection(filterSubsetStream(predicate, indices));
    }

    /**
     * Like {@link #filterSubset} but returns a stream rather than a {@link ObjectCollection}.
     *
     * <p>This is an <i>immutable</i> operation
     *
     * @param predicate iff true object is included, otherwise excluded
     * @param indices which indices of the collection to consider
     * @return a stream of filtered objects
     */
    public Stream<ObjectMask> filterSubsetStream(
            Predicate<ObjectMask> predicate, Collection<Integer> indices) {
        return delegate.streamIndices(indices).filter(predicate);
    }

    /**
     * Does the predicate evaluate to true on any object in the collection?
     *
     * @param predicate evaluates to true or false for a particular object
     * @return true if the predicate returns true on ANY one of the contained objects
     */
    public boolean anyMatch(Predicate<ObjectMask> predicate) {
        return delegate.streamStandardJava().anyMatch(predicate);
    }

    /**
     * Converts to a {@link HashSet}.
     *
     * @return a newly-created set, containing the items in the stream.
     */
    public Set<ObjectMask> toSet() {
        return delegate.streamStandardJava().collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Finds the maximum value of a function applied to each object in the collection.
     *
     * @param function function to apply
     * @return the maximum-int found by applying the function to each object (so long as the
     *     collection isn't empty)
     */
    public OptionalInt maxAsInt(ToIntFunction<ObjectMask> function) {
        return delegate.streamStandardJava().mapToInt(function).max();
    }

    /**
     * Finds the minimum value of a function applied to each object in the collection.
     *
     * @param function function to apply
     * @return the minimum-int found by applying the function to each object (so long as the
     *     collection isn't empty)
     */
    public OptionalInt minAsInt(ToIntFunction<ObjectMask> function) {
        return delegate.streamStandardJava().mapToInt(function).min();
    }
}
