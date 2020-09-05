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

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import java.util.function.IntFunction;
import org.anchoranalysis.image.convert.PrimitiveConverter;
import org.anchoranalysis.image.extent.Extent;

class ByteImplementation extends Base<UnsignedByteBuffer> {

    public ByteImplementation(Extent extent, IntFunction<UnsignedByteBuffer> bufferForSlice) {
        super(extent, bufferForSlice);
    }

    @Override
    protected void multiplyBuffer(UnsignedByteBuffer buffer, double factor) {
        while (buffer.hasRemaining()) {
            buffer.putUnsignedByte(buffer.position() - 1, scaleClipped(factor, buffer.getUnsignedByte()));
        }
    }

    @Override
    protected void subtractFromBuffer(UnsignedByteBuffer buffer, int valueToSubtractFrom) {
        while (buffer.hasRemaining()) {
            buffer.putUnsignedByte(buffer.position() - 1, valueToSubtractFrom - buffer.getUnsignedByte());
        }
    }

    @Override
    protected void multiplyByBufferIndex(UnsignedByteBuffer buffer, int index, double factor) {
        buffer.putUnsignedByte(index, scaleClipped(factor, buffer.getUnsignedByte(index)));
    }

    @Override
    protected void addToBufferIndex(UnsignedByteBuffer buffer, int index, int valueToBeAdded) {
        buffer.putUnsignedByte(index, addClipped(valueToBeAdded, buffer.getUnsignedByte(index)));
    }

    private static int scaleClipped(double factor, int pixelValue) {
        int intVal = (int) Math.round(factor * pixelValue);
        if (intVal < 0) {
            return 0;
        }
        if (intVal > 255) {
            return 255;
        }
        return intVal;
    }

    private static int addClipped(int value, int pixelValue) {
        int intVal = pixelValue + value;
        if (intVal < 0) {
            return 0;
        }
        if (intVal > 255) {
            return 255;
        }
        return intVal;
    }
}
