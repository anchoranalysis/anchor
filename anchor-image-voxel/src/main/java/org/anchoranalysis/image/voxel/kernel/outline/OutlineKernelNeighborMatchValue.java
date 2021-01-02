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
    
    /** Checks whether a particular neighbor voxel qualifies to make the current voxel an outline voxel. */
    @Override
    protected boolean doesNeighborQualify(boolean guard, int index, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params, Supplier<UnsignedByteBuffer> buffer, int zShift) {
        if (guard) {
            Optional<UnsignedByteBuffer> requireSlice = localSlicesRequireHigh.getLocal(zShift);
            return binaryValues.isOff(buffer.get().getRaw(index)) && checkIfRequireHighIsTrue(requireSlice.get(), point);
        } else {
            return params.isOutsideLowUnignored();
        }
    }

    private boolean checkIfRequireHighIsTrue(
            UnsignedByteBuffer additionalBuffer, Point3i point) {
        int indexGlobal = voxelsRequireHigh.extent().offsetSlice(point);
        return bvRequireHigh.isOn(additionalBuffer.getRaw(indexGlobal));            
    }
}
