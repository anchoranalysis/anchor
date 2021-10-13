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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.axis.AxisConverter;

/**
 * A <i>three</i>-dimensional tuple of <i>double</i> values.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode
@Accessors(fluent = true)
public abstract class Tuple3d implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    /** X-axis component of the tuple. */
    @Getter protected double x = 0.0;

    /** Y-axis component of the tuple. */
    @Getter protected double y = 0.0;

    /** Z-axis component of the tuple. */
    @Getter protected double z = 0.0;

    /**
     * Arithmetically adds values to each dimension's component.
     *
     * @param x value to add to X-axis component.
     * @param y value to add to Y-axis component.
     * @param z value to add to Z-axis component.
     */
    public final void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    /**
     * Arithmetically adds a {@link Tuple3d}'s values across each dimension.
     *
     * @param toAdd tuple to add to current state.
     */
    public final void add(Tuple3d toAdd) {
        this.x += toAdd.x;
        this.y += toAdd.y;
        this.z += toAdd.z;
    }

    /**
     * Arithmetically adds a {@link ReadableTuple3i}'s values across each dimension.
     *
     * @param toAdd tuple to add to current state.
     */
    public final void add(ReadableTuple3i toAdd) {
        this.x += toAdd.x();
        this.y += toAdd.y();
        this.z += toAdd.z();
    }

    /**
     * Arithmetically subtract a {@link Tuple3d}'s values across each dimension.
     *
     * @param toSubtract tuple to subtract from current state.
     */
    public final void subtract(Tuple3d toSubtract) {
        this.x -= toSubtract.x;
        this.y -= toSubtract.y;
        this.z -= toSubtract.z;
    }

    /**
     * Arithmetically multiply values across each dimension.
     *
     * @param factor value to multiply each component's value by.
     */
    public final void scale(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
    }

    /**
     * Arithmetically multiply values by a factor only in X and Y dimensions.
     *
     * @param factor value to multiply each component's value by.
     */
    public final void scaleXY(double factor) {
        this.x *= factor;
        this.y *= factor;
    }

    /**
     * Arithmetically divide values across each dimension.
     *
     * @param divisor value to divide each component's value by.
     */
    public final void divideBy(int divisor) {
        this.x /= divisor;
        this.y /= divisor;
        this.z /= divisor;
    }

    /**
     * Arithmetically divide values across each dimension.
     *
     * @param divisor value to divide each component's value by.
     */
    public final void divideBy(double divisor) {
        this.x /= divisor;
        this.y /= divisor;
        this.z /= divisor;
    }

    /** Convert each dimension's component into its absolute value. */
    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
    }

    /**
     * Increments each component's value by the corresponding value in a {@link Tuple3d}.
     *
     * @param shift how much to increment by.
     */
    public final void increment(Tuple3d shift) {
        incrementX(shift.x);
        incrementY(shift.y);
        incrementZ(shift.z);
    }

    /**
     * Increments the X component's value by a shift.
     *
     * @param shift how much to increment by.
     */
    public final void incrementX(double shift) {
        this.x += shift;
    }

    /**
     * Increments the Y component's value by a shift.
     *
     * @param shift how much to increment by.
     */
    public final void incrementY(double shift) {
        this.y += shift;
    }

    /**
     * Increments the Z component's value by a shift.
     *
     * @param shift how much to increment by.
     */
    public final void incrementZ(double shift) {
        this.z += shift;
    }

    /**
     * A component of a tuple corresponding to a particular axis.
     *
     * @param axis the axis.
     * @return the component of the tuple corresponding to that axis.
     */
    public final double valueByDimension(Axis axis) {
        switch (axis) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
            default:
                assert false;
                throw new AnchorFriendlyRuntimeException(AxisConverter.INVALID_AXIS_INDEX);
        }
    }

    /**
     * A component of a tuple corresponding to a particular dimension by index.
     *
     * @param dimensionIndex the index corresponding to an axis, as per {@link AxisConverter}.
     * @return the component of the tuple corresponding to that axis.
     */
    public final double valueByDimension(int dimensionIndex) {
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

    /**
     * Assigns a value to a component of a tuple corresponding to a particular dimension by index.
     *
     * @param dimensionIndex the index corresponding to an axis, as per {@link AxisConverter}.
     * @param valueToAssign the value to assign.
     */
    public final void setValueByDimension(int dimensionIndex, double valueToAssign) {
        switch (dimensionIndex) {
            case 0:
                this.x = valueToAssign;
                break;
            case 1:
                this.y = valueToAssign;
                break;
            case 2:
                this.z = valueToAssign;
                break;
            default:
                throw new AnchorFriendlyRuntimeException(AxisConverter.INVALID_AXIS_STRING);
        }
    }

    @Override
    public String toString() {
        return String.format("[%5.1f,%5.1f,%5.1f]", x, y, z);
    }

    /**
     * X-axis component of the tuple.
     *
     * @param x the value to set.
     */
    public final void setX(int x) {
        this.x = x;
    }

    /**
     * Y-axis component of the tuple.
     *
     * @param y the value to set.
     */
    public final void setY(int y) {
        this.y = y;
    }

    /**
     * Z-axis component of the tuple.
     *
     * @param z the value to set.
     */
    public final void setZ(int z) {
        this.z = z;
    }

    /**
     * X-axis component of the tuple.
     *
     * @param x the value to set.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Y-axis component of the tuple.
     *
     * @param y the value to set.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Z-axis component of the tuple.
     *
     * @param z the value to set.
     */
    public void setZ(double z) {
        this.z = z;
    }
}
