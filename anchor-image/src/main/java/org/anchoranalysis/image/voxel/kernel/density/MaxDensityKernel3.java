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
/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel.density;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

//

/**
 * Erosion with a 3x3 or 3x3x3 kernel
 *
 * @author Owen Feehan
 */
public class MaxDensityKernel3 extends BinaryKernel {

    private boolean outsideAtThreshold = false;

    private boolean useZ;

    private BinaryValuesByte bv;
    private int maxCnt;

    private LocalSlices inSlices;

    private Extent extent;

    // Constructor
    public MaxDensityKernel3(
            BinaryValuesByte bv, boolean outsideAtThreshold, boolean useZ, int maxCnt) {
        super(3);
        this.outsideAtThreshold = outsideAtThreshold;
        this.useZ = useZ;
        this.bv = bv;
        this.maxCnt = maxCnt;
    }

    @Override
    public void init(VoxelBox<ByteBuffer> in) {
        this.extent = in.extent();
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        this.inSlices = inSlices;
    }

    /**
     * This method is deliberately not broken into smaller pieces to avoid inlining.
     *
     * <p>This efficiency matters as it is called so many times over a large image.
     *
     * <p>Apologies that it is difficult to read with high cognitive-complexity.
     */
    @Override
    public boolean accptPos(int ind, Point3i point) {

        int cnt = 0;

        ByteBuffer inArrZ = inSlices.getLocal(0);
        ByteBuffer inArrZLess1 = inSlices.getLocal(-1);
        ByteBuffer inArrZPlus1 = inSlices.getLocal(+1);

        int xLength = extent.getX();

        int x = point.getX();
        int y = point.getY();

        if (bv.isOn(inArrZ.get(ind))) {
            cnt++;
        }

        // We walk up and down in x
        x--;
        ind--;
        if (x >= 0) {
            if (bv.isOn(inArrZ.get(ind))) {
                cnt++;
            }
        } else {
            if (outsideAtThreshold) {
                cnt++;
            }
        }

        x += 2;
        ind += 2;
        if (x < extent.getX()) {
            if (bv.isOn(inArrZ.get(ind))) {
                cnt++;
            }
        } else {
            if (outsideAtThreshold) {
                cnt++;
            }
        }
        ind--;

        // We walk up and down in y
        y--;
        ind -= xLength;
        if (y >= 0) {
            if (bv.isOn(inArrZ.get(ind))) {
                cnt++;
            }
        } else {
            if (outsideAtThreshold) {
                cnt++;
            }
        }

        y += 2;
        ind += (2 * xLength);
        if (y < (extent.getY())) {
            if (bv.isOn(inArrZ.get(ind))) {
                cnt++;
            }
        } else {
            if (outsideAtThreshold) {
                cnt++;
            }
        }
        ind -= xLength;

        if (useZ) {

            if (inArrZLess1 != null) {
                if (bv.isOn(inArrZLess1.get(ind))) {
                    cnt++;
                }
            } else {
                if (outsideAtThreshold) {
                    cnt++;
                }
            }

            if (inArrZPlus1 != null) {
                if (bv.isOn(inArrZPlus1.get(ind))) {
                    cnt++;
                }
            } else {
                if (outsideAtThreshold) {
                    cnt++;
                }
            }
        }

        return cnt <= maxCnt;
    }
}
