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
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.axis.AxisConverter;

/**
 * A <i>three</i>-dimensional tuple of <i>int</i> values.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode
public abstract class Tuple3i implements ReadableTuple3i {

    /** */
    private static final long serialVersionUID = 1L;

    /** X-axis component of the tuple. */
    @Setter protected int x = 0;

    /** Y-axis component of the tuple. */
    @Setter protected int y = 0;

    /** Z-axis component of the tuple. */
    @Setter protected int z = 0;

    /**
     * Arithmetically adds a {@link ReadableTuple3i}'s values across each dimension.
     *
     * @param toAdd tuple to add to current state.
     */
    public final void add(ReadableTuple3i toAdd) {
        this.x = this.x + toAdd.x();
        this.y = this.y + toAdd.y();
        this.z = this.z + toAdd.z();
    }

    /**
     * Arithmetically subtracts a value across each dimension.
     *
     * @param valueToSubtract value to subtract.
     */
    public final void subtract(int valueToSubtract) {
        this.x = this.x - valueToSubtract;
        this.y = this.y - valueToSubtract;
        this.z = this.z - valueToSubtract;
    }

    /**
     * Arithmetically subtracts a value across each dimension.
     *
     * @param valueToSubtract value to subtract, for each respective dimension.
     */
    public final void subtract(ReadableTuple3i valueToSubtract) {
        this.x = this.x - valueToSubtract.x();
        this.y = this.y - valueToSubtract.y();
        this.z = this.z - valueToSubtract.z();
    }

    /**
     * Arithmetically multiplies by a value across each dimension.
     *
     * @param factor value to multiply by.
     */
    public final void scale(int factor) {
        this.x = this.x * factor;
        this.y = this.y * factor;
        this.z = this.z * factor;
    }

    /**
     * Arithmetically multiplies by a value across each dimension.
     *
     * @param factor value to multiply by.
     */
    public final void scale(double factor) {
        scaleXY(factor);
        this.z = (int) (factor * this.z);
    }

    /**
     * Arithmetically divide by a value across each dimension.
     *
     * @param divisor value to divide by.
     */
    public final void divideBy(int divisor) {
        this.x = this.x / divisor;
        this.y = this.y / divisor;
        this.z = this.z / divisor;
    }

    /**
     * Arithmetically multiplies the X-axis component by a value.
     *
     * @param factor value to multiply by.
     */
    public final void scaleX(double factor) {
        this.x = (int) (factor * this.x);
    }

    /**
     * Arithmetically multiplies the Y-axis component by a value.
     *
     * @param factor value to multiply by.
     */
    public final void scaleY(double factor) {
        this.y = (int) (factor * this.y);
    }

    /**
     * Arithmetically multiplies the X- and Y-axis components by a value.
     *
     * @param factor value to multiply by.
     */
    public final void scaleXY(double factor) {
        scaleX(factor);
        scaleY(factor);
    }

    /**
     * Element-wise minimum between this point and another.
     *
     * @param point the other point.
     * @return a new point containing the minimum of the X, Y, Z components.
     */
    public Point3i min(ReadableTuple3i point) {
        return new Point3i(Math.min(x, point.x()), Math.min(y, point.y()), Math.min(z, point.z()));
    }

    /**
     * Element-wise maximum between this point and another.
     *
     * @param point the other point.
     * @return a new point containing the minimum of the X, Y, Z components.
     */
    public Point3i max(ReadableTuple3i point) {
        return new Point3i(Math.max(x, point.x()), Math.max(y, point.y()), Math.max(z, point.z()));
    }

    /**
     * Element-wise maximum between this point and a scalar.
     *
     * @param value the scalar.
     * @return a new point containing the minimum of the X, Y, Z components.
     */
    public Point3i max(int value) {
        return new Point3i(Math.max(x, value), Math.max(y, value), Math.max(z, value));
    }

    /**
     * A component of a tuple corresponding to a particular dimension by index.
     *
     * @param dimensionIndex the index corresponding to an axis, as per {@link AxisConverter}.
     * @return the component of the tuple corresponding to that axis.
     */
    public final int valueByDimension(int dimensionIndex) {
        if (dimensionIndex == 0) {
            return x;
        } else if (dimensionIndex == 1) {
            return y;
        } else if (dimensionIndex == 2) {
            return z;
        } else {
            throw new AnchorFriendlyRuntimeException(AxisConverter.INVALID_AXIS_STRING);
        }
    }

    @Override
    public final int valueByDimension(Axis axis) {
        switch (axis) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
            default:
                throw new AnchorFriendlyRuntimeException(AxisConverter.INVALID_AXIS_INDEX);
        }
    }

    /** Increments the X component's value by one. */
    public final void incrementX() {
        this.x++;
    }

    /** Increments the Y component's value by one. */
    public final void incrementY() {
        this.y++;
    }

    /** Increments the Z component's value by one. */
    public final void incrementZ() {
        this.z++;
    }

    /**
     * Increments the X component's value by a shift.
     *
     * @param shift how much to increment by.
     */
    public final void incrementX(int shift) {
        this.x += shift;
    }

    /**
     * Increments the Y component's value by a shift.
     *
     * @param shift how much to increment by.
     */
    public final void incrementY(int shift) {
        this.y += shift;
    }

    /**
     * Increments the Z component's value by a shift.
     *
     * @param shift how much to increment by.
     */
    public final void incrementZ(int shift) {
        this.z += shift;
    }

    /** Decrements the X component's value by one. */
    public final void decrementX() {
        this.x--;
    }

    /** Decrements the Y component's value by one. */
    public final void decrementY() {
        this.y--;
    }

    /** Decrements the Z component's value by one. */
    public final void decrementZ() {
        this.z--;
    }

    /**
     * Decrements the X component's value by a shift.
     *
     * @param shift how much to decrement by.
     */
    public final void decrementX(int shift) {
        this.x -= shift;
    }

    /**
     * Decrements the Y component's value by a shift.
     *
     * @param shift how much to decrement by.
     */
    public final void decrementY(int shift) {
        this.y -= shift;
    }

    /**
     * Decrements the Z component's value by a shift.
     *
     * @param shift how much to decrement by.
     */
    public final void decrementZ(int shift) {
        this.z -= shift;
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
