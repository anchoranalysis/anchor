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

import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.point.Point3i;

// Erosion with a 3x3 or 3x3x3 kernel
public final class DilationKernel extends BinaryKernelMorphologicalExtent {

    private boolean bigNeighborhood;

    // Constructor
    public DilationKernel(
            BinaryValuesByte bv, boolean outsideAtThreshold, boolean useZ, boolean bigNeighborhood)
            throws CreateException {
        super(bv, outsideAtThreshold, useZ);
        this.bigNeighborhood = bigNeighborhood;

        if (useZ && bigNeighborhood) {
            throw new CreateException(
                    "useZ and bigNeighborhood cannot be simultaneously true, as this mode is not currently supported");
        }
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

        int xLength = extent.x();

        int x = point.x();
        int y = point.y();

        if (binaryValues.isOn(inArrZ.getRaw(ind))) {
            return true;
        }

        // We walk up and down in x
        x--;
        ind--;
        if (x >= 0) {
            if (binaryValues.isOn(inArrZ.getRaw(ind))) {
                return true;
            }
        } else {
            if (outsideAtThreshold) {
                return true;
            }
        }

        x += 2;
        ind += 2;
        if (x < extent.x()) {
            if (binaryValues.isOn(inArrZ.getRaw(ind))) {
                return true;
            }
        } else {
            if (outsideAtThreshold) {
                return true;
            }
        }
        x--;
        ind--;

        // We walk up and down in y
        y--;
        ind -= xLength;
        if (y >= 0) {
            if (binaryValues.isOn(inArrZ.getRaw(ind))) {
                return true;
            }
        } else {
            if (outsideAtThreshold) {
                return true;
            }
        }

        y += 2;
        ind += (2 * xLength);
        if (y < (extent.y())) {
            if (binaryValues.isOn(inArrZ.getRaw(ind))) {
                return true;
            }
        } else {
            if (outsideAtThreshold) {
                return true;
            }
        }
        y--;
        ind -= xLength;

        if (bigNeighborhood) {

            // x-1, y-1

            x--;
            ind--;

            y--;
            ind -= xLength;

            if (x >= 0 && y >= 0) {
                if (binaryValues.isOn(inArrZ.getRaw(ind))) {
                    return true;
                }
            } else {
                if (outsideAtThreshold) {
                    return true;
                }
            }

            // x-1, y+1

            y += 2;
            ind += (2 * xLength);
            if (x >= 0 && y < (extent.y())) {
                if (binaryValues.isOn(inArrZ.getRaw(ind))) {
                    return true;
                }
            } else {
                if (outsideAtThreshold) {
                    return true;
                }
            }
            y--;
            ind -= xLength;

            x += 2;
            ind += 2;

            y--;
            ind -= xLength;

            // x +1, y-1

            if (x < extent.x() && y >= 0) {
                if (binaryValues.isOn(inArrZ.getRaw(ind))) {
                    return true;
                }
            } else {
                if (outsideAtThreshold) {
                    return true;
                }
            }

            // x+1, y+1

            y += 2;
            ind += (2 * xLength);
            if (x < extent.x() && y < (extent.y())) {
                if (binaryValues.isOn(inArrZ.getRaw(ind))) {
                    return true;
                }
            } else {
                if (outsideAtThreshold) {
                    return true;
                }
            }
            ind -= xLength;

            ind--;
        }

        if (useZ) {

            if (inArrZLess1 != null) {
                if (binaryValues.isOn(inArrZLess1.getRaw(ind))) {
                    return true;
                }
            } else {
                if (outsideAtThreshold) {
                    return true;
                }
            }

            if (inArrZPlus1 != null) {
                if (binaryValues.isOn(inArrZPlus1.getRaw(ind))) {
                    return true;
                }
            } else {
                if (outsideAtThreshold) {
                    return true;
                }
            }
        }

        return false;
    }
}