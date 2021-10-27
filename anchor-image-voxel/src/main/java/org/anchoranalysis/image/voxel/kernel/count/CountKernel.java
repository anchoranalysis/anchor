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

import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.neighbor.kernel.WalkRunnable;
import org.anchoranalysis.image.voxel.kernel.Kernel;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;
import org.anchoranalysis.math.arithmetic.Counter;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Base class for kernels that return a count (positive integer) for every voxel.
 *
 * @author Owen Feehan
 */
public abstract class CountKernel extends Kernel {

    private LocalSlices slices;

    /**
     * Creates with a kernel-size of three.
     */
    protected CountKernel() {
        super(3);
    }

    @Override
    public void notifyBuffer(LocalSlices slices, int sliceIndex) {
        this.slices = slices;
    }

    /**
     * Calculates the count at a particular point.
     *
     * @param point the point.
     * @return the count.
     */
    public int calculateAt(KernelPointCursor point) {

        UnsignedByteBuffer buffer = slices.getLocal(0).get(); // NOSONAR

        if (point.isBufferOff(buffer)) {
            return 0;
        }

        Counter counter = new Counter();

        WalkRunnable walker =
                new WalkRunnable(point, this::doesNeighborVoxelQualify, counter::increment);
        walker.walk(buffer, slices::getLocal);
        return counter.getCount();
    }

    /**
     * Whether a particular neighboring voxel is accepted or not.
     *
     * @param point the neighboring point which is queried if it qualifies or not.
     * @return true iff the point qualifies.
     */
    protected abstract boolean doesNeighborVoxelQualify(Point3i point);
}
