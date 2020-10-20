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

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferByte;
import org.anchoranalysis.image.voxel.buffer.mean.MeanIntensityByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;
import org.anchoranalysis.spatial.Extent;

class ByteImplementation extends Base<UnsignedByteBuffer> {

    public ByteImplementation(Voxels<UnsignedByteBuffer> voxels) {
        super(voxels);
    }

    @Override
    public void copyBufferIndexTo(
            UnsignedByteBuffer sourceBuffer,
            int sourceIndex,
            UnsignedByteBuffer destinationBuffer,
            int destinationIndex) {
        destinationBuffer.putRaw(destinationIndex, sourceBuffer.getRaw(sourceIndex));
    }

    @Override
    protected int voxelAtBufferIndex(UnsignedByteBuffer buffer, int index) {
        return buffer.getUnsigned(index);
    }

    @Override
    public Voxels<UnsignedByteBuffer> projectMax() {

        Extent extent = voxels.extent();

        MaxIntensityBufferByte mi = new MaxIntensityBufferByte(extent);

        for (int z = 0; z < extent.z(); z++) {
            mi.projectSlice(voxels.sliceBuffer(z));
        }

        return mi.asVoxels();
    }

    @Override
    public Voxels<UnsignedByteBuffer> projectMean() {

        Extent extent = voxels.extent();

        MeanIntensityByteBuffer projection = new MeanIntensityByteBuffer(extent);

        for (int z = 0; z < extent.z(); z++) {
            projection.projectSlice(voxels.sliceBuffer(z));
        }

        return projection.getFlatBuffer();
    }

    @Override
    public long voxelWithMaxIntensity() {
        return IterateVoxelsAll.intensityMax(voxels);
    }

    @Override
    protected boolean bufferValueGreaterThan(UnsignedByteBuffer buffer, int threshold) {
        return buffer.getUnsigned() > threshold;
    }

    @Override
    protected boolean bufferValueEqualTo(UnsignedByteBuffer buffer, int value) {
        return buffer.getUnsigned() == value;
    }
}
