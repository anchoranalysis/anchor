package org.anchoranalysis.core.geometry;

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

@EqualsAndHashCode(callSuper = true)
public final class Point3i extends Tuple3i {

    /** */
    private static final long serialVersionUID = 1L;

    /** Constructor - creates the points with a 0 in each dimension */
    public Point3i() {
        // Initializes with zeroes
    }

    public Point3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3i(ReadableTuple3i point) {
        this.x = point.x();
        this.y = point.y();
        this.z = point.z();
    }

    public int distanceSquared(Point3i point) {
        int sx = this.x - point.x;
        int sy = this.y - point.y;
        int sz = this.z - point.z;
        return (sx * sx) + (sy * sy) + (sz * sz);
    }

    public double distance(Point3i point) {
        return Math.sqrt(distanceSquared(point));
    }

    /** The maximum distance across any axis */
    public int distanceMax(Point3i point) {
        int sx = Math.abs(this.x - point.x);
        int sy = Math.abs(this.y - point.y);
        int sz = Math.abs(this.z - point.z);
        return Math.max(sx, Math.max(sy, sz));
    }

    @Override
    public Point3i duplicateChangeZ(int zNew) {
        return new Point3i(x, y, zNew);
    }

    /** Performs an addition without changing any values in an existing point */
    public static Point3i immutableAdd(ReadableTuple3i point, ReadableTuple3i toAdd) {
        Point3i pointCopy = new Point3i(point);
        pointCopy.add(toAdd);
        return pointCopy;
    }

    /** Performs an addition without changing any values in an existing point */
    public static Point3i immutableAdd(ReadableTuple3i point, int x, int y, int z) {
        Point3i pointCopy = new Point3i(point);
        pointCopy.incrementX(x);
        pointCopy.incrementY(y);
        pointCopy.incrementZ(z);
        return pointCopy;
    }

    /** Performs a subtraction without changing any values in an existing point */
    public static Point3i immutableSubtract(ReadableTuple3i point, ReadableTuple3i toSubtract) {
        Point3i pointCopy = new Point3i(point);
        pointCopy.subtract(toSubtract);
        return pointCopy;
    }

    /** Performs a scale without changing any values in an existing point */
    public static Point3i immutableScale(ReadableTuple3i point, int factor) {
        Point3i pointCopy = new Point3i(point);
        pointCopy.scale(factor);
        return pointCopy;
    }

    /** Applies an operation to each element across two points */
    public static Point3i elementwiseOperation(
            ReadableTuple3i point1, ReadableTuple3i point2, IntBinaryOperator operator) {
        return new Point3i(
                operator.applyAsInt(point1.x(), point2.x()),
                operator.applyAsInt(point1.y(), point2.y()),
                operator.applyAsInt(point1.z(), point2.z()));
    }
}
