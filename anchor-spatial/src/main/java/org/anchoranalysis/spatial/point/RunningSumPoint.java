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
 * A running sum for tracking points in each dimension
 *
 * @author Owen Feehan
 */
public final class RunningSumPoint {

    private final RunningSumCollection delegate;

    public RunningSumPoint() {
        delegate = new RunningSumCollection(3);
    }

    public void increment(Point2i point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
    }

    public void increment(Point2d point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
    }

    public void increment(Point3i point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
        forDim(2).increment(point.z());
    }

    public void increment(Point3d point) {
        forDim(0).increment(point.x());
        forDim(1).increment(point.y());
        forDim(2).increment(point.z());
    }

    public Point3d mean() {
        return new Point3d(forDim(0).mean(), forDim(1).mean(), forDim(2).mean());
    }

    public Point2d meanXY() {
        return new Point2d(forDim(0).mean(), forDim(1).mean());
    }

    /** The count for XY dimensions (guaranteed to always be the same */
    public long getCountXY() {
        return forDim(0).getCount();
    }

    /**
     * The count for Z dimension (this is identical to {@link #getCountXY} if only 3D points have
     * ben added)
     */
    public long getCountZ() {
        return forDim(2).getCount();
    }

    private RunningSum forDim(int dimIndex) {
        return delegate.get(dimIndex);
    }
}
