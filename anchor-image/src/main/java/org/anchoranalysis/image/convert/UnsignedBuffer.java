package org.anchoranalysis.image.convert;

import java.nio.Buffer;
import lombok.AllArgsConstructor;

/**
 * Base class for buffers that represent an unsigned-type in the signed-equivalent-type NIO {@link Buffer}.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public abstract class UnsignedBuffer {

    /** The delegate buffer */
    private Buffer delegate;
    
    /**
     * Whether there are elements between the current position and the limit {@link Buffer#hasRemaining}.
     * 
     * @return true iff elements exist
     */
    public final boolean hasRemaining() {
        return delegate.hasRemaining();
    }

    /**
     * The position of the buffer ala {@link Buffer#position}.
     * 
     * @return the position
     */
    public final int position() {
        return delegate.position();
    }

    /**
     * Assigns a new position to the buffer.
     * 
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     * 
     * @param newPosition the index to assign as position.
     */
    public final Buffer position(int newPosition) {
        return delegate.position(newPosition);
    }
    
    /**
     * The capacity of the buffer ala {@link Buffer#capacity}.
     * 
     * @return the capacity
     */
    public final int capacity() {
        return delegate.capacity();
    }

    /**
     * Clears the buffer ala {@link Buffer#clear}.
     */
    public final void clear() {
        delegate.clear();
    }
    
    /**
     * Is this buffer direct or non-direct?
     * 
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     * 
     * @return true iff the buffer is direct.
     */  
    public boolean isDirect() {
        return delegate.isDirect();
    }

    /**
     * Whether the buffer has an array?
     * 
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     * 
     * @return true if the buffer has an array
     */
    public boolean hasArray() {
        return delegate.hasArray();
    }
}
