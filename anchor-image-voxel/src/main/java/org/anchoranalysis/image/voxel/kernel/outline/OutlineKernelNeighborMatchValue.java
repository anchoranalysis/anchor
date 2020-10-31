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

import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.point.Point3i;

// Keeps any on pixel that touches an off pixel where the off pixel has a corresponding HIGH value
// in voxelsRequireHigh
public class OutlineKernelNeighborMatchValue extends OutlineKernelBase {

    private BinaryVoxels<UnsignedByteBuffer> voxelsRequireHigh;
    private LocalSlices localSlicesRequireHigh;
    private BinaryValuesByte bvRequireHigh;
    private ObjectMask object;

    public OutlineKernelNeighborMatchValue(
            ObjectMask object,
            BinaryVoxels<UnsignedByteBuffer> voxelsRequireHigh,
            OutlineKernelParameters params) {
        this(object.binaryValuesByte(), object, voxelsRequireHigh, params);
    }

    // Constructor
    private OutlineKernelNeighborMatchValue(
            BinaryValuesByte bv,
            ObjectMask object,
            BinaryVoxels<UnsignedByteBuffer> voxelsRequireHigh,
            OutlineKernelParameters params) {
        super(bv, params);
        this.voxelsRequireHigh = voxelsRequireHigh;
        this.object = object;
        this.bvRequireHigh = voxelsRequireHigh.binaryValues().createByte();
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        super.notifyZChange(inSlices, z);
        localSlicesRequireHigh =
                new LocalSlices(
                        z + object.boundingBox().cornerMin().z(), 3, voxelsRequireHigh.voxels());
    }

    /**
     * This method is deliberately not broken into smaller pieces to avoid inlining.
     *
     * <p>This efficiency matters as it is called so many times over a large image.
     *
     * <p>Apologies that it is difficult to read with high cognitive-complexity.
     */
    @Override
    public boolean acceptPoint(int ind, Point3i point) {

        UnsignedByteBuffer inArrZ = inSlices.getLocal(0);
        UnsignedByteBuffer inArrZLess1 = inSlices.getLocal(-1);
        UnsignedByteBuffer inArrZPlus1 = inSlices.getLocal(+1);

        UnsignedByteBuffer inArrR = localSlicesRequireHigh.getLocal(0);
        UnsignedByteBuffer inArrRLess1 = localSlicesRequireHigh.getLocal(-1);
        UnsignedByteBuffer inArrRPlus1 = localSlicesRequireHigh.getLocal(+1);

        int xLength = extent.x();

        int x = point.x();
        int y = point.y();

        if (binaryValues.isOff(inArrZ.getRaw(ind))) {
            return false;
        }

        // We walk up and down in x
        x--;
        ind--;
        if (x >= 0) {
            if (binaryValues.isOff(inArrZ.getRaw(ind))) {
                return checkIfRequireHighIsTrue(inArrR, point, -1, 0);
            }
        } else {
            if (!ignoreAtThreshold && !outsideAtThreshold) {
                return checkIfRequireHighIsTrue(inArrR, point, -1, 0);
            }
        }

        x += 2;
        ind += 2;
        if (x < extent.x()) {
            if (binaryValues.isOff(inArrZ.getRaw(ind))) {
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
            if (binaryValues.isOff(inArrZ.getRaw(ind))) {
                return checkIfRequireHighIsTrue(inArrR, point, 0, -1);
            }
        } else {
            if (!ignoreAtThreshold && !outsideAtThreshold) {
                return checkIfRequireHighIsTrue(inArrR, point, 0, -1);
            }
        }

        y += 2;
        ind += (2 * xLength);
        if (y < (extent.y())) {
            if (binaryValues.isOff(inArrZ.getRaw(ind))) {
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
                if (binaryValues.isOff(inArrZLess1.getRaw(ind))) {
                    return checkIfRequireHighIsTrue(inArrRLess1, point, 0, 0);
                }
            } else {
                if (!ignoreAtThreshold && !outsideAtThreshold) {
                    return checkIfRequireHighIsTrue(inArrRLess1, point, 0, 0);
                }
            }

            if (inArrZPlus1 != null) {
                if (binaryValues.isOff(inArrZPlus1.getRaw(ind))) {
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
            UnsignedByteBuffer inArr, Point3i point, int xShift, int yShift) {

        if (inArr == null) {
            return outsideAtThreshold;
        }

        int x1 = point.x() + object.boundingBox().cornerMin().x() + xShift;

        if (!voxelsRequireHigh.extent().containsX(x1)) {
            return outsideAtThreshold;
        }

        int y1 = point.y() + object.boundingBox().cornerMin().y() + yShift;

        if (!voxelsRequireHigh.extent().containsY(y1)) {
            return outsideAtThreshold;
        }

        int intGlobal = voxelsRequireHigh.extent().offset(x1, y1);
        return bvRequireHigh.isOn(inArr.getRaw(intGlobal));
    }
}