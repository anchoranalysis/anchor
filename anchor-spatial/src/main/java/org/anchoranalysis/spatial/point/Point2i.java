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
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A <i>two</i>-dimensional point of <i>int</i> values.
 *
 * <p>We consider a point to be a tuple representing a single physical point in space.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public final class Point2i implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    /** X-axis component of point. */
    @Setter private int x;

    /** Y-axis component of point. */
    @Setter private int y;

    /**
     * Creates with the same values as an existing {@link Point2i}.
     *
     * @param point to copy values from.
     */
    public Point2i(Point2i point) {
        x = point.x();
        y = point.y();
    }

    /**
     * X-axis component of point.
     *
     * @return the component value.
     */
    public int x() {
        return x;
    }

    /**
     * Y-axis component of point.
     *
     * @return the component value.
     */
    public int y() {
        return y;
    }

    /** Increments the X component's value by one. */
    public void incrementX() {
        this.x++;
    }

    /** Increments the Y component's value by one. */
    public void incrementY() {
        this.y++;
    }

    /**
     * Increments the X component's value by a shift.
     *
     * @param shift how much to increment by.
     */
    public void incrementX(int shift) {
        this.x += shift;
    }

    /**
     * Increments the Y component's value by a shift.
     *
     * @param shift how much to increment by.
     */
    public void incrementY(int shift) {
        this.y += shift;
    }

    /**
     * Adds values from a {@link Point2i} across each corresponding dimension.
     *
     * @param point the point whose values are added.
     */
    public void add(Point2i point) {
        this.x = this.x + point.x();
        this.y = this.y + point.y();
    }

    /**
     * Converts the point to an array.
     *
     * @return a newly created array with two elements, respectively for x and y components.
     */
    public double[] toArray() {
        double[] out = new double[2];
        out[0] = x;
        out[1] = y;
        return out;
    }

    /**
     * Adds two points immutably.
     *
     * @param point1 the first point to add.
     * @param point2 the second point to add.
     * @return a newly created point, where each dimension is the sum of the corresponding
     *     dimensions in the points.
     */
    public static Point2i immutableAdd(Point2i point1, Point2i point2) {
        Point2i pointCopy = new Point2i(point1);
        pointCopy.add(point2);
        return pointCopy;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", x, y);
    }
}
