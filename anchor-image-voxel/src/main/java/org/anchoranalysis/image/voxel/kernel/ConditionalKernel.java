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

package org.anchoranalysis.image.voxel.kernel;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.point.Point3i;

// Erosion with a 3x3 or 3x3x3 kernel
public class ConditionalKernel extends BinaryKernel {

    private BinaryKernel kernel;
    private int minValue;
    private Voxels<UnsignedByteBuffer> voxelsIntensity;

    // Constructor
    public ConditionalKernel(
            BinaryKernel kernel, int minValue, Voxels<UnsignedByteBuffer> voxelsIntensity) {
        super(kernel.getSize());
        this.kernel = kernel;
        this.minValue = minValue;
        this.voxelsIntensity = voxelsIntensity;
    }

    @Override
    public boolean acceptPoint(int ind, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params) {

        int value =
                voxelsIntensity
                        .sliceBuffer(point.z())
                        .getUnsigned(voxelsIntensity.extent().offsetSlice(point));

        if (value < minValue) {
            return false;
        }

        return kernel.acceptPoint(ind, point, binaryValues, params);
    }

    @Override
    public void init(Voxels<UnsignedByteBuffer> in, KernelApplicationParameters params) {
        kernel.init(in, params);
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        kernel.notifyZChange(inSlices, z);
    }
}
