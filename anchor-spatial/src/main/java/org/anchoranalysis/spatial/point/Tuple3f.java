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
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.axis.AxisConverter;

/**
 * A <i>three</i>-dimensional tuple of <i>float</i> values.
 *
 * @author Owen Feehan
 */
@Data
@Accessors(fluent = true)
public abstract class Tuple3f implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    /** X-axis component of the tuple. */
    protected float x = 0.0f;

    /** Y-axis component of the tuple. */
    protected float y = 0.0f;

    /** Z-axis component of the tuple. */
    protected float z = 0.0f;

    /**
     * A component of a tuple corresponding to a particular axis.
     *
     * @param axis the axis.
     * @return the component of the tuple corresponding to that axis.
     */
    public final float valueByDimension(Axis axis) {
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
    public final float valueByDimension(int dimensionIndex) {
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
    public final void setValueByDimension(int dimensionIndex, float valueToAssign) {
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
        return String.format("[%f,%f,%f]", x, y, z);
    }
}
