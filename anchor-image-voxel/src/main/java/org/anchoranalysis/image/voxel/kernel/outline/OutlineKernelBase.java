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
import org.anchoranalysis.image.voxel.kernel.morphological.BinaryKernelMorphologicalExtent;
import org.anchoranalysis.spatial.point.Point3i;

public abstract class OutlineKernelBase extends BinaryKernelMorphologicalExtent {

    @Override
    public boolean acceptPoint(int index, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params) {

        UnsignedByteBuffer buffer = getVoxels().getLocal(0).get(); // NOSONAR

        int xLength = extent.x();
                
        if (binaryValues.isOff(buffer.getRaw(index))) {
            return false;
        }
                
        // We walk up and down in x
        point.decrementX();
        index--;
        
        if (doesNeighborQualify(point.x() >= 0, index, point, binaryValues, params, () -> buffer, 0)) {
            point.incrementX();
            return true;
        }

        point.incrementX(2);
        index += 2;
        
        try {
            if (doesNeighborQualify(point.x() < extent.x(), index, point, binaryValues, params, () -> buffer, 0)) {
                return true;
            }
        } finally {
            point.decrementX();    
        }
        
        index--;
        

        // We walk up and down in y
        point.decrementY();
        index -= xLength;
        
        if (doesNeighborQualify(point.y() >= 0, index, point, binaryValues, params, () -> buffer, 0)) {
            point.incrementY();
            return true;
        }

        point.incrementY(2);
        index += (2 * xLength);
        
        try {
            if (doesNeighborQualify( point.y() < extent.y(), index, point, binaryValues, params, () -> buffer, 0)) {
                return true;
            }
        } finally {
            point.decrementY();    
        }
        index -= xLength;
        

        return slicesQualify(index, point, binaryValues, params);
    }
    
    /** Checks if any neighbor voxels on an adjacent z-slice qualify to make the current voxel an outline voxel. */
    private boolean slicesQualify(int index, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params) {
        if (params.isUseZ()) {
            return doesNeighborQualifyFromSlice(index, point, binaryValues, params, -1) ||
                    doesNeighborQualifyFromSlice(index, point, binaryValues, params, +1);
        } else {
            return false;
        }
    }
    
    private boolean doesNeighborQualifyFromSlice(int index, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params, int zShift) {
        Optional<UnsignedByteBuffer> buffer = getVoxels().getLocal(zShift);
        return doesNeighborQualify(buffer.isPresent(), index, point, binaryValues, params, buffer::get, zShift);
    }
    
    protected abstract boolean doesNeighborQualify(boolean guard, int index, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params, Supplier<UnsignedByteBuffer> buffer, int zShift);
}
