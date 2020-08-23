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

import java.nio.IntBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferInt;

class IntImplementation extends Base<IntBuffer> {

    public IntImplementation(Voxels<IntBuffer> voxels) {
        super(voxels);
    }

    @Override
    public Voxels<IntBuffer> projectMax() {

        Extent extent = voxels.extent();

        MaxIntensityBufferInt mi = new MaxIntensityBufferInt(extent);

        for (int z = 0; z < extent.z(); z++) {
            mi.projectSlice(voxels.sliceBuffer(z));
        }

        return mi.asVoxels();
    }

    @Override
    public Voxels<IntBuffer> projectMean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int voxelWithMaxIntensity() {

        int max = 0;
        boolean first = true;

        Extent extent = voxels.extent();

        for (int z = 0; z < extent.z(); z++) {

            IntBuffer pixels = voxels.sliceBuffer(z);

            while (pixels.hasRemaining()) {

                int val = pixels.get();
                if (first || val > max) {
                    max = val;
                    first = false;
                }
            }
        }
        return max;
    }

    @Override
    protected void copyBufferIndexTo(
            IntBuffer sourceBuffer,
            int sourceIndex,
            IntBuffer destinationBuffer,
            int destinationIndex) {
        destinationBuffer.put(destinationIndex, sourceBuffer.get(sourceIndex));
    }

    @Override
    protected int voxelAtBufferIndex(IntBuffer buffer, int index) {
        return buffer.get(index);
    }

    @Override
    protected boolean bufferValueGreaterThan(IntBuffer buffer, int threshold) {
        return buffer.get() > threshold;
    }

    @Override
    protected boolean bufferValueEqualTo(IntBuffer buffer, int value) {
        return buffer.get() == value;
    }
}
