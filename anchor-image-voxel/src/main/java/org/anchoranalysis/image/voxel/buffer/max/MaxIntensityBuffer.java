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

package org.anchoranalysis.image.voxel.buffer.max;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.spatial.extent.Extent;

/**
 * The buffer used when making a maximum-intensity projection
 *
 * @author Owen Feehan
 * @param <T> type of buffer used, both as input and result, of the maximum intesnity projection
 */
public abstract class MaxIntensityBuffer<T> {

    /** Target buffer, where the maximum-intensity pixels are stored */
    private Voxels<T> target;

    public MaxIntensityBuffer(Extent extent, VoxelsFactoryTypeBound<T> factory) {
        target = factory.createInitialized(extent.flattenZ());
    }

    public void projectSlice(T pixels) {

        VoxelBuffer<T> flatBuffer = target.slice(0);

        while (flatBuffer.hasRemaining()) {
            addBuffer(pixels, flatBuffer.buffer());
        }
    }

    /**
     * The target buffer
     *
     * @return the result of the maximum-intensity projection
     */
    public Voxels<T> asVoxels() {
        return target;
    }

    protected abstract void addBuffer(T anySlice, T target);
}
