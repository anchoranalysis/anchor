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
import java.util.function.Supplier;
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

        Optional<UnsignedByteBuffer> inArrR = localSlicesRequireHigh.getLocal(0); // NOSONAR

        int xLength = extent.x();
                
        if (binaryValues.isOff(inArrZ.getRaw(ind))) {
            return false;
        }

        int x = point.x();
        int y = point.y();
                
        // We walk up and down in x
        x--;
        ind--;
        
        if (doesNeighborQualify(x >= 0, ind, point, binaryValues, params, () -> inArrZ, inArrR::get, -1, 0)) {
            return true;
        }

        x += 2;
        ind += 2;
        
        if (doesNeighborQualify(x < extent.x(), ind, point, binaryValues, params, () -> inArrZ, inArrR::get, +1, 0)) {
            return true;
        }
        
        ind--;

        // We walk up and down in y
        y--;
        ind -= xLength;
        
        if (doesNeighborQualify(y >= 0, ind, point, binaryValues, params, () -> inArrZ, inArrR::get, 0, -1)) {
            return true;
        }

        y += 2;
        ind += (2 * xLength);
        
        if (doesNeighborQualify( y < extent.y(), ind, point, binaryValues, params, () -> inArrZ, inArrR::get, 0, +1)) {
            return true;
        }
        ind -= xLength;

        return slicesQualify(ind, point, binaryValues, params);
    }

    /** Checks if any neighbor voxels on an adjacent z-slice qualify to make the current voxel an outline voxel. */
    private boolean slicesQualify(int ind, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params) {
        Optional<UnsignedByteBuffer> minusOne = getVoxels().getLocal(-1);
        Optional<UnsignedByteBuffer> plusOne = getVoxels().getLocal(+1);
        if (params.isUseZ()) {
            return doesNeighborQualify(minusOne.isPresent(), ind, point, binaryValues, params, minusOne::get, localSlicesRequireHigh.getLocal(-1)::get, 0, 0) ||
                    doesNeighborQualify(plusOne.isPresent(), ind, point, binaryValues, params, plusOne::get, localSlicesRequireHigh.getLocal(+1)::get, 0, 0);
        } else {
            return false;
        }
    }
    
    /** Checks whether a particular neighbor voxel qualifies to make the current voxel an outline voxel. */
    private boolean doesNeighborQualify(boolean guard, int ind, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params, Supplier<UnsignedByteBuffer> inArrZ, Supplier<UnsignedByteBuffer> requireSlice, int xShift, int yShift) {
        if (guard) {
            return binaryValues.isOff(inArrZ.get().getRaw(ind)) && checkIfRequireHighIsTrue(requireSlice.get(), point, xShift, yShift);
        } else {
            return params.isOutsideLowUnignored();
        }
    }

    private boolean checkIfRequireHighIsTrue(
            UnsignedByteBuffer inArr, Point3i point, int xShift, int yShift) {
        int x1 = point.x() + xShift;
        int y1 = point.y() + yShift;
        int intGlobal = voxelsRequireHigh.extent().offset(x1, y1);
        return bvRequireHigh.isOn(inArr.getRaw(intGlobal));            
    }
}
