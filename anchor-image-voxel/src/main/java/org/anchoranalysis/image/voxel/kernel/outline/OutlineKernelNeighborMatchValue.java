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

import java.util.Optional;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Outputs the outline of an object-mask, but only for voxels on the exterior which neighbour a binary-mask.
 * 
 * <p>Specifically, voxels on the object are set only to <i>on</i> if they neighbour a <i>off</i> voxel <b>and</b> this neighboring voxel is <i>on</i> in the binary-mask. Otherwise
 * a voxel is <i>off</i>.
 * 
 * @author Owen Feehan
 *
 */
public class OutlineKernelNeighborMatchValue extends OutlineKernelBase {

    private final BinaryVoxels<UnsignedByteBuffer> voxelsRequireHigh;
    private final BinaryValuesByte bvRequireHigh;
    
    private LocalSlices localSlicesRequireHigh;

    /**
     * Creates for an object.
     *  
     * @param mask the mask defining possible neighbors, defined on the same coordinate space as {@code object}.
     */
    public OutlineKernelNeighborMatchValue(
            BinaryVoxels<UnsignedByteBuffer> mask
            ) {
        this.voxelsRequireHigh = mask;
        this.bvRequireHigh = voxelsRequireHigh.binaryValues().createByte();
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        super.notifyZChange(inSlices, z);
        localSlicesRequireHigh =
                new LocalSlices(
                        z, 3, voxelsRequireHigh.voxels());
    }

    /**
     * This method is deliberately not broken into smaller pieces to avoid inlining.
     *
     * <p>This efficiency matters as it is called so many times over a large image.
     *
     * <p>Apologies that it is difficult to read with high cognitive-complexity.
     */
    @Override
    public boolean acceptPoint(int ind, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params) {

        UnsignedByteBuffer inArrZ = getVoxels().getLocal(0).get(); // NOSONAR
        Optional<UnsignedByteBuffer> inArrZLess1 = getVoxels().getLocal(-1);
        Optional<UnsignedByteBuffer> inArrZPlus1 = getVoxels().getLocal(+1);

        Optional<UnsignedByteBuffer> inArrR = localSlicesRequireHigh.getLocal(0); // NOSONAR
        Optional<UnsignedByteBuffer> requireSlicesLess1 = localSlicesRequireHigh.getLocal(-1);
        Optional<UnsignedByteBuffer> requireSlicesPlus1 = localSlicesRequireHigh.getLocal(+1);

        int xLength = extent.x();
                
        if (binaryValues.isOff(inArrZ.getRaw(ind))) {
            return false;
        }

        int x = point.x();
        int y = point.y();
                
        // We walk up and down in x
        x--;
        ind--;
        if (x >= 0) {
            if (binaryValues.isOff(inArrZ.getRaw(ind)) && checkIfRequireHighIsTrue(inArrR, point, -1, 0, params)) {
                return true;
            }
        } else {
            if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                return true;
            }
        }

        x += 2;
        ind += 2;
        if (x < extent.x()) {
            if (binaryValues.isOff(inArrZ.getRaw(ind)) && checkIfRequireHighIsTrue(inArrR, point, +1, 0, params)) {
                return true;
            }
        } else {
            if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                return true;
            }
        }
        ind--;

        // We walk up and down in y
        y--;
        ind -= xLength;
        if (y >= 0) {
            if (binaryValues.isOff(inArrZ.getRaw(ind)) && checkIfRequireHighIsTrue(inArrR, point, 0, -1, params)) {
                return true;
            }
        } else {
            if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                return true;
            }
        }

        y += 2;
        ind += (2 * xLength);
        if (y < (extent.y())) {
            if (binaryValues.isOff(inArrZ.getRaw(ind)) && checkIfRequireHighIsTrue(inArrR, point, 0, +1, params)) {
                return true;
            }
        } else {
            if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                return true;
            }
        }
        ind -= xLength;

        if (params.isUseZ()) {
            
            if (inArrZLess1.isPresent()) {
                if (binaryValues.isOff(inArrZLess1.get().getRaw(ind)) && checkIfRequireHighIsTrue(requireSlicesLess1, point, 0, 0, params)) {
                    return true;    // NOSONAR
                }
            } else {
                if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                    return true;    // NOSONAR
                }
            }

            if (inArrZPlus1.isPresent()) {
                if (binaryValues.isOff(inArrZPlus1.get().getRaw(ind)) && checkIfRequireHighIsTrue(requireSlicesPlus1, point, 0, 0, params)) {
                    return true;    // NOSONAR
                }
            } else {
                if (!params.isIgnoreOutside() && !params.isOutsideHigh()) {
                    return true;    // NOSONAR
                }
            }
        }

        return false;
    }

    private boolean checkIfRequireHighIsTrue(
            Optional<UnsignedByteBuffer> inArr, Point3i point, int xShift, int yShift, KernelApplicationParameters params) {

        if (inArr.isPresent()) {
            int x1 = point.x() + xShift;

            if (!voxelsRequireHigh.extent().containsX(x1)) {
                return params.isOutsideHigh();
            }

            int y1 = point.y() + yShift;

            if (!voxelsRequireHigh.extent().containsY(y1)) {
                return params.isOutsideHigh();
            }

            int intGlobal = voxelsRequireHigh.extent().offset(x1, y1);
            return bvRequireHigh.isOn(inArr.get().getRaw(intGlobal));            
        } else {
            return true;
        }
    }
}
