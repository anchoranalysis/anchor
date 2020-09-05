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

package org.anchoranalysis.image.convert;

import java.nio.Buffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Type conversion between primitive data types.
 * 
 * <p>This class is intended to help with conversion of elements retrieved from {@link Buffer}
 * and its child-types.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrimitiveConverter {

    public static int unsignedByteToInt(byte value) {
        return Byte.toUnsignedInt(value);
    }

    public static int unsignedShortToInt(short value) {
        return Short.toUnsignedInt(value);
    }

    public static int unsignedIntToShort(int value) {
        return (short) value;
    }

    public static int unsignedByteToShort(byte value) {
        return (short) unsignedByteToInt(value);
    }

    public static long unsignedIntToLong(int value) {
        return Integer.toUnsignedLong(value);
    }

    public static int unsignedIntToInt(int value) {
        return value;
    }
}
