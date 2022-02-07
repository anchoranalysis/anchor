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

package org.anchoranalysis.image.core.object.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Like an {@link ObjectCollection} but each object has associated properties.
 *
 * <p>Like a {@link ObjectCollection}, it is backed internally by an {@link ArrayList}, offering add
 * operations in constant time, and other operations in linear-time.
 *
 * @author Owen Feehan
 */
public class ObjectCollectionWithProperties implements Iterable<ObjectWithProperties> {

    private final List<ObjectWithProperties> delegate;

    /** 
     * Create with zero objects.
     * 
     * @param capacity the capacity of the internally created list.
     */
    public ObjectCollectionWithProperties(int capacity) {
        delegate = new ArrayList<>(capacity);
    }

    /**
     * Create with a single object.
     *
     * @param object the object.
     */
    public ObjectCollectionWithProperties(ObjectMask object) {
        this(ObjectCollectionFactory.of(object));
    }

    /**
     * Create with a stream of objects with properties.
     *
     * @param objects the objects.
     */
    public ObjectCollectionWithProperties(Stream<ObjectWithProperties> objects) {
        delegate = objects.collect(Collectors.toList());
    }

    /**
     * Create with a stream of objects, assign empty properties to each.
     *
     * @param objects the objects.
     */
    public ObjectCollectionWithProperties(ObjectCollection objects) {
        delegate = objects.stream().mapToList(ObjectWithProperties::new);
    }

    /**
     * Add an {@link ObjectMask} to the collection, assigning empty properties to it.
     *
     * @param object the object.
     */
    public void add(ObjectMask object) {
        delegate.add(new ObjectWithProperties(object));
    }

    /**
     * Add an {@link ObjectWithProperties} to the collection.
     *
     * @param object the object.
     */
    public void add(ObjectWithProperties object) {
        delegate.add(object);
    }

    /**
     * Get an item at a particular index.
     *
     * @param index the index.
     * @return the corresponding item.
     */
    public ObjectWithProperties get(int index) {
        return delegate.get(index);
    }

    @Override
    public Iterator<ObjectWithProperties> iterator() {
        return delegate.iterator();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    /**
     * Returns the contained-objects without the corresponding properties.
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @return the a newly created {@link ObjectCollection} containing the same objects as the
     *     current collection, but without any associated properties.
     */
    public ObjectCollection withoutProperties() {
        return ObjectCollectionFactory.mapFrom(delegate, ObjectWithProperties::asObjectMask);
    }

    /**
     * Number of objects in the collection.
     *
     * @return the size of the collection.
     */
    public int size() {
        return delegate.size();
    }

    /**
     * Whether the number of objects is zero.
     *
     * @return true when no objects are contained in the collection, false if at least one object
     *     exists.
     */
    public boolean isEmpty() {
        return delegate.isEmpty();
    }
}
