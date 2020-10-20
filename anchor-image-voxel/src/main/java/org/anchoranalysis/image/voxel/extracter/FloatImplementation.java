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
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferFloat;
import org.anchoranalysis.spatial.Extent;

class FloatImplementation extends Base<FloatBuffer> {

    public FloatImplementation(Voxels<FloatBuffer> voxels) {
        super(voxels);
    }

    @Override
    public void copyBufferIndexTo(
            FloatBuffer sourceBuffer,
            int sourceIndex,
            FloatBuffer destinationBuffer,
            int destinationIndex) {
        destinationBuffer.put(destinationIndex, sourceBuffer.get(sourceIndex));
    }

    @Override
    public Voxels<FloatBuffer> projectMax() {

        Extent extent = voxels.extent();

        MaxIntensityBufferFloat mi = new MaxIntensityBufferFloat(extent);

        for (int z = 0; z < extent.z(); z++) {
            mi.projectSlice(voxels.sliceBuffer(z));
        }

        return mi.asVoxels();
    }

    @Override
    public Voxels<FloatBuffer> projectMean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long voxelWithMaxIntensity() {

        float max = 0;
        boolean first = true;

        Extent extent = voxels.extent();

        for (int z = 0; z < extent.z(); z++) {

            FloatBuffer pixels = voxels.sliceBuffer(z);

            while (pixels.hasRemaining()) {

                float val = pixels.get();
                if (first || val > max) {
                    max = val;
                    first = false;
                }
            }
        }
        return (long) Math.ceil(max);
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
}
