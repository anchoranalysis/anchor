/* (C)2020 */
package org.anchoranalysis.image.convert;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;

public class ByteConverter {

    private ByteConverter() {}

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
