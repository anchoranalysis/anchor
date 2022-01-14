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

package org.anchoranalysis.spatial.box;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Tracks the range experienced by points in the X, Y and Z dimensions.
 *
 * <p>The minimum and maximum values of any point across the dimensions is always remembered.
 *
 * <p>Points can be dynamically added and the min/max is updated, as needed.
 *
 * @author Owen Feehan
 */
public final class PointRange {

    /**
     * The <b>minimum</b> encountered value in each dimension, with each dimension of each point
     * considered separately.
     */
    private Point3i min;

    /**
     * The <b>maximum</b> encountered value in each dimension, with each dimension of each point
     * considered separately.
     */
    private Point3i max;

    /**
     * Adds a new point to be considered in the range.
     *
     * @param point the point to add.
     */
    public void add(ReadableTuple3i point) {
        add(point.x(), point.y(), point.z());
    }

    /**
     * Adds a new point to be considered in the range.
     *
     * @param x the value along the X-axis for the point to add.
     * @param y the value along the Y-axis for the point to add.
     * @param z the value along the Z-axis for the point to add.
     */
    public void add(int x, int y, int z) {

        if (max == null || min == null) {
            max = new Point3i(x, y, z);
            min = new Point3i(x, y, z);
            return;
        }

        if (x < min.x()) {
            min.setX(x);
        } else if (x > max.x()) {
            max.setX(x);
        }

        if (y < min.y()) {
            min.setY(y);
        } else if (y > max.y()) {
            max.setY(y);
        }

        if (z < min.z()) {
            min.setZ(z);
        } else if (z > max.z()) {
            max.setZ(z);
        }
    }

    /**
     * Creates a {@link BoundingBox} to just cover the range of points that have been added.
     *
     * @return a newly created box that spans from the minimum to maximum values encountered in each
     *     dimension - across all added points.
     * @throws OperationFailedException if no point has been added yet.
     */
    public BoundingBox toBoundingBox() throws OperationFailedException {

        if (min == null || max == null) {
            throw new OperationFailedException(
                    "No point has been added, so no bounding-box can be derived");
        }

        return toBoundingBoxNoCheck();
    }

    /**
     * Like {@link #toBoundingBox()} but if no points exist, an exception is not thrown.
     *
     * <p>Consider calling {@link #isEmpty} first to check.
     *
     * @return a newly created box that spans from the minimum to maximum values encountered in each
     *     dimension - across all added points.
     */
    public BoundingBox toBoundingBoxNoCheck() {
        return BoundingBox.createDuplicate(min, max);
    }

    /**
     * Has a valid min and max defined?
     *
     * <p>In other words, has at least one point been added?
     *
     * @return true iff a valid minimum and maximum exists.
     */
    public boolean isEmpty() {
        return (min == null) || (max == null);
    }
}
