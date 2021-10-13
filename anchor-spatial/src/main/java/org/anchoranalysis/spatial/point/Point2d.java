package org.anchoranalysis.spatial.point;

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

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * A <i>two</i>-dimensional point of <i>double</i> values.
 *
 * <p>We consider a point to be a tuple representing a single physical point in space.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Accessors(fluent = true)
public final class Point2d implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    /** X-axis component of point. */
    @Getter private double x;

    /** Y-axis component of point. */
    @Getter private double y;

    /**
     * Creates with the same values as an existing {@link Point2d}.
     *
     * @param point to copy values from.
     */
    public Point2d(Point2d point) {
        this.x = point.x;
        this.y = point.y;
    }

    /**
     * Arithmetically adds a {@link Point2i}'s values across each dimension.
     *
     * @param toAdd tuple to add to current state.
     */
    public void add(Point2i toAdd) {
        this.x = this.x + toAdd.x();
        this.y = this.y + toAdd.y();
    }

    /**
     * Arithmetically adds a {@link Point2d}'s values across each dimension.
     *
     * @param toAdd tuple to add to current state.
     */
    public void add(Point2d toAdd) {
        this.x = this.x + toAdd.x();
        this.y = this.y + toAdd.y();
    }

    /**
     * Arithmetically multiplies each dimension's value by a factor.
     *
     * @param factor the factor to multiply by.
     */
    public void scale(double factor) {
        this.x *= factor;
        this.y *= factor;
    }

    /**
     * The square of the Euclidean distance between this point and another {@link Point2d}.
     *
     * @param point the other point to a measure a distance to.
     * @return the distance squared.
     */
    public double distance(Point2d point) {
        return Math.sqrt(distanceSquared(point));
    }

    /**
     * The square of the Euclidean distance between this point and another {@link Point2d}.
     *
     * @param point the other point to a measure a distance to.
     * @return the distance squared.
     */
    public double distanceSquared(Point2d point) {
        double sx = this.x - point.x;
        double sy = this.y - point.y;
        return (sx * sx) + (sy * sy);
    }

    @Override
    public String toString() {
        return String.format("[%f,%f]", x, y);
    }
}
