package org.anchoranalysis.image.convert;

import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import lombok.Getter;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Wraps an {@code IntBuffer} but automatically performs conversion to {@code long}.
 * 
 * <p>The conversion applies to {@link IntBuffer#get} and {@link IntBuffer#put} of single
 * elements, but not to any mass get or put operations.
 * 
 * <p>The user has a choice of getting/setting using raw ({@link #getRaw}, {@link #putRaw} etc.) or
 * unsigned-conversion ({@link #getUnsigned}, {@link #putUnsigned} etc.) methods. The raw methods are always more
 * efficient, and so should be preferred when conversion is not needed.
 * 
 * @author Owen Feehan
 *
 */
public final class UnsignedIntBuffer extends UnsignedBuffer {

    /** The underlying storage buffer, to which calls are delegated with our without conversion. */
    @Getter private final IntBuffer delegate;

    /**
     * Allocates a new (direct) buffer of unsigned-ints.
     * 
     * @param capacity size of buffer.
     * @return newly created buffer (non-direct, i.e. backed by an array).
     */
    public static UnsignedIntBuffer allocate(int capacity) {
        return new UnsignedIntBuffer( IntBuffer.allocate(capacity) );
    }

    /**
     * Exposes a raw int-array as a buffer with unsigned-ints.
     * 
     * @param array the int-array
     * @return a new instance of {@link UnsignedIntBuffer} that reuses {@code array} internally. 
     */
    public static UnsignedIntBuffer wrapRaw(int[] array) {
        return new UnsignedIntBuffer( IntBuffer.wrap(array) );
    }

    /**
     * Exposes a raw {@link UnsignedIntBuffer} as a buffer with unsigned-ints.
     * 
     * @param bufferRaw the raw-buffer
     * @return a new instance of {@link UnsignedIntBuffer} that reuses {@code bufferRaw} internally. 
     */
    public static UnsignedIntBuffer wrapRaw(IntBuffer bufferRaw) {
        return new UnsignedIntBuffer(bufferRaw);
    }
    
    /**
     * Creates given a delegate.
     * 
     * @param delegate the delegate
     */
    private UnsignedIntBuffer(IntBuffer delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    /**
     * Gets an unsigned-int (represented as an int) at the current buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #getUnsigned()}.
     * 
     * @return unsigned-int (represented by an int)
     */
    public int getRaw() {
        return delegate.get();
    }
    
    /**
     * Gets an unsigned-int (represented as a int) at a particular buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #getUnsigned(int)}.
     * 
     * @param index the buffer position
     * @return unsigned-int (represented by a int)
     */
    public int getRaw(int index) {
        return delegate.get(index);
    }
    
    /**
     * Gets an unsigned-int (represented as a long) at the current buffer position.
     * 
     * @return unsigned-int (represented by a long)
     */
    public long getUnsigned() {
        return PrimitiveConverter.unsignedIntToLong( getRaw() );
    }
    
    /**
     * Gets an unsigned-int (represented as a long) at a particular buffer position.
     * 
     * @param index the buffer position
     * @return unsigned-int (represented by a long)
     */
    public long getUnsigned(int index) {
        return PrimitiveConverter.unsignedIntToLong( getRaw(index) );
    }

    /**
     * Puts an unsigned-int (represented as a int) at the current buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #putUnsigned(long)}.
     * 
     * @param value unsigned-int (represented by a int)
     */
    public void putRaw(int value) {
        delegate.put(value);
    }
    
    /**
     * Puts an unsigned-int (represented as a int) a particular buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #putUnsigned(int,long)}.
     * 
     * @param index the buffer position
     * @param value the unsigned-int (represented by an int)
     */
    public void putRaw(int index, int value) {
        delegate.put(index, value);
    }
    
    /**
     * Puts an unsigned-int (represented by a long) at current buffer position.
     * 
     * <p>A conversion occurs from long to int.
     * 
     * @param value the unsigned-int (represented by a long)
     */
    public void putUnsigned(long value) {
        putRaw( (int) value);
    }
    
    /**
     * Puts an unsigned-int (represented as a long) a particular buffer position.
     * 
     * <p>A conversion occurs from long to int.
     * 
     * @param index the buffer position
     * @param value the unsigned-int (represented by a long)
     */
    public void putUnsigned(int index, long value) {
        putRaw( index, (int) value);
    }

    /**
     * Puts a float at the current buffer position.
     * 
     * <p>A conversion occurs from float to int.
     * 
     * @param value the float.
     */
    public void putFloat(float value) {
        putRaw((int) value);
    }
    
    /**
     * Puts a float at a particular buffer position.
     * 
     * <p>A conversion occurs from float to int.
     * 
     * @param index the buffer position
     * @param value the float.
     */ 
    public void putFloat(int index, float value) {
        putRaw(index, (int) value);
    }
    
    /**
     * Puts a double at the current buffer position.
     * 
     * <p>A conversion occurs from double to int.
     * 
     * @param value the double
     */
    public void putDouble(double value) {
        putRaw((int) value);
    }

    /**
     * Puts a double at a particular buffer position.
     * 
     * <p>A conversion occurs from double to int.
     * 
     * @param index the buffer position
     * @param value the double
     */
    public void putDouble(int index, double value) {
        putRaw(index, (int) value);
    }
    
    /**
     * The array of the buffer ala {@link ShortBuffer#array}.
     * 
     * <p>Note that that {@link #hasArray} should first be called to check an array exists.
     * 
     * @return the array
     */
    public final int[] array() {
        return delegate.array();
    }

    public IntBuffer put(int arg0, int arg1) {
        return delegate.put(arg0, arg1);
    }
}
