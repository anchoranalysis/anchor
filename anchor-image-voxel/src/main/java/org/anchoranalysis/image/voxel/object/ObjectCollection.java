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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * A collection of {@link ObjectMask}
 *
 * @author Owen Feehan
 */
public class ObjectCollection implements Iterable<ObjectMask> {

    private final List<ObjectMask> delegate;

    /** Creates with no objects */
    public ObjectCollection() {
        delegate = new ArrayList<>();
    }

    /**
     * Creates with elements from a stream
     *
     * @param stream objects
     */
    public ObjectCollection(Stream<ObjectMask> stream) {
        delegate = stream.collect(Collectors.toList());
    }

    /**
     * Shifts the bounding-box of each object by adding to it i.e. adds a vector to the corner
     * position
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * <p>
     *
     * @param shiftBy what to add to the corner position
     * @return newly created object-collection with shifted corner position and identical extent
     */
    public ObjectCollection shiftBy(ReadableTuple3i shiftBy) {
        return stream().mapBoundingBoxPreserveExtent(box -> box.shiftBy(shiftBy));
    }

    public boolean add(ObjectMask object) {
        return delegate.add(object);
    }

    public boolean addAll(ObjectCollection objects) {
        return addAll(objects.delegate);
    }

    public boolean addAll(Collection<? extends ObjectMask> c) {
        return delegate.addAll(c);
    }

    public void clear() {
        delegate.clear();
    }

    /**
     * Checks if two collections are equal in a shallow way
     *
     * <p>Specifically, objects are tested to be equal using their object references (i.e. they are
     * equal iff they have the same reference)
     *
     * <p>This is a cheaper equality check than with {@link #equalsDeep}
     *
     * <p>Both collections must have identical ordering.
     */
    @Override
    public boolean equals(Object arg0) {
        return delegate.equals(arg0);
    }

    /**
     * Checks if two collections are equal in a deeper way.
     *
     * <p>Specifically, objects are tested to be equal using a deep byte-by-byte comparison using
     * {@link ObjectMask#equalsDeep}. Their objects do not need to be equal.
     *
     * <p>This is more expensive equality check than with {@link #equalsDeep}, but is useful for
     * comparing objects that were instantiated in different places.
     *
     * <p>Both collections must have identical ordering.
     */
    public boolean equalsDeep(ObjectCollection other) {
        if (size() != other.size()) {
            return false;
        }

        for (int i = 0; i < size(); i++) {
            if (!get(i).equalsDeep(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    public ObjectMask get(int index) {
        return delegate.get(index);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<ObjectMask> iterator() {
        return delegate.iterator();
    }

    public void remove(int index) {
        delegate.remove(index);
    }

    public int size() {
        return delegate.size();
    }

    /**
     * A string representation of all objects in the collection using their center of gravities (and
     * optionally indices)
     *
     * @param newlines if true a newline separates each item, otherwise a whitespace
     * @param includeIndices whether to additionally show the index of each item beside its center
     *     of gravity
     * @return a descriptive string of the collection (begining and ending with parantheses)
     */
    public String toString(boolean newlines, boolean includeIndices) {

        String sep = newlines ? "\n" : " ";

        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        for (int index = 0; index < delegate.size(); index++) {

            sb.append(objectToString(delegate.get(index), index, includeIndices));
            sb.append(sep);
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Default string representation of the collection, one line with each object described by its
     * center-of-gravity
     */
    @Override
    public String toString() {
        return toString(false, false);
    }

    public int countIntersectingVoxels(ObjectMask object) {

        int count = 0;
        for (ObjectMask other : this) {
            count += other.countIntersectingVoxels(object);
        }
        return count;
    }

    public boolean hasIntersectingVoxels(ObjectMask object) {

        for (ObjectMask other : this) {
            if (other.hasIntersectingVoxels(object)) {
                return true;
            }
        }
        return false;
    }

    public ObjectCollection findObjectsWithIntersectingBBox(ObjectMask objectToIntersectWith) {
        return stream()
                .filter(
                        object ->
                                object.boundingBox()
                                        .intersection()
                                        .existsWith(objectToIntersectWith.boundingBox()));
    }

    public boolean objectsAreAllInside(Extent e) {
        for (ObjectMask object : this) {
            if (!e.contains(object.boundingBox().cornerMin())) {
                return false;
            }
            if (!e.contains(object.boundingBox().calculateCornerMax())) {
                return false;
            }
        }
        return true;
    }

    public BinaryValuesByte getFirstBinaryValuesByte() {
        return get(0).binaryValuesByte();
    }

    public BinaryValues getFirstBinaryValues() {
        return get(0).binaryValues();
    }

    /** Deep copy, including duplicating object-masks */
    public ObjectCollection duplicate() {
        return stream().map(ObjectMask::duplicate);
    }

    /** Shallow copy of objects */
    public ObjectCollection duplicateShallow() {
        return new ObjectCollection(streamStandardJava());
    }

    /**
     * A subset of the collection identified by particular indices.
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @param indices index of each element to keep in new collection.
     * @return newly-created collection with only the indexed elements.
     */
    public ObjectCollection createSubset(List<Integer> indices) {
        return new ObjectCollection(streamIndices(indices));
    }

    /**
     * Exposes the underlying objects as a list
     *
     * <p>Be careful when manipulating this list, as it is the same list used internally in the
     * object.
     *
     * @return a list with the object-masks in this collection
     */
    public List<ObjectMask> asList() {
        return delegate;
    }

    /***
     * Provides various functional-programming operations on the object-collection
     *
     * @return a stream-like interface of operations
     */
    public ObjectMaskStream stream() {
        return new ObjectMaskStream(this);
    }

    /**
     * A stream of object-masks as per Java's standard collections interface
     *
     * @return the stream
     */
    public Stream<ObjectMask> streamStandardJava() {
        return delegate.stream();
    }

    /** Streams only objects at specific indices */
    Stream<ObjectMask> streamIndices(List<Integer> indices) {
        return indices.stream().map(this::get);
    }

    /** Descriptive string representation of an object-mask */
    private static String objectToString(ObjectMask object, int index, boolean includeIndex) {
        String cog = object.centerOfGravity().toString();
        int numberVoxels = object.numberVoxelsOn();
        if (includeIndex) {
            return String.format("%d %s(%s)", index, cog, numberVoxels);
        } else {
            return String.format("%s(%s)", cog, numberVoxels);
        }
    }
}
