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
package org.anchoranalysis.image.voxel.buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.convert.UnsignedBuffer;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;

/**
 * Deep-copies all child classes of {@link Buffer} and {@link UnsignedBuffer}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DuplicateBuffer {

    public static ByteBuffer copy(ByteBuffer buffer) {
        ByteBuffer clone = ByteBuffer.allocate(buffer.capacity());
        buffer.rewind(); // copy from the beginning
        clone.put(buffer);
        buffer.rewind();
        clone.flip();
        return clone;
    }

    public static FloatBuffer copy(FloatBuffer buffer) {
        FloatBuffer clone = FloatBuffer.allocate(buffer.capacity());
        buffer.rewind(); // copy from the beginning
        clone.put(buffer);
        buffer.rewind();
        clone.flip();
        return clone;
    }

    public static ShortBuffer copy(ShortBuffer buffer) {
        ShortBuffer clone = ShortBuffer.allocate(buffer.capacity());
        buffer.rewind(); // copy from the beginning
        clone.put(buffer);
        buffer.rewind();
        clone.flip();
        return clone;
    }

    public static IntBuffer copy(IntBuffer buffer) {
        IntBuffer clone = IntBuffer.allocate(buffer.capacity());
        buffer.rewind(); // copy from the beginning
        clone.put(buffer);
        buffer.rewind();
        clone.flip();
        return clone;
    }

    public static UnsignedByteBuffer copy(UnsignedByteBuffer buffer) {
        return UnsignedByteBuffer.wrapRaw(copy(buffer.getDelegate()));
    }

    public static UnsignedShortBuffer copy(UnsignedShortBuffer buffer) {
        return UnsignedShortBuffer.wrapRaw(copy(buffer.getDelegate()));
    }

    public static UnsignedIntBuffer copy(UnsignedIntBuffer buffer) {
        return UnsignedIntBuffer.wrapRaw(copy(buffer.getDelegate()));
    }
}
