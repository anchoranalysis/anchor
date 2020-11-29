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
package org.anchoranalysis.image.core.object.scale;

import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Provides functions to provide access and creation to/from the object-mask representation of a
 * generic type.
 *
 * <p>The purpose is to allow both {@link ObjectMask} and other types that may contain a {@link
 * ObjectMask} to be scaled, with generic functions providing access and creation to/from the
 * object-mask representation.
 *
 * @author Owen Feehan
 * @param <T> element-type, either an object-mask or has an object-mask representation.
 */
public interface AccessObjectMask<T> {

    /**
     * An object-mask for a given element.
     *
     * <p>This operation is assumed to involve negligible computational cost.
     *
     * @param element the element
     * @return the object-mask
     */
    ObjectMask objectFor(T element);

    /**
     * Positionally-shifts an element by a given quantity in the positive direction.
     *
     * @param element the element to shift by
     * @param quantity the quantity to shift by
     * @return a newly created element based on {@code element} but positionally-shifted.
     */
    T shiftBy(T element, ReadableTuple3i quantity);

    /**
     * Ensures the element lies within a certain extent.
     *
     * @param extent the extent to clip to
     * @return either a newly created element or the existing element (if no change needs to occur)
     */
    T clipTo(T element, Extent extent);

    /**
     * Creates an element of type {@code T} from an object-representation and index.
     *
     * @param index the index of the object-representation in terms of the original list
     * @param object an object-representation corresponding to this index
     * @return a newly created element corresponding to the object-representation.
     */
    T createFrom(int index, ObjectMask object);

    /**
     * A bounding-box for a given element.
     *
     * <p>This operation is assumed to involve negligible computational cost.
     *
     * @param element the element
     * @return the bounding box.
     */
    default BoundingBox boundingBoxFor(T element) {
        return objectFor(element).boundingBox();
    }
}
