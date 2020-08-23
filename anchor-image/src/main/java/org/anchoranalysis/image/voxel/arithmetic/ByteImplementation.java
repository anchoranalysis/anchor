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

import java.nio.ByteBuffer;
import java.util.function.IntFunction;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;

class ByteImplementation extends Base<ByteBuffer> {

    public ByteImplementation(Extent extent, IntFunction<ByteBuffer> bufferForSlice) {
        super(extent, bufferForSlice);
    }

    @Override
    protected void multiplyBuffer(ByteBuffer buffer, double factor) {
        while (buffer.hasRemaining()) {
            byte mult = scaleClippedByte(factor, buffer.get());
            buffer.put(buffer.position() - 1, mult);
        }
    }

    @Override
    protected void subtractFromBuffer(ByteBuffer buffer, int valueToSubtractFrom) {
        while (buffer.hasRemaining()) {
            byte subtracted = subtractFromClippedByte(valueToSubtractFrom, buffer.get());
            buffer.put(buffer.position() - 1, subtracted);
        }
    }

    @Override
    protected void multiplyByBufferIndex(ByteBuffer buffer, int index, double factor) {
        byte mult = scaleClippedByte(factor, buffer.get(index));
        buffer.put(index, mult);
    }

    @Override
    protected void addToBufferIndex(ByteBuffer buffer, int index, int valueToBeAdded) {
        byte added = addClippedByte(valueToBeAdded, buffer.get(index));
        buffer.put(index, added);
    }

    private static byte addClippedByte(int value, byte pixelValue) {
        return (byte) addClipped(value, ByteConverter.unsignedByteToInt(pixelValue));
    }

    private static byte subtractFromClippedByte(int valueToSubtractFrom, byte pixelValue) {
        return (byte) (valueToSubtractFrom - ByteConverter.unsignedByteToInt(pixelValue));
    }

    private static byte scaleClippedByte(double factor, byte pixelValue) {
        return (byte) scaleClipped(factor, ByteConverter.unsignedByteToInt(pixelValue));
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
