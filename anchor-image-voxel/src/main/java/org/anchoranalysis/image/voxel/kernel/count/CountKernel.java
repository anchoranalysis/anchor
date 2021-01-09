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

package org.anchoranalysis.image.voxel.kernel.count;

import java.util.Optional;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.Kernel;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point3i;

public abstract class CountKernel extends Kernel {

    private LocalSlices inSlices;

    private Extent extent;

    // Constructor
    protected CountKernel() {
        super(3);
    }

    @Override
    public void init(Voxels<UnsignedByteBuffer> in, KernelApplicationParameters params) {
        this.extent = in.extent();
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        this.inSlices = inSlices;
    }

    protected abstract boolean isNeighborVoxelAccepted(
            Point3i point);

    public int countAtPosition(int index, Point3i point, BinaryValuesByte binaryValues, KernelApplicationParameters params) {

        
        UnsignedByteBuffer buffer = inSlices.getLocal(0).get(); // NOSONAR
        Optional<UnsignedByteBuffer> bufferZLess1 = inSlices.getLocal(-1);
        Optional<UnsignedByteBuffer> bufferZPlus1 = inSlices.getLocal(+1);
        
        KernelPointCursor cursor = new KernelPointCursor(index, point, extent, binaryValues, params);

        if (binaryValues.isOff(buffer.getRaw(cursor.getIndex()))) {
            return 0;
        }

        int count = 0;

        // We walk up and down in x
        cursor.decrementX();
        
        if (cursor.nonNegativeX()) {
            if (binaryValues.isOff(buffer.getRaw(cursor.getIndex()))
                    && isNeighborVoxelAccepted(cursor.getPoint())) {
                count++;
            }
        } else {
            if (cursor.isOutsideOffUnignored() && isNeighborVoxelAccepted(cursor.getPoint())) {
                count++;
            }
        }

        cursor.incrementXTwice();
        if (cursor.lessThanMaxX()) {
            if (binaryValues.isOff(buffer.getRaw(cursor.getIndex()))
                    && isNeighborVoxelAccepted(cursor.getPoint())) {
                count++;
            }
        } else {
            if (cursor.isOutsideOffUnignored() && isNeighborVoxelAccepted(cursor.getPoint())) {
                count++;
            }
        }
        
        cursor.decrementX();

        cursor.decrementY();

        if (cursor.nonNegativeY()) {
            if (binaryValues.isOff(buffer.getRaw(cursor.getIndex()))
                    && isNeighborVoxelAccepted(cursor.getPoint())) {
                count++;
            }
        } else {
            if (cursor.isOutsideOffUnignored() && isNeighborVoxelAccepted(cursor.getPoint()) ) {
                count++;
            }
        }

        cursor.incrementYTwice();

        if (cursor.lessThanMaxY()) {
            if (binaryValues.isOff(buffer.getRaw(cursor.getIndex()))
                    && isNeighborVoxelAccepted(cursor.getPoint())) {
                count++;
            }
        } else {
            if (cursor.isOutsideOffUnignored() && isNeighborVoxelAccepted(cursor.getPoint())) {
                count++;
            }
        }
        
        cursor.decrementY();

        if (params.isUseZ()) {
            
            cursor.decrementZ();
            
            if (bufferZLess1.isPresent()) {
                if (binaryValues.isOff(bufferZLess1.get().getRaw(cursor.getIndex()))
                        && isNeighborVoxelAccepted(cursor.getPoint())) {
                    count++;
                }
            } else {
                if (cursor.isOutsideOffUnignored() && isNeighborVoxelAccepted(cursor.getPoint())) {
                    count++;
                }
            }

            cursor.incrementZTwice();
            
            if (bufferZPlus1.isPresent()) {
                if (binaryValues.isOff(bufferZPlus1.get().getRaw(cursor.getIndex()))
                        && isNeighborVoxelAccepted(cursor.getPoint())) {
                    count++;
                }
            } else {
                if (cursor.isOutsideOffUnignored() && isNeighborVoxelAccepted(cursor.getPoint())) {
                    count++;
                }
            }
            
            cursor.decrementZ();
        }
        return count;
    }
}
