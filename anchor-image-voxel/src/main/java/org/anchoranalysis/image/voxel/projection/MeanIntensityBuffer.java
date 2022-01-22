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

package org.anchoranalysis.image.voxel.projection;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.convert.VoxelsConverterMulti;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.spatial.box.Extent;

/**
 * The buffer used when making a <i>mean-intensity projection</i>.
 *
 * @author Owen Feehan
 * @param <T> type of buffer used, both as input and result, of the maximum intensity projection
 */
class MeanIntensityBuffer<T> implements ProjectableBuffer<T> {

    private static final VoxelsConverterMulti CONVERTER = new VoxelsConverterMulti();

    private Voxels<FloatBuffer> voxelsSum;
    private int count = 0;
    private final VoxelsFactoryTypeBound<T> factory;

    /**
     * Creates with minimal parameters, as no preprocessing is necessary.
     *
     * @param factory the voxel data-type to use for to create the (mean-intensity) buffer.
     * @param extent the size expected for images that will be projected.
     */
    public MeanIntensityBuffer(VoxelsFactoryTypeBound<T> factory, Extent extent) {
        this.factory = factory;
        this.voxelsSum = VoxelsFactory.getFloat().createInitialized(extent);
    }

    @Override
    public void addVoxelBuffer(VoxelBuffer<T> voxelBuffer) {
        addVoxelBufferInternal(voxelBuffer, 0);
        count++;
    }

    @Override
    public void addVoxels(Voxels<T> voxels) {
        for (int z = 0; z < voxels.extent().z(); z++) {
            addVoxelBufferInternal(voxels.slice(z), z);
        }
        count++;
    }

    @Override
    public Voxels<T> completeProjection() {
        voxelsSum.arithmetic().divideBy(count);
        return CONVERTER.convert(new VoxelsUntyped(voxelsSum), factory);
    }

    /** Adds a {@link VoxelsBuffer} without incrementing the count. */
    private void addVoxelBufferInternal(VoxelBuffer<T> voxelBuffer, int z) {
        FloatBuffer sumBuffer = voxelsSum.sliceBuffer(z);
        voxelsSum
                .extent()
                .iterateOverXYOffset(
                        offset ->
                                incrementSumBuffer(offset, voxelBuffer.getInt(offset), sumBuffer));
    }

    /** Increments a particular offset in the sum buffer by a certain amount */
    private void incrementSumBuffer(int index, int toAdd, FloatBuffer sumBuffer) {
        sumBuffer.put(index, sumBuffer.get(index) + toAdd);
    }
}
