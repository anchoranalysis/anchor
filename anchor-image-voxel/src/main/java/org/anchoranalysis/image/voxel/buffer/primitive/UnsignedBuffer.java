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

import java.nio.Buffer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Base class for buffers that represent an unsigned-type in the signed-equivalent-type NIO {@link
 * Buffer}.
 *
 * <p>This class exists as Java does not supported unsigned primitive types (apart from char), so it
 * is necessary to explicitly convert an unsigned primitive type to a larger primitive type for
 * arithmetic operations (e.g. unsigned bytes need to be converted to short or higher, unsigned
 * shorts to int or higher, unsigned int to long or higher).
 *
 * <p>Subclasses must define a sensible {@link Object#equals} and {@link Object#hashCode} that takes
 * account the delegate buffer.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@EqualsAndHashCode
public abstract class UnsignedBuffer {

    /** The delegate buffer */
    private Buffer delegate;

    /**
     * Puts a float at the current buffer position.
     *
     * <p>A conversion occurs from float to short.
     *
     * @param value the float.
     */
    public abstract void putFloat(float value);

    /**
     * Puts a float at a particular buffer position.
     *
     * <p>A conversion occurs from float to byte.
     *
     * @param index the buffer position
     * @param value the float.
     */
    public abstract void putFloat(int index, float value);

    /**
     * Puts a double at the current buffer position.
     *
     * @param value the double
     */
    public abstract void putDouble(double value);

    /**
     * Puts a double at a particular buffer position.
     *
     * @param index the buffer position
     * @param value the double
     */
    public abstract void putDouble(int index, double value);

    /**
     * Puts a long at the current buffer position.
     *
     * @param value the long.
     */
    public abstract void putLong(long value);

    /**
     * Whether there are elements between the current position and the limit {@link
     * Buffer#hasRemaining}.
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
     * @return the buffer.
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

    /** Clears the buffer ala {@link Buffer#clear}. */
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
