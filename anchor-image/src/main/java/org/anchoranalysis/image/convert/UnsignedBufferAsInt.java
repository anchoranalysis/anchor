package org.anchoranalysis.image.convert;

import java.nio.Buffer;

/**
 * A {@link UnsignedBuffer} that exposes its internal types as {@code int}.
 * 
 * <p>This means that an {@code int} is returned by {@code getUnsigned} and accepted by {@code putUnsigned} and
 * related functions.
 * 
 * @author Owen Feehan
 *
 */
public abstract class UnsignedBufferAsInt extends UnsignedBuffer {

    public UnsignedBufferAsInt(Buffer delegate) {
        super(delegate);
    }

    /**
     * Gets an unsigned-byte (represented as a int) at the current buffer position.
     * 
     * @return unsigned-byte (represented by a int)
     */
    public abstract int getUnsigned();

    /**
     * Gets an unsigned-byte (represented as a int) at a particular buffer position.
     * 
     * @param index the buffer position
     * @return unsigned-byte (represented by a int)
     */
    public abstract int getUnsigned(int index);
    
    /**
     * Puts an unsigned-byte (represented by an int) at current buffer position.
     * 
     * <p>A conversion occurs from int to byte.
     * 
     * @param value the unsigned-byte (represented by an int)
     */
    public abstract void putUnsigned(int value);

    /**
     * Puts an unsigned-byte (represented as a int) a particular buffer position.
     * 
     * <p>A conversion occurs from int to byte.
     * 
     * @param index the buffer position
     * @param value the unsigned-byte (represented by an int)
     */
    public abstract void putUnsigned(int index, int value);
}
