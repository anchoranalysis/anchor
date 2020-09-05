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

package org.anchoranalysis.image.voxel.kernel.dilateerode;

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.Voxels;

// Erosion with a 3x3 or 3x3x3 kernel
public final class DilationKernel3ZOnly extends BinaryKernelMorph3 {

    // Constructor
    public DilationKernel3ZOnly(BinaryValuesByte bv, boolean outsideAtThreshold) {
        super(bv, outsideAtThreshold);
    }

    @Override
    public void init(Voxels<UnsignedByteBuffer> in) {
        // NOTHING TO DO
    }

    @Override
    public boolean acceptPoint(int ind, Point3i point) {

        UnsignedByteBuffer inArrZ = inSlices.getLocal(0);
        UnsignedByteBuffer inArrZLess1 = inSlices.getLocal(-1);
        UnsignedByteBuffer inArrZPlus1 = inSlices.getLocal(+1);

        if (bv.isOn(inArrZ.getRaw(ind))) {
            return true;
        }

        if (inArrZLess1 != null) {
            if (bv.isOn(inArrZLess1.getRaw(ind))) {
                return true;
            }
        } else {
            if (outsideAtThreshold) {
                return true;
            }
        }

        if (inArrZPlus1 != null) {
            if (bv.isOn(inArrZPlus1.getRaw(ind))) {
                return true;
            }
        } else {
            if (outsideAtThreshold) {
                return true;
            }
        }

        return false;
    }
}
