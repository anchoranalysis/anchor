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

package org.anchoranalysis.image.voxel.buffer.mean;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.spatial.Extent;

/**
 * The buffer used when making a <i>mean-intensity projection</i>.
 *
 * @author Owen Feehan
 * @param <T> type of buffer used, both as input and result, of the maximum intensity projection
 */
class MeanIntensityBuffer<T> implements ProjectableBuffer<T> {

    private Voxels<T> projection;
    private Voxels<FloatBuffer> voxelsSum;
    private int countSlices = 0;

    /** Simple constructor since no preprocessing is necessary. */
    public MeanIntensityBuffer(VoxelsFactoryTypeBound<T> flatType, Extent extent) {
        Extent extentForProjection = extent.flattenZ();
        projection = flatType.createInitialized(extentForProjection);
        voxelsSum = VoxelsFactory.getFloat().createInitialized(extentForProjection);
    }

    @Override
    public void addSlice(VoxelBuffer<T> voxels) {
        projection
                .extent()
                .iterateOverXYOffset(offset -> incrementSumBuffer(offset, voxels.getInt(offset)));
        countSlices++;
    }

    @Override
    public Voxels<T> completeProjection() {
        projection.arithmetic().divideBy(countSlices);
        return projection;
    }

    /** Increments a particular offset in the sum bufffer by a certain amount */
    private void incrementSumBuffer(int index, int toAdd) {
        FloatBuffer sumBuffer = voxelsSum.sliceBuffer(0);
        sumBuffer.put(index, sumBuffer.get(index) + toAdd);
    }
}
