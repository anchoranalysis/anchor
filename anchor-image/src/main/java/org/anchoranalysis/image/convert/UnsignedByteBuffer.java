package org.anchoranalysis.image.convert;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Wraps a {@code ByteBuffer} but automatically performs conversion to {@code int}.
 * 
 * <p>The conversion applies to {@link ByteBuffer#get} and {@link ByteBuffer#put} of single
 * elements, but not to any mass get or put operations.
 * 
 * <p>The user has a choice of getting/setting using raw ({@link #getRaw}, {@link #putRaw} etc.) or
 * unsigned-conversion ({@link #getUnsignedByte}, {@link #putUnsignedByte} etc.) methods. The raw methods are always more
 * efficient, and so should be preferred when conversion is not needed.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public final class UnsignedByteBuffer {

    @Getter private final ByteBuffer delegate;
    
    /**
     * Allocates a new (direct) buffer of unsigned-bytes.
     * 
     * @param capacity size of buffer.
     * @return newly created buffer (non-direct, i.e. backed by an array).
     */
    public static UnsignedByteBuffer allocate(int capacity) {
        return new UnsignedByteBuffer( ByteBuffer.allocate(capacity) );
    }

    /**
     * Exposes a raw byte-array as a buffer with unsigned bytes.
     * 
     * @param array the byte-array
     * @return a new instance of {@link UnsignedByteBuffer} that reuses {@code array} internally. 
     */
    public static UnsignedByteBuffer wrapRaw(byte[] array) {
        return new UnsignedByteBuffer( ByteBuffer.wrap(array) );
    }

    /**
     * Exposes a raw {@link ByteBuffer} as a buffer with unsigned bytes.
     * 
     * @param bufferRaw the raw-buffer
     * @return a new instance of {@link UnsignedByteBuffer} that reuses {@code bufferRaw} internally. 
     */
    public static UnsignedByteBuffer wrapRaw(ByteBuffer bufferRaw) {
        return new UnsignedByteBuffer(bufferRaw);
    }
    
    /**
     * Gets an unsigned-byte (represented as a byte) at the current buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #getUnsignedByte()}.
     * 
     * @return unsigned-byte (represented by a byte)
     */
    public byte getRaw() {
        return delegate.get();
    }

    /**
     * Gets an unsigned-byte (represented as a byte) at a particular buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #getUnsignedByte(int)}.
     * 
     * @param index the buffer position
     * @return unsigned-byte (represented by a byte)
     */
    public byte getRaw(int index) {
        return delegate.get(index);
    }
    
    /**
     * Gets an unsigned-byte (represented as a int) at the current buffer position.
     * 
     * @return unsigned-byte (represented by a int)
     */
    public int getUnsignedByte() {
        return PrimitiveConverter.unsignedByteToInt( getRaw() );
    }

    /**
     * Gets an unsigned-byte (represented as a int) at a particular buffer position.
     * 
     * @param index the buffer position
     * @return unsigned-byte (represented by a int)
     */
    public int getUnsignedByte(int index) {
        return PrimitiveConverter.unsignedByteToInt( getRaw(index) );
    }
    
    /**
     * Puts an unsigned-byte (represented as a byte) at the current buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #putUnsignedByte(int)}.
     * 
     * @param value unsigned-byte (represented by a byte)
     */
    public void putRaw(byte value) {
        delegate.put(value);
    }

    /**
     * Puts an unsigned-byte (represented as a byte) a particular buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #putUnsignedByte(int,int)}.
     * 
     * @param index the buffer position
     * @param value the unsigned-byte (represented by an int)
     */
    public void putRaw(int index, byte value) {
        delegate.put(index, value);
    }
    
    /**
     * Puts an unsigned-byte (represented by an int) at current buffer position.
     * 
     * <p>A conversion occurs from int to byte.
     * 
     * @param value the unsigned-byte (represented by an int)
     */
    public void putUnsignedByte(int value) {
        putRaw( (byte) value);
    }

    /**
     * Puts an unsigned-byte (represented as a int) a particular buffer position.
     * 
     * <p>A conversion occurs from int to byte.
     * 
     * @param index the buffer position
     * @param value the unsigned-byte (represented by an int)
     */
    public void putUnsignedByte(int index, int value) {
        putRaw(index, (byte) value);
    }

    /**
     * Puts an unsigned-short (represented by a short) at current buffer position.
     * 
     * <p>A conversion occurs from short to int and then to byte.
     * 
     * @param value the unsigned-short (represented by a short)
     */
    public void putUnsignedShort(short value) {
        putRaw( (byte) PrimitiveConverter.unsignedShortToInt(value) );
    }

    /**
     * Puts an unsigned-int (represented by a int) at current buffer position.
     * 
     * <p>A conversion occurs from int to long and then to byte.
     * 
     * @param value the unsigned-int (represented by an int)
     */
    public void putUnsignedInt(int value) {
        putRaw( (byte) PrimitiveConverter.unsignedIntToLong(value) );
    }
    
    /**
     * Puts a signed float (represented by a float) at the current buffer position.
     * 
     * <p>A conversion occurs from float to byte.
     * 
     * @param value the float.
     */
    public void putFloat(float value) {
        putRaw( (byte) value);
    }

    /**
     * Puts a signed float (represented by a float) at a particular buffer position.
     * 
     * <p>A conversion occurs from float to byte.
     * 
     * @param index the buffer position
     * @param value the float.
     */    
    public void putFloat(int index, float value) {
        putRaw( index, (byte) value);
    }
    
    /**
     * Puts a signed double (represented by a double) at the current buffer position.
     * 
     * <p>A conversion occurs from double to byte.
     * 
     * @param value the double
     */
    public void putDouble(double value) {
        putRaw( (byte) value);
    }

    /**
     * Relative put-method from an unsigned byte buffer, represented by a {@link ByteBuffer}.
     * 
     * This is identical to {@link ByteBuffer#put(ByteBuffer)}, with no return value.
     * 
     * @param source source of bytes to put
     */
    public void put(ByteBuffer source) {
        delegate.put(source);
    }

    /**
     * Relative put-method from an unsigned byte buffer, represented by a {@link UnsignedByteBuffer}.
     * 
     * This is identical to {@link ByteBuffer#put(ByteBuffer)}, with no return value.
     * 
     * @param source source of bytes to put
     */
    public void put(UnsignedByteBuffer source) {
        delegate.put(source.getDelegate());
    }

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
     * The array of the buffer ala {@link Buffer#array}.
     * 
     * @return the array
     */
    public final byte[] array() {
        return delegate.array();
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
}
