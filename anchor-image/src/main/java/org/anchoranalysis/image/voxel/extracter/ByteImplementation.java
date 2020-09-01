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

import java.nio.ByteBuffer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferByte;
import org.anchoranalysis.image.voxel.buffer.mean.MeanIntensityByteBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsVoxelBoxAsInt;

class ByteImplementation extends Base<ByteBuffer> {

    public ByteImplementation(Voxels<ByteBuffer> voxels) {
        super(voxels);
    }

    @Override
    public void copyBufferIndexTo(
            ByteBuffer sourceBuffer,
            int sourceIndex,
            ByteBuffer destinationBuffer,
            int destinationIndex) {
        destinationBuffer.put(destinationIndex, sourceBuffer.get(sourceIndex));
    }

    @Override
    protected int voxelAtBufferIndex(ByteBuffer buffer, int index) {
        return ByteConverter.unsignedByteToInt(buffer.get(index));
    }

    @Override
    public Voxels<ByteBuffer> projectMax() {

        Extent extent = voxels.extent();

        MaxIntensityBufferByte mi = new MaxIntensityBufferByte(extent);

        for (int z = 0; z < extent.z(); z++) {
            mi.projectSlice(voxels.sliceBuffer(z));
        }

        return mi.asVoxels();
    }

    @Override
    public Voxels<ByteBuffer> projectMean() {

        Extent extent = voxels.extent();

        MeanIntensityByteBuffer mi = new MeanIntensityByteBuffer(extent);

        for (int z = 0; z < extent.z(); z++) {
            mi.projectSlice(voxels.sliceBuffer(z));
        }

        return mi.getFlatBuffer();
    }

    @Override
    public int voxelWithMaxIntensity() {
        return IterateVoxelsVoxelBoxAsInt.findMaxValue(voxels);
    }

    @Override
    protected boolean bufferValueGreaterThan(ByteBuffer buffer, int threshold) {
        return ByteConverter.unsignedByteToInt(buffer.get()) > threshold;
    }

    @Override
    protected boolean bufferValueEqualTo(ByteBuffer buffer, int value) {
        return ByteConverter.unsignedByteToInt(buffer.get()) == value;
    }
}
