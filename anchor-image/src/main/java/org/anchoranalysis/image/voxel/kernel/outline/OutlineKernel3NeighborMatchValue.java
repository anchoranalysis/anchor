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

package org.anchoranalysis.image.voxel.kernel.outline;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

// Keeps any on pixel that touches an off pixel where the off pixel has a corresponding HIGH value
// in vbRequireHigh
public class OutlineKernel3NeighborMatchValue extends OutlineKernel3Base {

    private BinaryVoxelBox<ByteBuffer> vbRequireHigh;
    private LocalSlices localSlicesRequireHigh;
    private BinaryValuesByte bvRequireHigh;
    private ObjectMask object;

    public OutlineKernel3NeighborMatchValue(
            boolean outsideAtThreshold,
            boolean useZ,
            ObjectMask object,
            BinaryVoxelBox<ByteBuffer> vbRequireHigh,
            boolean ignoreAtThreshold) {
        this(
                object.getBinaryValuesByte(),
                outsideAtThreshold,
                useZ,
                object,
                vbRequireHigh,
                ignoreAtThreshold);
    }

    // Constructor
    private OutlineKernel3NeighborMatchValue(
            BinaryValuesByte bv,
            boolean outsideAtThreshold,
            boolean useZ,
            ObjectMask object,
            BinaryVoxelBox<ByteBuffer> vbRequireHigh,
            boolean ignoreAtThreshold) {
        super(bv, outsideAtThreshold, useZ, ignoreAtThreshold);
        this.vbRequireHigh = vbRequireHigh;
        this.object = object;
        this.bvRequireHigh = vbRequireHigh.getBinaryValues().createByte();
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        super.notifyZChange(inSlices, z);
        localSlicesRequireHigh =
                new LocalSlices(
                        z + object.getBoundingBox().cornerMin().getZ(),
                        3,
                        vbRequireHigh.getVoxelBox());
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

        ByteBuffer inArrZ = inSlices.getLocal(0);
        ByteBuffer inArrZLess1 = inSlices.getLocal(-1);
        ByteBuffer inArrZPlus1 = inSlices.getLocal(+1);

        ByteBuffer inArrR = localSlicesRequireHigh.getLocal(0);
        ByteBuffer inArrRLess1 = localSlicesRequireHigh.getLocal(-1);
        ByteBuffer inArrRPlus1 = localSlicesRequireHigh.getLocal(+1);

        int xLength = extent.getX();

        int x = point.getX();
        int y = point.getY();

        if (bv.isOff(inArrZ.get(ind))) {
            return false;
        }

        // We walk up and down in x
        x--;
        ind--;
        if (x >= 0) {
            if (bv.isOff(inArrZ.get(ind))) {
                return checkIfRequireHighIsTrue(inArrR, point, -1, 0);
            }
        } else {
            if (!ignoreAtThreshold && !outsideAtThreshold) {
                return checkIfRequireHighIsTrue(inArrR, point, -1, 0);
            }
        }

        x += 2;
        ind += 2;
        if (x < extent.getX()) {
            if (bv.isOff(inArrZ.get(ind))) {
                return checkIfRequireHighIsTrue(inArrR, point, +1, 0);
            }
        } else {
            if (!ignoreAtThreshold && !outsideAtThreshold) {
                return checkIfRequireHighIsTrue(inArrR, point, +1, 0);
            }
        }
        ind--;

        // We walk up and down in y
        y--;
        ind -= xLength;
        if (y >= 0) {
            if (bv.isOff(inArrZ.get(ind))) {
                return checkIfRequireHighIsTrue(inArrR, point, 0, -1);
            }
        } else {
            if (!ignoreAtThreshold && !outsideAtThreshold) {
                return checkIfRequireHighIsTrue(inArrR, point, 0, -1);
            }
        }

        y += 2;
        ind += (2 * xLength);
        if (y < (extent.getY())) {
            if (bv.isOff(inArrZ.get(ind))) {
                return checkIfRequireHighIsTrue(inArrR, point, 0, +1);
            }
        } else {
            if (!ignoreAtThreshold && !outsideAtThreshold) {
                return checkIfRequireHighIsTrue(inArrR, point, 0, +1);
            }
        }
        ind -= xLength;

        if (useZ) {

            if (inArrZLess1 != null) {
                if (bv.isOff(inArrZLess1.get(ind))) {
                    return checkIfRequireHighIsTrue(inArrRLess1, point, 0, 0);
                }
            } else {
                if (!ignoreAtThreshold && !outsideAtThreshold) {
                    return checkIfRequireHighIsTrue(inArrRLess1, point, 0, 0);
                }
            }

            if (inArrZPlus1 != null) {
                if (bv.isOff(inArrZPlus1.get(ind))) {
                    return checkIfRequireHighIsTrue(inArrRPlus1, point, 0, 0);
                }
            } else {
                if (!ignoreAtThreshold && !outsideAtThreshold) {
                    return checkIfRequireHighIsTrue(inArrRPlus1, point, 0, 0);
                }
            }
        }

        return false;
    }

    private boolean checkIfRequireHighIsTrue(
            ByteBuffer inArr, Point3i point, int xShift, int yShift) {

        if (inArr == null) {
            return outsideAtThreshold;
        }

        int x1 = point.getX() + object.getBoundingBox().cornerMin().getX() + xShift;

        if (!vbRequireHigh.extent().containsX(x1)) {
            return outsideAtThreshold;
        }

        int y1 = point.getY() + object.getBoundingBox().cornerMin().getY() + yShift;

        if (!vbRequireHigh.extent().containsY(y1)) {
            return outsideAtThreshold;
        }

        int intGlobal = vbRequireHigh.extent().offset(x1, y1);
        return bvRequireHigh.isOn(inArr.get(intGlobal));
    }
}