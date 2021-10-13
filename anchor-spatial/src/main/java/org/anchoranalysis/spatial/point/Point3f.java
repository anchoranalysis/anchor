package org.anchoranalysis.spatial.point;

import lombok.EqualsAndHashCode;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

/**
 * A <i>three</i>-dimensional point of <i>float</i> values.
 *
 * <p>We consider a point to be a tuple representing a single physical point in space.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public final class Point3f extends Tuple3f {

    /** */
    private static final long serialVersionUID = 1L;

    /** Creates the points with a 0 in each dimension. */
    public Point3f() {
        // Initializes with [0, 0, 0]
    }

    /**
     * Creates with the same values as an existing {@link Point3f}.
     *
     * @param point to copy values from.
     */
    public Point3f(Point3f point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
    }

    /**
     * Create with values for each dimension.
     *
     * @param x the value for the X-dimension.
     * @param y the value for the Y-dimension.
     * @param z the value for the Z-dimension.
     */
    public Point3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * The Euclidean distance between this point and another.
     *
     * @param point the other point to a measure a distance to.
     * @return the distance.
     */
    public final double distance(Point3f point) {
        return Math.sqrt(distanceSquared(point));
    }

    /**
     * The square of the Euclidean distance between this point and another.
     *
     * @param point the other point to a measure a distance to.
     * @return the distance squared.
     */
    public float distanceSquared(Point3f point) {
        float sx = this.x - point.x;
        float sy = this.y - point.y;
        float sz = this.z - point.z;
        return (sx * sx) + (sy * sy) + (sz * sz);
    }
}
