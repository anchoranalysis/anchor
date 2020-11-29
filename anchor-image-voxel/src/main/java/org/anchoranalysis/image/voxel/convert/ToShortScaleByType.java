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

package org.anchoranalysis.image.voxel.convert;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;

/**
 * Converts voxel buffers to a unsigned 16-bit buffer, scaling against the maximum value in each
 * data-type.
 *
 * <p>There is no clamping of values, but some values might become very small.
 *
 * @author Owen Feehan
 */
public final class ToShortScaleByType extends VoxelsConverter<UnsignedShortBuffer> {

    private static final int DIVIDE_BY_UNSIGNED_INT =
            (int) (UnsignedIntVoxelType.MAX_VALUE / UnsignedShortVoxelType.MAX_VALUE);

    private static final int MULTIPLY_BY_UNSIGNED_BYTE =
            (int) (UnsignedShortVoxelType.MAX_VALUE / UnsignedByteVoxelType.MAX_VALUE);

    @Override
    protected void convertUnsignedByte(UnsignedByteBuffer in, UnsignedShortBuffer out) {
        out.putUnsigned(in.getUnsigned() * MULTIPLY_BY_UNSIGNED_BYTE);
    }

    @Override
    protected void convertUnsignedShort(UnsignedShortBuffer in, UnsignedShortBuffer out) {
        out.putRaw(in.getRaw());
    }

    @Override
    protected void convertUnsignedInt(UnsignedIntBuffer in, UnsignedShortBuffer out) {
        out.putLongClamped(in.getUnsigned() / DIVIDE_BY_UNSIGNED_INT);
    }

    /**
     * Converts the current position in a {@link FloatBuffer} to the current position in a {@link
     * UnsignedShortBuffer}.
     *
     * <p>We pretend the maximum effective value of the float is the same as
     * UnsignedIntVoxelType.MAX_VALUE, and scale to this range fits the buffer.
     *
     * @param in the current position of this buffer gives the value to convert, and the position is
     *     incremented.
     * @param out the converted value is written to the current position of this buffer, and the
     *     position is incremented.
     */
    @Override
    protected void convertFloat(FloatBuffer in, UnsignedShortBuffer out) {
        out.putDouble(in.get() / DIVIDE_BY_UNSIGNED_INT);
    }
}
