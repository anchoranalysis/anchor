package org.anchoranalysis.core.axis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

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
 * Converts to/from {@link AxisType} to string representations and integer index (0 for X, 1 for Y,
 * 2 for Z)
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AxisTypeConverter {

    public static final String INVALID_AXIS_INDEX = "Index must be >=0 and <3";

    public static final String UNKNOWN_AXIS_TYPE = "Unknown axis type";

    /** Maps a string of x, y, z (case ignored) to a corresponding axis type */
    public static AxisType createFromString(String axis) {
        if ("x".equalsIgnoreCase(axis)) {
            return AxisType.X;

        } else if ("y".equalsIgnoreCase(axis)) {
            return AxisType.Y;

        } else if ("z".equalsIgnoreCase(axis)) {
            return AxisType.Z;

        } else {
            throw new AnchorFriendlyRuntimeException(INVALID_AXIS_INDEX);
        }
    }

    /** An index representing each dimension where X->0, Y->1, Z->2 */
    public static int dimensionIndexFor(AxisType axis) {
        switch (axis) {
            case X:
                return 0;
            case Y:
                return 1;
            case Z:
                return 2;
            default:
                throw new AnchorFriendlyRuntimeException(UNKNOWN_AXIS_TYPE);
        }
    }

    /** Multiplexes the correct value for the given dimension */
    public static int valueFor(AxisType axisType, int x, int y, int z) {
        switch (axisType) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
            default:
                throw new AnchorFriendlyRuntimeException(UNKNOWN_AXIS_TYPE);
        }
    }
}
