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
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.spatial.box.Extent;

class UnsignedIntImplementation extends Base<UnsignedIntBuffer> {

    public UnsignedIntImplementation(Extent extent, IntFunction<UnsignedIntBuffer> bufferForSlice) {
        super(extent, bufferForSlice);
    }

    @Override
    protected void multiplyBuffer(UnsignedIntBuffer buffer, double factor) {
        UnsignedIntHelper.calculateForEveryVoxel(
                buffer, value -> BinaryOperationHelper.multiplyByLong(value, factor));
    }

    @Override
    protected void subtractFromBuffer(UnsignedIntBuffer buffer, int valueToSubtractFrom) {
        UnsignedIntHelper.calculateForEveryVoxel(buffer, value -> valueToSubtractFrom - value);
    }

    @Override
    protected void addToBufferIndex(UnsignedIntBuffer buffer, int index, int valueToBeAdded) {
        UnsignedIntHelper.calculateForEveryVoxel(buffer, value -> value + valueToBeAdded);
    }

    @Override
    protected void multiplyByBufferIndex(UnsignedIntBuffer buffer, int index, double factor) {
        UnsignedIntHelper.calculateForIndex(
                buffer, index, value -> BinaryOperationHelper.multiplyByLong(value, factor));
    }

    @Override
    protected void divideByBuffer(UnsignedIntBuffer buffer, int divisor) {
        UnsignedIntHelper.calculateForEveryVoxel(buffer, value -> value / divisor);
    }
}
