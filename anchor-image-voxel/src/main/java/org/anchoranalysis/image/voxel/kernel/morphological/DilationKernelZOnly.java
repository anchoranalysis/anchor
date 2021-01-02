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

package org.anchoranalysis.image.voxel.kernel.morphological;

import java.util.Optional;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.spatial.point.Point3i;

// Erosion with a 3x3 or 3x3x3 kernel
final class DilationKernelZOnly extends BinaryKernelMorphological {

    @Override
    public void init(Voxels<UnsignedByteBuffer> in, KernelApplicationParameters params) {
        // NOTHING TO DO
    }

    @Override
    public boolean acceptPoint(int index, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params) {

        UnsignedByteBuffer buffer = getVoxels().getLocal(0).get(); // NOSONAR
        Optional<UnsignedByteBuffer> bufferZLess1 = getVoxels().getLocal(-1);
        Optional<UnsignedByteBuffer> bufferZPlus1 = getVoxels().getLocal(+1);

        if (binaryValues.isOn(buffer.getRaw(index))) {
            return true;
        }

        if (bufferZLess1.isPresent()) {
            if (binaryValues.isOn(bufferZLess1.get().getRaw(index))) {
                return true;
            }
        } else {
            if (params.isOutsideHigh()) {
                return true;
            }
        }

        if (bufferZPlus1.isPresent()) {
            if (binaryValues.isOn(bufferZPlus1.get().getRaw(index))) {
                return true;
            }
        } else {
            if (params.isOutsideHigh()) {
                return true;
            }
        }

        return false;
    }
}
