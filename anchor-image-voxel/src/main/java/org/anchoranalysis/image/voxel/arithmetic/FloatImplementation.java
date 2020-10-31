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

import java.nio.FloatBuffer;
import java.util.function.IntFunction;
import org.anchoranalysis.spatial.Extent;

class FloatImplementation extends Base<FloatBuffer> {

    public FloatImplementation(Extent extent, IntFunction<FloatBuffer> bufferForSlice) {
        super(extent, bufferForSlice);
    }

    @Override
    protected void multiplyBuffer(FloatBuffer buffer, double factor) {
        FloatHelper.calculateForEveryVoxel(buffer, value -> BinaryOperationHelper.multiplyByFloat(value, factor));
    }

    @Override
    protected void subtractFromBuffer(FloatBuffer buffer, int valueToSubtractFrom) {
        FloatHelper.calculateForEveryVoxel(buffer, value -> valueToSubtractFrom - value);
    }

    @Override
    protected void addToBufferIndex(FloatBuffer buffer, int index, int valueToBeAdded) {
        FloatHelper.calculateForEveryVoxel(buffer, value -> value + valueToBeAdded);
    }

    @Override
    protected void multiplyByBufferIndex(FloatBuffer buffer, int index, double factor) {
        FloatHelper.calculateForIndex(buffer, index, value -> BinaryOperationHelper.multiplyByFloat(value, factor));
    }

    @Override
    protected void divideByBuffer(FloatBuffer buffer, int divisor) {
        FloatHelper.calculateForEveryVoxel(buffer, value -> value / divisor);
    }
}
