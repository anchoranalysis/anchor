package org.anchoranalysis.spatial.point;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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
import lombok.Data;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.spatial.axis.AxisType;
import org.anchoranalysis.spatial.axis.AxisTypeConverter;

@Data
@Accessors(fluent = true)
public abstract class Tuple3f implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    protected float x = 0.0f;

    protected float y = 0.0f;

    protected float z = 0.0f;

    public final float valueByDimension(int dimIndex) {
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

    public final float valueByDimension(AxisType axisType) {
        switch (axisType) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
            default:
                assert false;
                throw new AnchorFriendlyRuntimeException(AxisTypeConverter.INVALID_AXIS_INDEX);
        }
    }

    public final void setValueByDimension(int dimIndex, float val) {
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
                throw new AnchorFriendlyRuntimeException(AxisTypeConverter.INVALID_AXIS_STRING);
        }
    }

    public float distanceSquared(Point3f point) {
        float sx = this.x - point.x;
        float sy = this.y - point.y;
        float sz = this.z - point.z;
        return (sx * sx) + (sy * sy) + (sz * sz);
    }

    public final double distance(Point3f point) {
        return Math.sqrt(distanceSquared(point));
    }

    @Override
    public String toString() {
        return String.format("[%f,%f,%f]", x, y, z);
    }
}
