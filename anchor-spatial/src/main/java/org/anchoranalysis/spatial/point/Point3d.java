package org.anchoranalysis.spatial.point;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
 * A <i>three</i>-dimensional point of <i>double</i> values.
 *
 * <p>We consider a point to be a tuple representing a single physical point in space.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class Point3d extends Tuple3d {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Creates with the same values as an existing {@link Point3d}.
     *
     * @param point to copy values from.
     */
    public Point3d(Point3f point) {
        this.x = point.x();
        this.y = point.y();
        this.z = point.z();
    }

    /**
     * Creates with the same values as an existing {@link Tuple3d}.
     *
     * @param point to copy values from.
     */
    public Point3d(Tuple3d point) {
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
    public Point3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * The Euclidean distance between this point and another {@link Point3d}.
     *
     * @param point the other point to a measure a distance to.
     * @return the distance.
     */
    public double distance(Point3d point) {
        return Math.sqrt(distanceSquared(point));
    }

    /**
     * The square of the Euclidean distance between this point and another {@link Point3d}.
     *
     * @param point the other point to a measure a distance to.
     * @return the distance squared.
     */
    public double distanceSquared(Point3d point) {
        double sx = this.x - point.x;
        double sy = this.y - point.y;
        double sz = this.z - point.z;
        return (sx * sx) + (sy * sy) + (sz * sz);
    }

    /**
     * The square of the Euclidean distance between this point and another {@link Point3i}.
     *
     * @param point the other point to a measure a distance to.
     * @return the distance squared.
     */
    public double distanceSquared(Point3i point) {
        double sx = this.x - point.x;
        double sy = this.y - point.y;
        double sz = this.z - point.z;
        return (sx * sx) + (sy * sy) + (sz * sz);
    }

    /**
     * Creates a new point with identical x and y values, but with z set to 0.
     *
     * <p>This is an <i>immutable</i> operation that does not affect current state.
     *
     * @return the new point.
     */
    public Point3d dropZ() {
        return new Point3d(x, y, 0);
    }

    /**
     * Element-wise minimum between this point and another.
     *
     * @param point the other point.
     * @return a new point containing the minimum of the x, y, z components.
     */
    public Point3d min(Tuple3d point) {
        return new Point3d(Math.min(x, point.x), Math.min(y, point.y), Math.min(z, point.z));
    }

    /**
     * Element-wise minimum between this point and another.
     *
     * @param point the other point.
     * @return a new point containing the minimum of the x, y, z components.
     */
    public Point3d min(ReadableTuple3i point) {
        return new Point3d(Math.min(x, point.x()), Math.min(y, point.y()), Math.min(z, point.z()));
    }

    /**
     * Element-wise maximum between this point and a scalar.
     *
     * @param val the scalar.
     * @return a new point containing the minimum of the x, y, z components.
     */
    public Point3d max(double val) {
        return new Point3d(Math.max(x, val), Math.max(y, val), Math.max(z, val));
    }

    /**
     * Checks if this point is equal to another {@link Point3d} accepting a tolerance between double
     * values.
     *
     * @param other the other point to check for equality with.
     * @param delta the maximum difference before two double values are considered to be unequal.
     * @return true iff all dimensions of the point are equal (within the tolerance).
     */
    public boolean equalsTolerance(Point3d other, double delta) {
        return equalsTolerance(x, other.x, delta)
                && equalsTolerance(y, other.y, delta)
                && equalsTolerance(z, other.z, delta);
    }

    /**
     * Converts the point to an array.
     *
     * @return a newly created array with three elements, respectively for x, y and z components.
     */
    public double[] toArray() {
        double[] out = new double[3];
        out[0] = x;
        out[1] = y;
        out[2] = z;
        return out;
    }

    /**
     * Converts the X and Y dimensions of the point to an array.
     *
     * @return a newly created array with two elements, respectively for x and y components.
     */
    public double[] toArrayXY() {
        double[] out = new double[2];
        out[0] = x;
        out[1] = y;
        return out;
    }

    /**
     * Element-wise maximum between this point and another.
     *
     * @param point the other point.
     * @return a new point containing the minimum of the x, y, z components.
     */
    public Point3d max(Tuple3d point) {
        return new Point3d(Math.max(x, point.x), Math.max(y, point.y), Math.max(z, point.z));
    }

    /**
     * Adds two tuples immutably.
     *
     * @param tuple1 the first tuple to add.
     * @param tuple2 the second tuple to add.
     * @return a newly created point, where each dimension is the sum of the corresponding
     *     dimensions in the points.
     */
    public static Point3d immutableAdd(Tuple3d tuple1, Tuple3d tuple2) {
        Point3d pointDup = new Point3d(tuple1);
        pointDup.add(tuple2);
        return pointDup;
    }

    /**
     * Adds values to a point immutably.
     *
     * @param point the point to add.
     * @param x the value to add to the X-component.
     * @param y the value to add to the Y-component.
     * @param z the value to add to the Z-component.
     * @return a newly created point, where each dimension is the sum of the corresponding
     *     dimensions in the points.
     */
    public static Point3d immutableAdd(Point3d point, int x, int y, int z) {
        Point3d pointCopy = new Point3d(point);
        pointCopy.incrementX(x);
        pointCopy.incrementY(y);
        pointCopy.incrementZ(z);
        return pointCopy;
    }

    /**
     * Subtracts a {@link Tuple3d} from a {@link Tuple3d} immutably.
     *
     * @param tuple the tuple to subtract from.
     * @param toSubtract the tuple to subtract.
     * @return a newly created point, where each dimension is the subtraction of the corresponding
     *     dimensions in the points.
     */
    public static Point3d immutableSubtract(Tuple3d tuple, Tuple3d toSubtract) {
        Point3d pointDup = new Point3d(tuple);
        pointDup.subtract(toSubtract);
        return pointDup;
    }

    /**
     * Subtracts a {@link Tuple3d} from a {@link Tuple3i} immutably.
     *
     * @param tuple the tuple to subtract from.
     * @param toSubtract the tuple to subtract.
     * @return a newly created point, where each dimension is the subtraction of the corresponding
     *     dimensions in the points.
     */
    public static Point3d immutableSubtract(Tuple3i tuple, Tuple3d toSubtract) {
        Point3d point = PointConverter.doubleFromInt(tuple);
        point.subtract(toSubtract);
        return point;
    }

    /**
     * Scales a tuple immutably.
     *
     * @param tuple the tuple to scale.
     * @param factor what to multiply each dimension by.
     * @return a newly created point, where each component is scaled by {@code factor}.
     */
    public static Point3d immutableScale(Tuple3d tuple, int factor) {
        Point3d pointDup = new Point3d(tuple);
        pointDup.scale(factor);
        return pointDup;
    }

    /** Tests if {@code value1} and {@code value2} are equal within a certain tolerance. */
    private static boolean equalsTolerance(double value1, double value2, double delta) {
        return Math.abs(value1 - value2) < delta;
    }
}
