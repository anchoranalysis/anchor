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

package org.anchoranalysis.image.voxel.iterator.neighbor;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Only processes points within a certain extent.
 *
 * <p>Any points outside this extent are rejected.
 *
 * @param <T> result-type that can be collected after processing
 * @author Owen Feehan
 */
@RequiredArgsConstructor
final class WithinExtent<T> implements ProcessVoxelNeighbor<T> {

    // START REQUIRED ARGUMENTS
    private final Extent extent;
    private final ProcessVoxelNeighborAbsolute<T> processAbsolutePoint;
    // END REQUIRED ARGUMENTS

    private Point3i point;

    @Override
    public void initSource(Point3i point, int sourceVal, int sourceOffsetXY) {
        this.point = point;
        this.processAbsolutePoint.initSource(sourceVal, sourceOffsetXY);
    }

    @Override
    public void processPoint(int xChange, int yChange) {

        int x1 = point.x() + xChange;
        int y1 = point.y() + yChange;

        if (x1 < 0 || x1 >= extent.x() || y1 < 0 || y1 >= extent.y()) {
            return;
        }

        processAbsolutePoint.processPoint(xChange, yChange, x1, y1);
    }

    @Override
    public boolean notifyChangeZ(int zChange) {
        int z1 = point.z() + zChange;

        if (!extent.containsZ(z1)) {
            return false;
        }

        processAbsolutePoint.notifyChangeZ(zChange, z1);
        return true;
    }

    @Override
    public T collectResult() {
        return processAbsolutePoint.collectResult();
    }
}
