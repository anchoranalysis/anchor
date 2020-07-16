package org.anchoranalysis.core.geometry;

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
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.axis.AxisTypeConverter;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

@EqualsAndHashCode
public abstract class Tuple3d implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    @Getter protected double x = 0.0;

    @Getter protected double y = 0.0;

    @Getter protected double z = 0.0;

    public final void add(Tuple3d point) {
        this.x += point.x;
        this.y += point.y;
        this.z += point.z;
    }

    public final void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public final void add(ReadableTuple3i point) {
        this.x += point.getX();
        this.y += point.getY();
        this.z += point.getZ();
    }

    public final void subtract(Tuple3d point) {
        this.x -= point.x;
        this.y -= point.y;
        this.z -= point.z;
    }

    public final void scale(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
    }

    public final void scaleXY(double factor) {
        this.x *= factor;
        this.y *= factor;
    }

    public final void divideBy(int divisor) {
        this.x /= divisor;
        this.y /= divisor;
        this.z /= divisor;
    }

    public final void divideBy(double divisor) {
        this.x /= divisor;
        this.y /= divisor;
        this.z /= divisor;
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
    }

    public final void increment(Tuple3d incrBy) {
        incrementX(incrBy.x);
        incrementY(incrBy.y);
        incrementZ(incrBy.z);
    }

    public final void incrementX(double val) {
        this.x += val;
    }

    public final void incrementY(double val) {
        this.y += val;
    }

    public final void incrementZ(double val) {
        this.z += val;
    }

    public final double getValueByDimension(int dimIndex) {
        if (dimIndex == 0) {
            return x;
        } else if (dimIndex == 1) {
            return y;
        } else if (dimIndex == 2) {
            return z;
        } else {
            throw new AnchorFriendlyRuntimeException(AxisTypeConverter.INVALID_AXIS_INDEX);
        }
    }

    public final double getValueByDimension(AxisType axisType) {
        switch (axisType) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
            default:
                assert false;
                throw new AnchorFriendlyRuntimeException(AxisTypeConverter.UNKNOWN_AXIS_TYPE);
        }
    }

    public final void setValueByDimension(int dimIndex, double val) {
        switch (dimIndex) {
            case 0:
                this.x = val;
                break;
            case 1:
                this.y = val;
                break;
            case 2:
                this.z = val;
                break;
            default:
                throw new AnchorFriendlyRuntimeException(AxisTypeConverter.INVALID_AXIS_INDEX);
        }
    }

    public final double dot(Tuple3d vec) {
        return (x * vec.x) + (y * vec.y) + (z * vec.z);
    }

    @Override
    public String toString() {
        return String.format("[%5.1f,%5.1f,%5.1f]", x, y, z);
    }

    public final void setX(int x) {
        this.x = (double) x;
    }

    public final void setY(int y) {
        this.y = (double) y;
    }

    public final void setZ(int z) {
        this.z = (double) z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
