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

package org.anchoranalysis.image.voxel.iterator.neighbor;

import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import lombok.AllArgsConstructor;

/**
 * Wraps a {@link ProcessVoxelNeighborAbsolute} as a {@link ProcessChangedPointAbsoluteMasked}.
 *
 * @param <T> result-type that can be collected after processing
 */
@AllArgsConstructor
final class WrapAbsoluteAsMasked<T> implements ProcessChangedPointAbsoluteMasked<T> {

    private final ProcessVoxelNeighborAbsolute<T> delegate;

    @Override
    public void initSource(int sourceValue, int sourceOffsetXY) {
        delegate.initSource(sourceValue, sourceOffsetXY);
    }

    @Override
    public void notifyChangeZ(int zChange, int z, UnsignedByteBuffer objectMaskBuffer) {
        delegate.notifyChangeZ(zChange, z);
    }

    @Override
    public void processPoint(int xChange, int yChange, int x, int y, int objectMaskOffset) {
        delegate.processPoint(xChange, yChange, x, y);
    }

    @Override
    public T collectResult() {
        return delegate.collectResult();
    }
}
