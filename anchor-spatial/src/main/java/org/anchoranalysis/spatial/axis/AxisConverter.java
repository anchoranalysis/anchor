package org.anchoranalysis.spatial.axis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;

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
 * Converts to/from {@link Axis} to a {@link String} and {@code int} representations, checking for
 * invalid input.
 *
 * <p>The {@code int} representation is is <code>0</code> for the X-axis, <code>1</code> for the
 * Y-axis, and <code>2</code> for the Z-axis.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AxisConverter {

    /**
     * Human-readable error message used when an invalid {@link String}-value for {@code axis} is
     * passed.
     */
    public static final String INVALID_AXIS_STRING = "axis must be: x or y or z";

    /**
     * Human-readable error message used when an invalid <code>int</code>-value for {@code axis} is
     * passed.
     */
    public static final String INVALID_AXIS_INDEX = "the index for axis must be: 0 or 1 or 2";

    /**
     * Maps a string of x, y, z (case ignored) to a corresponding axis type.
     *
     * @param axisAsString the string indicating the axis.
     * @return the corresponding {@link Axis} for {@code axisAsString}.
     * @throws AxisConversionException if {@code axisAsString} does not correspond to an axis.
     */
    public static Axis createFromString(String axisAsString) throws AxisConversionException {
        if ("x".equalsIgnoreCase(axisAsString)) {
            return Axis.X;

        } else if ("y".equalsIgnoreCase(axisAsString)) {
            return Axis.Y;

        } else if ("z".equalsIgnoreCase(axisAsString)) {
            return Axis.Z;

        } else {
            throw new AxisConversionException(INVALID_AXIS_STRING);
        }
    }

    /**
     * A corresponding index representing for the dimension an {@link Axis} represents.
     *
     * <p>This is <code>0</code> for the X-axis, <code>1</code> for the Y-axis, and <code>2</code>
     * for the Z-axis.
     *
     * @param axis the axis to find an index for.
     * @return the index corresponding to {@code axis}.
     * @throws AxisConversionException if {@code axis} is unrecognized.
     */
    public static int dimensionIndexFor(Axis axis) throws AxisConversionException {
        switch (axis) {
            case X:
                return 0;
            case Y:
                return 1;
            case Z:
                return 2;
            default:
                throw new AxisConversionException(INVALID_AXIS_INDEX);
        }
    }

    /**
     * Multiplexes the a value corresponding to a particular axis.
     *
     * @param axis the axis used to select a value.
     * @param x a possible-value, representing the X-axis.
     * @param y a possible-value, representing the Y-axis.
     * @param z a possible-value, representing the Z-axis.
     * @return one of {@code x} or {@code y} or {@code z} corresponding to {@code axis}.
     */
    public static int valueFor(Axis axis, int x, int y, int z) {
        switch (axis) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
            default:
                throw new AnchorImpossibleSituationException();
        }
    }
}
