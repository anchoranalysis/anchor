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
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Deep-copies {@link Buffer} and its child-types.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class DuplicateBuffer {

    @SuppressWarnings("unchecked")
    public static <T> T copy(T buffer) {

        if (buffer instanceof ByteBuffer) {
            return (T) copy((ByteBuffer) buffer);
        } else if (buffer instanceof FloatBuffer) {
            return (T) copy((FloatBuffer) buffer);
        } else if (buffer instanceof ShortBuffer) {
            return (T) copy((ShortBuffer) buffer);
        } else if (buffer instanceof IntBuffer) {
            return (T) copy((IntBuffer) buffer);
        } if (buffer instanceof UnsignedByteBuffer) {
            return (T) copy((UnsignedByteBuffer) buffer);
        } else {
            throw new AnchorImpossibleSituationException();
        }
    }

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
    
    public static UnsignedByteBuffer copy(UnsignedByteBuffer buffer) {
        return new UnsignedByteBuffer( copy(buffer.getDelegate()) );
    }
}