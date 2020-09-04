/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.image.stack.rgb;

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.Dimensions;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RGBOutputUtils {

    public static void writeRGBColorToByteArr(
            RGBColor color,
            Point3i point,
            Dimensions dimensions,
            UnsignedByteBuffer red,
            UnsignedByteBuffer blue,
            UnsignedByteBuffer green) {
        int index = dimensions.offsetSlice(point);
        red.put(index, (byte) color.getRed());
        green.put(index, (byte) color.getGreen());
        blue.put(index, (byte) color.getBlue());
    }
}
