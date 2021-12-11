package org.anchoranalysis.spatial.point;

import java.util.function.IntBinaryOperator;
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
 * A <i>three</i>-dimensional point of <i>int</i> values.
 *
 * <p>We consider a point to be a tuple representing a single physical point in space.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public final class Point3i extends Tuple3i {

    /** */
    private static final long serialVersionUID = 1L;

    /** Creates the points with a 0 in each dimension. */
    public Point3i() {
        // Initializes with zeroes
    }

    /**
     * Create with an identical value in each dimension.
     *
     * @param valueForAllDimensions the value for each dimension.
     */
    public Point3i(int valueForAllDimensions) {
        this(valueForAllDimensions, valueForAllDimensions, valueForAllDimensions);
    }

    /**
     * Create with values for each dimension.
     *
     * @param x the value for the X-dimension.
     * @param y the value for the Y-dimension.
     * @param z the value for the Z-dimension.
     */
    public Point3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates with the same values as an existing {@link ReadableTuple3i}.
     *
     * @param point to copy values from.
     */
    public Point3i(ReadableTuple3i point) {
        this.x = point.x();
        this.y = point.y();
        this.z = point.z();
    }

    /**
     * The Euclidean distance between this point and another.
     *
     * @param point the other point to a measure a distance to.
     * @return the distance.
     */
    public double distance(Point3i point) {
        return Math.sqrt(distanceSquared(point));
    }

    /**
     * The square of the Euclidean distance between this point and another {@link Point3i}.
     *
     * @param point the other point to a measure a distance to.
     * @return the distance squared.
     */
    public int distanceSquared(Point3i point) {
        int sx = this.x - point.x;
        int sy = this.y - point.y;
        int sz = this.z - point.z;
        return (sx * sx) + (sy * sy) + (sz * sz);
    }

    /**
     * The square of the Euclidean distance between this point and another {@link Point3d}.
     *
     * @param point the other point to a measure a distance to.
     * @return the distance squared.
     */
    public double distanceSquared(Point3d point) {
        double sx = -point.x + this.x;
        double sy = -point.y + this.y;
        double sz = -point.z + this.z;
        return (sx * sx) + (sy * sy) + (sz * sz);
    }

    /**
     * The maximum distance across any individual axis to another point.
     *
     * @param point the point to consider distances to, across each axis.
     * @return the maximal distance along any one particular axis.
     */
    public int distanceMax(Point3i point) {
        int sx = Math.abs(this.x - point.x);
        int sy = Math.abs(this.y - point.y);
        int sz = Math.abs(this.z - point.z);
        return Math.max(sx, Math.max(sy, sz));
    }

    @Override
    public Point3i duplicateChangeZ(int zValueToAssign) {
        return new Point3i(x, y, zValueToAssign);
    }

    /**
     * Adds two tuples immutably.
     *
     * @param tuple1 the first tuple to add.
     * @param tuple2 the second tuple to add.
     * @return a newly created point, where each dimension is the sum of the corresponding
     *     dimensions in the points.
     */
    public static Point3i immutableAdd(ReadableTuple3i tuple1, ReadableTuple3i tuple2) {
        Point3i pointCopy = new Point3i(tuple1);
        pointCopy.add(tuple2);
        return pointCopy;
    }

    /**
     * Adds values to a tuple immutably.
     *
     * @param tuple the tuple to add.
     * @param x the value to add to the X-component.
     * @param y the value to add to the Y-component.
     * @param z the value to add to the Z-component.
     * @return a newly created point, where each dimension is the sum of the corresponding
     *     dimensions in the points.
     */
    public static Point3i immutableAdd(ReadableTuple3i tuple, int x, int y, int z) {
        Point3i pointCopy = new Point3i(tuple);
        pointCopy.incrementX(x);
        pointCopy.incrementY(y);
        pointCopy.incrementZ(z);
        return pointCopy;
    }

    /**
     * Subtracts two tuples immutably.
     *
     * @param tuple the tuple to subtract from.
     * @param toSubtract the tuple to subtract.
     * @return a newly created point, where each dimension is the subtraction of the corresponding
     *     dimensions in the points.
     */
    public static Point3i immutableSubtract(ReadableTuple3i tuple, ReadableTuple3i toSubtract) {
        Point3i pointCopy = new Point3i(tuple);
        pointCopy.subtract(toSubtract);
        return pointCopy;
    }

    /**
     * Multiplies each component by {@code factor} without changing any values in an existing point.
     *
     * <p>This is an <i>immutable</i> operation that produces a new point.
     *
     * @param point the point whose component's will be scaled.
     * @param factor the factor to scale by.
     * @return a newly created point with scaled values.
     */
    public static Point3i immutableScale(ReadableTuple3i point, int factor) {
        Point3i pointCopy = new Point3i(point);
        pointCopy.scale(factor);
        return pointCopy;
    }

    /**
     * Creates a new point by applying a pairwise operation to each dimension's values for two
     * tuples.
     *
     * @param tuple1 the first tuple.
     * @param tuple2 the second tuple.
     * @param operator the operator to apply to each dimension of the tuples to produce the
     *     corresponding dimension in the returned tuple.
     * @return a newly-created point arising from applying {@code operator} to the components of
     *     each tuple.
     */
    public static Point3i elementwiseOperation(
            ReadableTuple3i tuple1, ReadableTuple3i tuple2, IntBinaryOperator operator) {
        return new Point3i(
                operator.applyAsInt(tuple1.x(), tuple2.x()),
                operator.applyAsInt(tuple1.y(), tuple2.y()),
                operator.applyAsInt(tuple1.z(), tuple2.z()));
    }
}
