/* (C)2020 */
package org.anchoranalysis.core.arithmetic;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;

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
        forDim(0).increment(point.getX());
        forDim(1).increment(point.getY());
    }

    public void increment(Point2d point) {
        forDim(0).increment(point.getX());
        forDim(1).increment(point.getY());
    }

    public void increment(Point3i point) {
        forDim(0).increment(point.getX());
        forDim(1).increment(point.getY());
        forDim(2).increment(point.getZ());
    }

    public void increment(Point3d point) {
        forDim(0).increment(point.getX());
        forDim(1).increment(point.getY());
        forDim(2).increment(point.getZ());
    }

    public Point3d mean() {
        return new Point3d(forDim(0).mean(), forDim(1).mean(), forDim(2).mean());
    }

    public Point2d meanXY() {
        return new Point2d(forDim(0).mean(), forDim(1).mean());
    }

    /** The count for XY dimensions (guaranteed to always be the same */
    public int getCountXY() {
        return forDim(0).getCount();
    }

    /**
     * The count for Z dimension (this is identical to {@link getCountXY} if only 3D points have ben
     * added)
     */
    public int getCountZ() {
        return forDim(2).getCount();
    }

    private RunningSum forDim(int dimIndex) {
        return delegate.get(dimIndex);
    }
}
