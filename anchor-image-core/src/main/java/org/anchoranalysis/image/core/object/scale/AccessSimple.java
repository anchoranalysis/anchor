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
import org.anchoranalysis.spatial.extent.Extent;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Provides access to the simple-case of a list of {@link ObjectMask}s.
 *
 * @author Owen Feehan
 */
class AccessSimple implements AccessObjectMask<ObjectMask> {

    @Override
    public ObjectMask objectFor(ObjectMask element) {
        return element;
    }

    @Override
    public ObjectMask shiftBy(ObjectMask element, ReadableTuple3i quantity) {
        return element.shiftBy(quantity);
    }

    @Override
    public ObjectMask createFrom(int index, ObjectMask object) {
        return object;
    }

    @Override
    public ObjectMask clipTo(ObjectMask element, Extent extent) {
        return element.clipTo(extent);
    }
}
