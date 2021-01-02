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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.Buffer;
import lombok.EqualsAndHashCode;

/**
 * A {@link UnsignedBuffer} that exposes its internal types as {@code int}.
 *
 * <p>This means that an {@code int} is returned by {@code getUnsigned} and accepted by {@code
 * putUnsigned} and related functions.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public abstract class UnsignedBufferAsInt extends UnsignedBuffer {

    protected UnsignedBufferAsInt(Buffer delegate) {
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
    
    /***
     * Print a description and the the first {link #MAX_NUMBER_ROWS_COLUMNS_IN_TO_STRING} rows and columns as values.
     */
    @Override
    public String toString() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream stream = new PrintStream(output)) {
            stream.printf("The buffer has capacity: %d%n", capacity());

            for( int offset=0; offset<capacity(); offset++) {
                stream.printf("%03d ", getUnsigned(offset));        }    
            }
            
        return output.toString();
    }
}
