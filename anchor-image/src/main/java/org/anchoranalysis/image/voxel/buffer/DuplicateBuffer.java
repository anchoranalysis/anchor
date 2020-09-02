package org.anchoranalysis.image.voxel.buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
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
    public static <T extends Buffer> T copy(T buffer) {

        if (buffer instanceof ByteBuffer) {
            return (T) copy((ByteBuffer) buffer);
        } else if (buffer instanceof FloatBuffer) {
            return (T) copy((FloatBuffer) buffer);
        } else if (buffer instanceof ShortBuffer) {
            return (T) copy((ShortBuffer) buffer);
        } else if (buffer instanceof IntBuffer) {
            return (T) copy((IntBuffer) buffer);
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
}
