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
package org.anchoranalysis.image.voxel.extracter;

import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferShort;
import org.anchoranalysis.image.voxel.buffer.mean.MeanIntensityShortBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsVoxelBoxAsInt;

class ShortImplementation extends Base<ShortBuffer> {

    public ShortImplementation(Voxels<ShortBuffer> voxels) {
        super(voxels);
    }

    @Override
    public void copyBufferIndexTo(
            ShortBuffer sourceBuffer,
            int sourceIndex,
            ShortBuffer destinationBuffer,
            int destinationIndex) {
        destinationBuffer.put(destinationIndex, sourceBuffer.get(sourceIndex));
    }

    @Override
    public Voxels<ShortBuffer> projectMax() {

        Extent extent = voxels.extent();

        MaxIntensityBufferShort projection = new MaxIntensityBufferShort(extent);

        for (int z = 0; z < extent.z(); z++) {
            projection.projectSlice(voxels.sliceBuffer(z));
        }

        return projection.asVoxels();
    }

    @Override
    public Voxels<ShortBuffer> projectMean() {

        Extent extent = voxels.extent();

        MeanIntensityShortBuffer projection = new MeanIntensityShortBuffer(extent);

        for (int z = 0; z < extent.z(); z++) {
            projection.projectSlice(voxels.sliceBuffer(z));
        }

        return projection.getFlatBuffer();
    }

    @Override
    public int voxelWithMaxIntensity() {
        return IterateVoxelsVoxelBoxAsInt.findMaxValue(voxels);
    }

    @Override
    protected int voxelAtBufferIndex(ShortBuffer buffer, int index) {
        return ByteConverter.unsignedShortToInt(buffer.get(index));
    }

    @Override
    protected boolean bufferValueGreaterThan(ShortBuffer buffer, int threshold) {
        return buffer.get() > threshold;
    }

    @Override
    protected boolean bufferValueEqualTo(ShortBuffer buffer, int value) {
        return buffer.get() == value;
    }
}
