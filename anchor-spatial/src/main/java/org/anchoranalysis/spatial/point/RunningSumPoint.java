/*-
 * #%L
 * anchor-spatial
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
package org.anchoranalysis.spatial.point;

import org.anchoranalysis.math.arithmetic.RunningSum;
import org.anchoranalysis.math.arithmetic.RunningSumCollection;

/**
 * A running sum for tracking points separately in each dimension.
 *
 * @author Owen Feehan
 */
public final class RunningSumPoint {

    private final RunningSumCollection delegate = new RunningSumCollection(3);

    /**
     * Increments the running-sum by a {@link Point2i}.
     * 
     * <p>No impact occurs on the third-dimension of the running-sum.
     * 
     * @param point the point to add to the running sum.
     */
    public void increment(Point2i point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
    }

    /**
     * Increments the running-sum by a {@link Point2d}.
     * 
     * <p>No impact occurs on the third-dimension of the running-sum.
     * 
     * @param point the point to add to the running sum.
     */
    public void increment(Point2d point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
    }

    /**
     * Increments the running-sum by a {@link Point3i}.
     * 
     * @param point the point to add to the running sum.
     */
    public void increment(Point3i point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
        forDim(2).increment(point.z());
    }

    /**
     * Increments the running-sum by a {@link Point3d}.
     * 
     * @param point the point to add to the running sum.
     */
    public void increment(Point3d point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
        forDim(2).increment(point.z());
    }

    /**
     * The mean value of all points added to the running-sum, separately for all three-dimensions.
     * 
     * @return a newly created point, with the mean for each dimension.
     */
    public Point3d mean() {
        return new Point3d(forDim(0).mean(), forDim(1).mean(), forDim(2).mean());
    }

    /**
     * The mean value of all points added to the running-sum, only for the X- and Y-dimensions.
     * 
     * @return a newly created point, with the mean for X- and Y- dimensions.
     */
    public Point2d meanXY() {
        return new Point2d(forDim(0).mean(), forDim(1).mean());
    }

    /** 
     * The count for the X or Y dimensions.
     * 
     * <p>The count is guaranteed to always be the same for either the X or Y dimension.
     * 
     *  @return the count.
     */
    public long countXY() {
        return forDim(0).getCount();
    }

    /**
     * The count for the Z dimension.
     * 
     * <p>This is identical to {@link #countXY} if only 3D points have been added.
     * 
     * @return the count.
     */
    public long countZ() {
        return forDim(2).getCount();
    }

    /** A {@link RunningSum} corresponding to a particular dimension. */
    private RunningSum forDim(int dimensionIndex) {
        return delegate.get(dimensionIndex);
    }
}
