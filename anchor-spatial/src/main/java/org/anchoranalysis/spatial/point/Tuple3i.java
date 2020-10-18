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

import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.axis.AxisTypeConverter;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

@EqualsAndHashCode
public abstract class Tuple3i implements ReadableTuple3i {

    /** */
    private static final long serialVersionUID = 1L;

    @Setter protected int x = 0;

    @Setter protected int y = 0;

    @Setter protected int z = 0;

    public final void add(ReadableTuple3i point) {
        this.x = this.x + point.x();
        this.y = this.y + point.y();
        this.z = this.z + point.z();
    }

    public final void subtract(int val) {
        this.x = this.x - val;
        this.y = this.y - val;
        this.z = this.z - val;
    }

    public final void subtract(ReadableTuple3i point) {
        this.x = this.x - point.x();
        this.y = this.y - point.y();
        this.z = this.z - point.z();
    }

    public final void scale(int factor) {
        this.x = this.x * factor;
        this.y = this.y * factor;
        this.z = this.z * factor;
    }

    public final void scale(double factor) {
        this.x = (int) (factor * this.x);
        this.y = (int) (factor * this.y);
        this.z = (int) (factor * this.z);
    }

    public final void divideBy(int factor) {
        this.x = this.x / factor;
        this.y = this.y / factor;
        this.z = this.z / factor;
    }

    public final void scaleX(double factor) {
        this.x = (int) (factor * this.x);
    }

    public final void scaleY(double factor) {
        this.y = (int) (factor * this.y);
    }

    public final void scaleXY(double factor) {
        scaleX(factor);
        scaleY(factor);
    }

    /**
     * Element-wise minimum between this point and another
     *
     * @param point the other point
     * @return a new point containing the minimum of the x, y, z components
     */
    public Point3i min(ReadableTuple3i point) {
        return new Point3i(Math.min(x, point.x()), Math.min(y, point.y()), Math.min(z, point.z()));
    }

    /**
     * Element-wise maximum between this point and another
     *
     * @param point the other point
     * @return a new point containing the minimum of the x, y, z components
     */
    public Point3i max(ReadableTuple3i point) {
        return new Point3i(Math.max(x, point.x()), Math.max(y, point.y()), Math.max(z, point.z()));
    }

    /**
     * Element-wise maximum between this point and a scalar
     *
     * @param val the scalar
     * @return a new point containing the minimum of the x, y, z components
     */
    public Point3i max(int val) {
        return new Point3i(Math.max(x, val), Math.max(y, val), Math.max(z, val));
    }

    public final int byDimension(int dimIndex) {
        if (dimIndex == 0) {
            return x;
        } else if (dimIndex == 1) {
            return y;
        } else if (dimIndex == 2) {
            return z;
        } else {
            throw new AnchorFriendlyRuntimeException(AxisTypeConverter.INVALID_AXIS_STRING);
        }
    }

    @Override
    public final int byDimension(AxisType axisType) {
        switch (axisType) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
            default:
                throw new AnchorFriendlyRuntimeException(AxisTypeConverter.INVALID_AXIS_INDEX);
        }
    }

    public final void incrementX() {
        this.x++;
    }

    public final void incrementY() {
        this.y++;
    }

    public final void incrementZ() {
        this.z++;
    }

    public final void incrementX(int val) {
        this.x += val;
    }

    public final void incrementY(int val) {
        this.y += val;
    }

    public final void incrementZ(int val) {
        this.z += val;
    }

    public final void decrementX() {
        this.x--;
    }

    public final void decrementY() {
        this.y--;
    }

    public final void decrementZ() {
        this.z--;
    }

    public final void decrementX(int val) {
        this.x -= val;
    }

    public final void decrementY(int val) {
        this.y -= val;
    }

    public final void decrementZ(int val) {
        this.z -= val;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d,%d]", x, y, z);
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int z() {
        return z;
    }
}
