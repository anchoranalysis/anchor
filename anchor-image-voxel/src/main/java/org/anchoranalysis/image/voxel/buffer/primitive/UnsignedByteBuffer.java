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
package org.anchoranalysis.image.voxel.buffer.primitive;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

/**
 * Wraps a {@code ByteBuffer} but automatically performs conversion to {@code int}.
 *
 * <p>The conversion applies to {@link ByteBuffer#get} and {@link ByteBuffer#put} of single
 * elements, but not to any mass get or put operations.
 *
 * <p>The user has a choice of getting/setting using raw ({@link #getRaw}, {@link #putRaw} etc.) or
 * unsigned-conversion ({@link #getUnsigned}, {@link #putUnsigned} etc.) methods. The raw methods
 * are always more efficient, and so should be preferred when conversion is not needed.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public final class UnsignedByteBuffer extends UnsignedBufferAsInt {

    /** The underlying storage buffer, to which calls are delegated with our without conversion. */
    @Getter @EqualsAndHashCode.Exclude private final ByteBuffer delegate;

    /**
     * Allocates a new buffer of unsigned-bytes.
     *
     * @param capacity size of buffer.
     * @return newly created buffer (non-direct, i.e. backed by an array).
     */
    public static UnsignedByteBuffer allocate(int capacity) {
        return new UnsignedByteBuffer(ByteBuffer.allocate(capacity));
    }

    /**
     * Exposes a raw byte-array as a buffer with unsigned-bytes.
     *
     * @param array the byte-array
     * @return a new instance of {@link UnsignedByteBuffer} that reuses {@code array} internally.
     */
    public static UnsignedByteBuffer wrapRaw(byte[] array) {
        return new UnsignedByteBuffer(ByteBuffer.wrap(array));
    }

    /**
     * Exposes a raw {@link ByteBuffer} as a buffer with unsigned-bytes.
     *
     * @param bufferRaw the raw-buffer
     * @return a new instance of {@link UnsignedByteBuffer} that reuses {@code bufferRaw}
     *     internally.
     */
    public static UnsignedByteBuffer wrapRaw(ByteBuffer bufferRaw) {
        return new UnsignedByteBuffer(bufferRaw);
    }

    /**
     * Creates given a delegate.
     *
     * @param delegate the delegate
     */
    private UnsignedByteBuffer(ByteBuffer delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    /**
     * Gets an unsigned-byte (represented as a byte) at the current buffer position.
     *
     * <p>No further conversion occurs, so this method is more efficient than {@link
     * #getUnsigned()}.
     *
     * @return unsigned-byte (represented by a byte)
     */
    public byte getRaw() {
        return delegate.get();
    }

    /**
     * Gets an unsigned-byte (represented as a byte) at a particular buffer position.
     *
     * <p>No further conversion occurs, so this method is more efficient than {@link
     * #getUnsigned(int)}.
     *
     * @param index the buffer position
     * @return unsigned-byte (represented by a byte)
     */
    public byte getRaw(int index) {
        return delegate.get(index);
    }

    @Override
    public int getUnsigned() {
        return PrimitiveConverter.unsignedByteToInt(getRaw());
    }

    @Override
    public int getUnsigned(int index) {
        return PrimitiveConverter.unsignedByteToInt(getRaw(index));
    }

    /**
     * Puts an unsigned-byte (represented as a byte) at the current buffer position.
     *
     * <p>No further conversion occurs, so this method is more efficient than {@link
     * #putUnsigned(int)}.
     *
     * @param value unsigned-byte (represented by a byte)
     */
    public void putRaw(byte value) {
        delegate.put(value);
    }

    /**
     * Puts an unsigned-byte (represented as a byte) a particular buffer position.
     *
     * <p>No further conversion occurs, so this method is more efficient than {@link
     * #putUnsigned(int,int)}.
     *
     * @param index the buffer position
     * @param value the unsigned-byte (represented by an int)
     */
    public void putRaw(int index, byte value) {
        delegate.put(index, value);
    }

    @Override
    public void putUnsigned(int value) {
        putRaw((byte) value);
    }

    @Override
    public void putUnsigned(int index, int value) {
        putRaw(index, (byte) value);
    }

    /**
     * Puts an unsigned-int (represented by a int) at current buffer position.
     *
     * <p>A conversion occurs from int to long and then to byte.
     *
     * @param value the unsigned-int (represented by an int)
     */
    public void putUnsignedInt(int value) {
        putRaw((byte) PrimitiveConverter.unsignedIntToLong(value));
    }

    @Override
    public void putLong(long value) {
        putRaw((byte) value);
    }

    @Override
    public void putFloat(float value) {
        putRaw((byte) value);
    }

    @Override
    public void putFloat(int index, float value) {
        putRaw(index, (byte) value);
    }

    /**
     * Puts a float at the current buffer position, clipping to ensure the value is within the range
     * {@code (0,255)}.
     *
     * @param value the float
     */
    public void putFloatClipped(float value) {
        if (value > UnsignedByteVoxelType.MAX_VALUE_INT) {
            value = UnsignedByteVoxelType.MAX_VALUE_INT;
        }
        if (value < 0) {
            value = 0;
        }
        putFloat(value);
    }

    @Override
    public void putDouble(double value) {
        putRaw((byte) value);
    }

    /**
     * Puts a double at the current buffer position, clipping to ensure the value is within the
     * range {@code (0,255)}.
     *
     * @param value the double
     */
    public void putDoubleClipped(double value) {
        if (value > 255) {
            value = 255;
        }
        if (value < 0) {
            value = 0;
        }
        putDouble(value);
    }

    @Override
    public void putDouble(int index, double value) {
        putRaw(index, (byte) value);
    }

    /**
     * Relative put-method from an unsigned byte buffer, represented by a {@link ByteBuffer}.
     *
     * <p>This is identical to {@link ByteBuffer#put(ByteBuffer)}, with no return value.
     *
     * @param source source of bytes to put
     */
    public void put(ByteBuffer source) {
        delegate.put(source);
    }

    /**
     * Relative put-method from an unsigned byte buffer, represented by a {@link
     * UnsignedByteBuffer}.
     *
     * <p>This is identical to {@link ByteBuffer#put(ByteBuffer)}, with no return value.
     *
     * @param source source of bytes to put
     */
    public void put(UnsignedByteBuffer source) {
        delegate.put(source.getDelegate());
    }

    /**
     * The array of the buffer ala {@link ByteBuffer#array}.
     *
     * <p>Unlike {@link ByteBuffer#array} an array will always be returned, copying it into a newly
     * created array, if it cannot be directly accessed.
     *
     * @return the array
     */
    public final byte[] array() {
        Preconditions.checkArgument(delegate.hasArray());
        return delegate.array();
    }

    /** Rewinds the buffer ala {@link ByteBuffer#rewind}. */
    public final void rewind() {
        delegate.rewind();
    }
}
