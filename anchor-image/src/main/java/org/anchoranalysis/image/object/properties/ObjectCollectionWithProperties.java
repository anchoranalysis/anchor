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

package org.anchoranalysis.image.object.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Like an {@ObjectCollection} but each object has associated properties.
 *
 * @author Owen Feehan
 */
public class ObjectCollectionWithProperties implements Iterable<ObjectWithProperties> {

    private final List<ObjectWithProperties> delegate;

    public ObjectCollectionWithProperties() {
        delegate = new ArrayList<>();
    }

    public ObjectCollectionWithProperties(ObjectMask object) {
        this(ObjectCollectionFactory.of(object));
    }

    public ObjectCollectionWithProperties(ObjectCollection objects) {
        delegate = objects.stream().mapToList(ObjectWithProperties::new);
    }

    public boolean add(ObjectMask object) {
        return delegate.add(new ObjectWithProperties(object));
    }

    public boolean add(ObjectWithProperties object) {
        return delegate.add(object);
    }

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
     * Returns the contained-objects without corresponding properties
     *
     * <p>This is an IMMUTABLE operation.
     */
    public ObjectCollection withoutProperties() {
        return ObjectCollectionFactory.mapFrom(delegate, ObjectWithProperties::getMask);
    }
}
