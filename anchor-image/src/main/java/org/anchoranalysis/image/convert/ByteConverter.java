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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ByteConverter {

    public static int unsignedByteToInt(byte b) {
        return b & 0xff;
    }

    public static int unsignedShortToInt(short s) {
        return s & 0xffff;
    }

    public static int unsignedIntToShort(int i) {
        return (short) i;
    }

    public static int unsignedByteToShort(byte b) {
        return (short) unsignedByteToInt(b);
    }

    public static long unsignedIntToLong(int v) {
        return (long) v;
    }

    public static int unsignedIntToInt(int v) {
        return v;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Buffer> T copy(T b) {

        if (b instanceof ByteBuffer) {
            return (T) copy((ByteBuffer) b);
        } else if (b instanceof FloatBuffer) {
            return (T) copy((FloatBuffer) b);
        } else if (b instanceof ShortBuffer) {
            return (T) copy((ShortBuffer) b);
        } else if (b instanceof IntBuffer) {
            return (T) copy((IntBuffer) b);
        } else {
            throw new AnchorImpossibleSituationException();
        }
    }

    public static ByteBuffer copy(ByteBuffer original) {
        ByteBuffer clone = ByteBuffer.allocate(original.capacity());
        original.rewind(); // copy from the beginning
        clone.put(original);
        original.rewind();
        clone.flip();
        return clone;
    }

    public static FloatBuffer copy(FloatBuffer original) {
        FloatBuffer clone = FloatBuffer.allocate(original.capacity());
        original.rewind(); // copy from the beginning
        clone.put(original);
        original.rewind();
        clone.flip();
        return clone;
    }

    public static ShortBuffer copy(ShortBuffer original) {
        ShortBuffer clone = ShortBuffer.allocate(original.capacity());
        original.rewind(); // copy from the beginning
        clone.put(original);
        original.rewind();
        clone.flip();
        return clone;
    }
}
