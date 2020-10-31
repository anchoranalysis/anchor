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
package org.anchoranalysis.image.voxel.arithmetic;

import java.util.function.IntFunction;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.spatial.Extent;

class UnsignedByteImplementation extends Base<UnsignedByteBuffer> {

    private static final int MAXIMUM_VALUE = UnsignedByteVoxelType.MAX_VALUE_INT;
    
    public UnsignedByteImplementation(Extent extent, IntFunction<UnsignedByteBuffer> bufferForSlice) {
        super(extent, bufferForSlice);
    }

    @Override
    protected void multiplyBuffer(UnsignedByteBuffer buffer, double factor) {
        UnsignedBufferAsIntHelper.calculateForEveryVoxel(buffer, MAXIMUM_VALUE, value -> BinaryOperationHelper.multiplyByInt(value,factor));
    }

    @Override
    protected void divideByBuffer(UnsignedByteBuffer buffer, int divisor) {
        UnsignedBufferAsIntHelper.calculateForEveryVoxel(buffer, MAXIMUM_VALUE, value -> value / divisor );
    }
    
    @Override
    protected void subtractFromBuffer(UnsignedByteBuffer buffer, int valueToSubtractFrom) {
        UnsignedBufferAsIntHelper.calculateForEveryVoxel(buffer, MAXIMUM_VALUE, value -> valueToSubtractFrom - value );
    }

    @Override
    protected void multiplyByBufferIndex(UnsignedByteBuffer buffer, int index, double factor) {
        UnsignedBufferAsIntHelper.calculateForIndex(buffer, index, MAXIMUM_VALUE, value -> BinaryOperationHelper.multiplyByInt(value,factor) );
    }

    @Override
    protected void addToBufferIndex(UnsignedByteBuffer buffer, int index, int valueToBeAdded) {
        UnsignedBufferAsIntHelper.calculateForIndex(buffer, index, MAXIMUM_VALUE, value -> value + valueToBeAdded);
    }
}
