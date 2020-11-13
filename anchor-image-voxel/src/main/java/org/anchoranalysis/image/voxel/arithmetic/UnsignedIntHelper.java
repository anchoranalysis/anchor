/*-
 * #%L
 * anchor-image-voxel
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
package org.anchoranalysis.image.voxel.arithmetic;

import java.util.function.LongUnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class UnsignedIntHelper {

    private static final long MAXIMUM_VALUE = UnsignedIntVoxelType.MAX_VALUE;

    public static void calculateForEveryVoxel(
            UnsignedIntBuffer buffer, LongUnaryOperator operator) {
        while (buffer.hasRemaining()) {
            putClamped(buffer, operator.applyAsLong(buffer.getUnsigned()));
        }
    }

    public static void calculateForIndex(
            UnsignedIntBuffer buffer, int index, LongUnaryOperator operator) {
        putClampedAtIndex(buffer, operator.applyAsLong(buffer.getUnsigned(index)), index);
    }

    /** Put a (clamped via a long) double-value at previous buffer position. */
    private static void putClamped(UnsignedIntBuffer buffer, long valueToAssign) {
        putClampedAtIndex(buffer, valueToAssign, buffer.position() - 1);
    }

    /** Put a (clamped) double-value at previous buffer position */
    private static void putClampedAtIndex(UnsignedIntBuffer buffer, long valueToAssign, int index) {
        buffer.putUnsigned(index, clamp(valueToAssign));
    }

    private static long clamp(long value) {
        if (value < 0) {
            return 0;
        }
        if (value > MAXIMUM_VALUE) {
            return MAXIMUM_VALUE;
        }
        return value;
    }
}
