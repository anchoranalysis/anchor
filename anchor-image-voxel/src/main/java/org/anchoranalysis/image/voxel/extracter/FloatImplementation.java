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

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.iterator.MinMaxRange;
import org.anchoranalysis.image.voxel.projection.MeanIntensityProjection;
import org.anchoranalysis.image.voxel.projection.extrema.MaxIntensityProjection;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Implementation of {@link VoxelsExtracter} where voxels have <b>float</b> type.
 *
 * @author Owen Feehan
 */
class FloatImplementation extends VoxelsExtracterBase<FloatBuffer> {

    public FloatImplementation(Voxels<FloatBuffer> voxels) {
        super(voxels);
    }

    @Override
    public void copySingleVoxelTo(
            FloatBuffer sourceBuffer,
            int sourceIndex,
            FloatBuffer destinationBuffer,
            int destinationIndex) {
        destinationBuffer.put(destinationIndex, sourceBuffer.get(sourceIndex));
    }

    @Override
    public long voxelWithMaxIntensity() {

        float max = Float.MIN_VALUE;

        Extent extent = voxels.extent();

        for (int z = 0; z < extent.z(); z++) {

            FloatBuffer pixels = voxels.sliceBuffer(z);

            while (pixels.hasRemaining()) {

                float value = pixels.get();
                if (value > max) {
                    max = value;
                }
            }
        }
        return (long) Math.ceil(max);
    }

    @Override
    public long voxelWithMinIntensity() {

        float min = Float.MAX_VALUE;

        Extent extent = voxels.extent();

        for (int z = 0; z < extent.z(); z++) {

            FloatBuffer pixels = voxels.sliceBuffer(z);

            while (pixels.hasRemaining()) {

                float value = pixels.get();
                if (value < min) {
                    min = value;
                }
            }
        }
        return (long) Math.floor(min);
    }

    @Override
    public MinMaxRange voxelsWithMinMaxIntensity() {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        Extent extent = voxels.extent();

        for (int z = 0; z < extent.z(); z++) {

            FloatBuffer pixels = voxels.sliceBuffer(z);

            while (pixels.hasRemaining()) {

                float value = pixels.get();
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
            }
        }
        return new MinMaxRange((long) Math.floor(min), (long) Math.ceil(max));
    }

    @Override
    protected int voxelAtBufferIndex(FloatBuffer buffer, int index) {
        return (int) buffer.get(index);
    }

    @Override
    protected boolean bufferValueGreaterThan(FloatBuffer buffer, int threshold) {
        return buffer.get() > threshold;
    }

    @Override
    protected boolean bufferValueEqualTo(FloatBuffer buffer, int value) {
        return buffer.get() == value;
    }

    @Override
    protected ProjectableBuffer<FloatBuffer> createMaxIntensityBuffer(Extent extent) {
        return new MaxIntensityProjection().createFloat(extent);
    }

    @Override
    protected ProjectableBuffer<FloatBuffer> createMeanIntensityBuffer(Extent extent) {
        return new MeanIntensityProjection().createFloat(extent);
    }
}
