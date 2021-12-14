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
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityProjection;
import org.anchoranalysis.image.voxel.buffer.mean.MeanIntensityProjection;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;
import org.anchoranalysis.spatial.box.Extent;

class UnsignedShortImplementation extends VoxelsExtracterBase<UnsignedShortBuffer> {

    public UnsignedShortImplementation(Voxels<UnsignedShortBuffer> voxels) {
        super(voxels);
    }

    @Override
    public void copySingleVoxelTo(
            UnsignedShortBuffer sourceBuffer,
            int sourceIndex,
            UnsignedShortBuffer destinationBuffer,
            int destinationIndex) {
        destinationBuffer.putRaw(destinationIndex, sourceBuffer.getRaw(sourceIndex));
    }

    @Override
    public long voxelWithMaxIntensity() {
        return IterateVoxelsAll.intensityMax(voxels);
    }

    @Override
    public long voxelWithMinIntensity() {
        return IterateVoxelsAll.intensityMin(voxels);
    }

    @Override
    protected int voxelAtBufferIndex(UnsignedShortBuffer buffer, int index) {
        return buffer.getUnsigned(index);
    }

    @Override
    protected boolean bufferValueGreaterThan(UnsignedShortBuffer buffer, int threshold) {
        return buffer.getUnsigned() > threshold;
    }

    @Override
    protected boolean bufferValueEqualTo(UnsignedShortBuffer buffer, int value) {
        return buffer.getUnsigned() == value;
    }

    @Override
    protected ProjectableBuffer<UnsignedShortBuffer> createMaxIntensityBuffer(Extent extent) {
        return MaxIntensityProjection.createUnsignedShort(extent);
    }

    @Override
    protected ProjectableBuffer<UnsignedShortBuffer> createMeanIntensityBuffer(Extent extent) {
        return MeanIntensityProjection.createUnsignedShort(extent);
    }
}
