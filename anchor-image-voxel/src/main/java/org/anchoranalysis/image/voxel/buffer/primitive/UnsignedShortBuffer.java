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
import java.nio.ShortBuffer;
import lombok.Getter;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;

/**
 * Wraps a {@code ShortBuffer} but automatically performs conversion to {@code int}.
 *
 * <p>The conversion applies to {@link ShortBuffer#get} and {@link ShortBuffer#put} of single
 * elements, but not to any mass get or put operations.
 *
 * <p>The user has a choice of getting/setting using raw ({@link #getRaw}, {@link #putRaw} etc.) or
 * unsigned-conversion ({@link #getUnsigned}, {@link #putUnsigned} etc.) methods. The raw methods
 * are always more efficient, and so should be preferred when conversion is not needed.
 *
 * @author Owen Feehan
 */
public final class UnsignedShortBuffer extends UnsignedBufferAsInt {

    /** The underlying storage buffer, to which calls are delegated with our without conversion. */
    @Getter private final ShortBuffer delegate;

    /**
     * Allocates a new (direct) buffer of unsigned-shorts.
     *
     * @param capacity size of buffer.
     * @return newly created buffer (non-direct, i.e. backed by an array).
     */
    public static UnsignedShortBuffer allocate(int capacity) {
        return new UnsignedShortBuffer(ShortBuffer.allocate(capacity));
    }

    /**
     * Exposes a raw short-array as a buffer with unsigned-shorts.
     *
     * @param array the short-array
     * @return a new instance of {@link UnsignedShortBuffer} that reuses {@code array} internally.
     */
    public static UnsignedShortBuffer wrapRaw(short[] array) {
        return new UnsignedShortBuffer(ShortBuffer.wrap(array));
    }

    /**
     * Exposes a raw {@link UnsignedShortBuffer} as a buffer with unsigned-shorts.
     *
     * @param bufferRaw the raw-buffer
     * @return a new instance of {@link UnsignedShortBuffer} that reuses {@code bufferRaw}
     *     internally.
     */
    public static UnsignedShortBuffer wrapRaw(ShortBuffer bufferRaw) {
        return new UnsignedShortBuffer(bufferRaw);
    }

    /**
     * Creates given a delegate.
     *
     * @param delegate the delegate
     */
    private UnsignedShortBuffer(ShortBuffer delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    /**
     * Gets an unsigned-short (represented as a short) at the current buffer position.
     *
     * <p>No further conversion occurs, so this method is more efficient than {@link
     * #getUnsigned()}.
     *
     * @return unsigned-short (represented by a short)
     */
    public short getRaw() {
        return delegate.get();
    }

    /**
     * Gets an unsigned-short (represented as a short) at a particular buffer position.
     *
     * <p>No further conversion occurs, so this method is more efficient than {@link
     * #getUnsigned(int)}.
     *
     * @param index the buffer position
     * @return unsigned-short (represented by a short)
     */
    public short getRaw(int index) {
        return delegate.get(index);
    }

    @Override
    public int getUnsigned() {
        return PrimitiveConverter.unsignedShortToInt(getRaw());
    }

    @Override
    public int getUnsigned(int index) {
        return PrimitiveConverter.unsignedShortToInt(getRaw(index));
    }

    /**
     * Puts an unsigned-short (represented as a short) at the current buffer position.
     *
     * <p>No further conversion occurs, so this method is more efficient than {@link
     * #putUnsigned(int)}.
     *
     * @param value unsigned-short (represented by a short)
     */
    public void putRaw(short value) {
        delegate.put(value);
    }

    /**
     * Puts an unsigned-short (represented as a short) a particular buffer position.
     *
     * <p>No further conversion occurs, so this method is more efficient than {@link
     * #putUnsigned(int,int)}.
     *
     * @param index the buffer position
     * @param value the unsigned-short (represented by an short)
     */
    public void putRaw(int index, short value) {
        delegate.put(index, value);
    }

    @Override
    public void putUnsigned(int value) {
        putRaw((short) value);
    }

    /**
     * Puts a long at the current buffer position, clipping to ensure the value is within the range
     * {@code (0,255)}.
     *
     * @param value the float
     */
    public void putUnsignedClipped(int value) {
        if (value > UnsignedShortVoxelType.MAX_VALUE_INT) {
            putUnsigned(UnsignedShortVoxelType.MAX_VALUE_INT);
        } else if (value < 0) {
            putUnsigned(0);
        } else {
            putUnsigned(value);
        }
    }

    @Override
    public void putUnsigned(int index, int value) {
        putRaw(index, (short) value);
    }

    @Override
    public void putLong(long value) {
        putRaw((short) value);
    }

    /**
     * Puts a long at the current buffer position, clipping to ensure the value is within the range
     * {@code (0,255)}.
     *
     * @param value the float
     */
    public void putLongClipped(long value) {
        if (value > UnsignedShortVoxelType.MAX_VALUE_INT) {
            putUnsigned(UnsignedShortVoxelType.MAX_VALUE_INT);
        } else if (value < 0) {
            putUnsigned(0);
        } else {
            putLong(value);
        }
    }

    @Override
    public void putFloat(float value) {
        putRaw((short) value);
    }

    /**
     * Puts a float at the current buffer position, clipping to ensure the value is within the range
     * {@code (0,255)}.
     *
     * @param value the float
     */
    public void putFloatClipped(float value) {
        if (value > UnsignedShortVoxelType.MAX_VALUE_INT) {
            putUnsigned(UnsignedShortVoxelType.MAX_VALUE_INT);
        } else if (value < 0) {
            putUnsigned(0);
        } else {
            putFloat(value);
        }
    }

    @Override
    public void putFloat(int index, float value) {
        putRaw(index, (short) value);
    }

    @Override
    public void putDouble(double value) {
        putRaw((short) value);
    }

    @Override
    public void putDouble(int index, double value) {
        putRaw(index, (short) value);
    }

    /**
     * Relative put-method from an unsigned short buffer, represented by a {@link ShortBuffer}.
     *
     * <p>This is identical to {@link ShortBuffer#put(ShortBuffer)}, with no return value.
     *
     * @param source source of bytes to put
     */
    public void put(ShortBuffer source) {
        delegate.put(source);
    }

    /**
     * Relative put-method from an unsigned short buffer, represented by a {@link
     * UnsignedShortBuffer}.
     *
     * <p>This is identical to {@link ShortBuffer#put(ShortBuffer)}, with no return value.
     *
     * @param source source of bytes to put
     */
    public void put(UnsignedShortBuffer source) {
        delegate.put(source.getDelegate());
    }

    /**
     * The array of the buffer ala {@link ShortBuffer#array}.
     *
     * <p>Unlike {@link ShortBuffer#array} an array will always be returned, copying it into a newly
     * created array, if it cannot be directly accessed.
     *
     * @return the array
     */
    public final short[] array() {
        Preconditions.checkArgument(delegate.hasArray());
        return delegate.array();
    }
}
