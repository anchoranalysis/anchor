/*-
 * #%L
 * anchor-image-core
 * %%
 * Copyright (C) 2010 - 2025 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.core.stack;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.math.arithmetic.DoubleUtilities;

/**
 * The GPS coordinates associated with an image.
 *
 * <p>It has a custom equality method to handle floating point precision issues.
 */
@AllArgsConstructor
public class ImageLocation {

    /**
     * the latitude, in degrees (can be positive or negative).
     *
     * <p>It is specified in degrees where positive values are north of the equator, and negative
     * values are south of it.
     */
    @Getter private double latitude;

    /**
     * the longitude, in degrees.
     *
     * <p>It is specified in degrees where positive values are east of the prime meridian, and
     * negative values are west of it.
     */
    @Getter private double longitude;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImageLocation objCast) {
            return DoubleUtilities.areEqual(latitude, objCast.latitude)
                    && DoubleUtilities.areEqual(longitude, objCast.longitude);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundedDoubleToBits(latitude), roundedDoubleToBits(longitude));
    }

    /** Convert a double (rounded) to a bit-representation in the form of a long. */
    private static long roundedDoubleToBits(double value) {
        return Double.doubleToLongBits(Math.round(value * 1e6) / 1e6);
    }
}
