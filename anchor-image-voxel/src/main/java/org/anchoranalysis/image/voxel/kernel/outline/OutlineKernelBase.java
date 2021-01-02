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
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;
import org.anchoranalysis.image.voxel.kernel.morphological.BinaryKernelMorphologicalExtent;
import org.anchoranalysis.spatial.point.Point3i;

public abstract class OutlineKernelBase extends BinaryKernelMorphologicalExtent {

    @Override
    public boolean acceptPoint(int index2, Point3i point2, BinaryValuesByte binaryValues, KernelApplicationParameters params) {

        UnsignedByteBuffer buffer = getVoxels().getLocal(0).get(); // NOSONAR
                
        if (binaryValues.isOff(buffer.getRaw(index2))) {
            return false;
        }
        
        KernelPointCursor point = new KernelPointCursor(index2, point2, extent, binaryValues, params);
                
        // We walk up and down in x
        point.decrementX();
        
        if (doesNeighborQualify(point.nonNegativeX(), point, () -> buffer, 0)) {
            point.incrementX();
            return true;
        }

        point.incrementXTwice();
        
        try {
            if (doesNeighborQualify(point.nonNegativeX(), point, () -> buffer, 0)) {
                return true;
            }
        } finally {
            point.decrementX();
        }

        // We walk up and down in y
        point.decrementY();
        
        if (doesNeighborQualify(point.nonNegativeY(), point, () -> buffer, 0)) {
            point.incrementY();
            return true;
        }
        
        point.incrementYTwice();
        
        try {
            if (doesNeighborQualify(point.lessThanMaxY(), point, () -> buffer, 0)) {
                return true;
            }
        } finally {
            point.decrementY();    
        }

        return slicesQualify(point);
    }
    
    /** Checks if any neighbor voxels on an adjacent z-slice qualify to make the current voxel an outline voxel. */
    private boolean slicesQualify(KernelPointCursor point) {
        if (point.isUseZ()) {
            return doesNeighborQualifyFromSlice(point, -1) ||
                    doesNeighborQualifyFromSlice(point, +1);
        } else {
            return false;
        }
    }
    
    private boolean doesNeighborQualifyFromSlice(KernelPointCursor point, int zShift) {
        Optional<UnsignedByteBuffer> buffer = getVoxels().getLocal(zShift);
        return doesNeighborQualify(buffer.isPresent(), point, buffer::get, zShift);
    }
    
    protected abstract boolean doesNeighborQualify(boolean guard, KernelPointCursor point, Supplier<UnsignedByteBuffer> buffer, int zShift);
}
