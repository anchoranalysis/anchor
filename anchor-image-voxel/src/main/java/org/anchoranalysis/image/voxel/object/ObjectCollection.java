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
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesInt;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * A collection of {@link ObjectMask}s.
 *
 * @author Owen Feehan
 */
public class ObjectCollection implements Iterable<ObjectMask> {

    private final List<ObjectMask> delegate;

    /** Creates with no objects. */
    public ObjectCollection() {
        delegate = new ArrayList<>();
    }

    /**
     * Creates with elements from a stream.
     *
     * @param stream the stream of objects.
     */
    public ObjectCollection(Stream<ObjectMask> stream) {
        delegate = stream.collect(Collectors.toList());
    }

    /**
     * Shifts the bounding-box of each object by adding to it.
     *
     * <p>i.e. adds a vector to the corner position.
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @param shiftBy what to add to the corner position.
     * @return newly created object-collection with shifted corner position and identical extent.
     */
    public ObjectCollection shiftBy(ReadableTuple3i shiftBy) {
        return stream().mapBoundingBoxPreserveExtent(box -> box.shiftBy(shiftBy));
    }

    /**
     * Adds an object to the collection.
     *
     * @param object the object to add.
     */
    public void add(ObjectMask object) {
        delegate.add(object);
    }

    /**
     * Adds all objects in {@code objects} to the collection.
     *
     * @param objects the objects to add.
     */
    public void addAll(ObjectCollection objects) {
        addAll(objects.delegate);
    }

    /**
     * Adds all objects in {@code collection} to the collection.
     *
     * @param collection the collection of objects to add.
     */
    public void addAll(Collection<? extends ObjectMask> collection) {
        delegate.addAll(collection);
    }

    /**
     * Checks if two collections are equal in a shallow way.
     *
     * <p>Specifically, objects are tested to be equal using their object references.
     *
     * <p>i.e. they are equal iff they have the same reference.
     *
     * <p>This is a cheaper equality check than with {@link #equalsDeep}.
     *
     * <p>Both collections must have identical ordering.
     */
    @Override
    public boolean equals(Object other) {
        return delegate.equals(other);
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
     *
     * @param other the collection to test equality with.
     * @return true iff the the current collection is equal to {@code other}, as above.
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

    /**
     * Get an {@link ObjectMask} at a particular position in the collection.
     *
     * @param index the index the object is located at.
     * @return the object.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public ObjectMask get(int index) {
        return delegate.get(index);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    /**
     * Whether the collection contains no objects.
     *
     * @return true iff the collection contains no objects.
     */
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<ObjectMask> iterator() {
        return delegate.iterator();
    }

    /**
     * Removes the object at the specified position in the collection.
     *
     * @param index the position of the object to remove.
     */
    public void remove(int index) {
        delegate.remove(index);
    }

    /**
     * The number of objects in the collection.
     *
     * @return the number of elements.
     */
    public int size() {
        return delegate.size();
    }

    /**
     * A string representation of all objects in the collection using their center of gravities (and
     * optionally indices).
     *
     * @param newlines if true a newline separates each item, otherwise a whitespace.
     * @param includeIndices whether to additionally show the index of each item beside its center
     *     of gravity.
     * @return a descriptive string of the collection (beginning and ending with parantheses).
     */
    public String toString(boolean newlines, boolean includeIndices) {

        String sep = newlines ? "\n" : " ";

        StringBuilder builder = new StringBuilder();
        builder.append("( ");
        for (int index = 0; index < delegate.size(); index++) {

            builder.append(objectToString(delegate.get(index), index, includeIndices));
            builder.append(sep);
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * Default string representation of the collection, one line with each object described by its
     * center-of-gravity.
     */
    @Override
    public String toString() {
        return toString(false, false);
    }

    /**
     * The {@link BinaryValuesByte} associated with the first object in the collection.
     *
     * @return the binary-values associated with the first object.
     * @throws IndexOutOfBoundsException if the collection is empty.
     */
    public BinaryValuesByte getFirstBinaryValuesByte() {
        return get(0).binaryValuesByte();
    }

    /**
     * The {@link BinaryValuesInt} associated with the first object in the collection.
     *
     * @return the binary-values associated with the first object.
     * @throws IndexOutOfBoundsException if the collection is empty.
     */
    public BinaryValuesInt getFirstBinaryValues() {
        return get(0).binaryValues();
    }

    /**
     * Deep copy, including duplicating {@link ObjectMask}s.
     *
     * @return the deep-copy.
     */
    public ObjectCollection duplicate() {
        return stream().map(ObjectMask::duplicate);
    }

    /**
     * Shallow copy of objects.
     *
     * @return the deep-copy.
     */
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
    public ObjectCollection createSubset(Collection<Integer> indices) {
        return new ObjectCollection(streamIndices(indices));
    }

    /**
     * Exposes the underlying objects as a list.
     *
     * <p>Be careful when manipulating this list, as it is the same list used internally in the
     * object.
     *
     * @return a list with the {@link ObjectMask}s in this collection.
     */
    public List<ObjectMask> asList() {
        return delegate;
    }

    /***
     * Provides various functional-programming operations on the object-collection.
     *
     * @return a stream-like interface of operations.
     */
    public ObjectMaskStream stream() {
        return new ObjectMaskStream(this);
    }

    /**
     * A stream of object-masks as per Java's standard collections interface.
     *
     * @return the stream.
     */
    public Stream<ObjectMask> streamStandardJava() {
        return delegate.stream();
    }

    /**
     * Streams only objects at specific indices.
     *
     * @param indices indices of objects to place in the stream.
     * @return the stream containing only the objects whose indices are in {@code indices}.
     */
    public Stream<ObjectMask> streamIndices(Collection<Integer> indices) {
        return indices.stream().map(this::get);
    }

    /** Descriptive string representation of an {@link ObjectMask}. */
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
