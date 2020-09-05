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

import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import java.util.function.IntFunction;
import org.anchoranalysis.image.extent.Extent;

class IntImplementation extends Base<UnsignedIntBuffer> {

    public IntImplementation(Extent extent, IntFunction<UnsignedIntBuffer> bufferForSlice) {
        super(extent, bufferForSlice);
    }

    @Override
    protected void multiplyBuffer(UnsignedIntBuffer buffer, double factor) {
        while (buffer.hasRemaining()) {
            buffer.putDouble(buffer.position() - 1, buffer.getUnsigned() * factor);
        }
    }

    @Override
    protected void subtractFromBuffer(UnsignedIntBuffer buffer, int valueToSubtractFrom) {
        while (buffer.hasRemaining()) {
            buffer.putUnsigned(buffer.position() - 1, valueToSubtractFrom - buffer.getUnsigned());
        }
    }

    // TODO when values are too small or too large
    @Override
    protected void addToBufferIndex(UnsignedIntBuffer buffer, int index, int valueToBeAdded) {
        buffer.putUnsigned(index, buffer.getUnsigned(index) + valueToBeAdded);
    }

    @Override
    protected void multiplyByBufferIndex(UnsignedIntBuffer buffer, int index, double factor) {
        throw new IllegalArgumentException("Currently unsupported method");
    }
}
