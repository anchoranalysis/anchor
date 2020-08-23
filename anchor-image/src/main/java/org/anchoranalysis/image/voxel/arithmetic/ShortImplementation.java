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

import java.nio.ShortBuffer;
import java.util.function.IntFunction;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;

/**
 * TODO what to do when values are too small or too large?
 *
 * @author Owen Feehan
 */
class ShortImplementation extends Base<ShortBuffer> {

    public ShortImplementation(Extent extent, IntFunction<ShortBuffer> bufferForSlice) {
        super(extent, bufferForSlice);
    }

    @Override
    protected void multiplyBuffer(ShortBuffer buffer, double factor) {
        while (buffer.hasRemaining()) {
            short mult = multiplyBy(buffer.get(), factor);
            buffer.put(buffer.position() - 1, mult);
        }
    }

    @Override
    protected void multiplyByBufferIndex(ShortBuffer buffer, int index, double factor) {
        short mult = multiplyBy(buffer.get(index), factor);
        buffer.put(index, mult);
    }

    @Override
    protected void subtractFromBuffer(ShortBuffer buffer, int valueToSubtractFrom) {

        while (buffer.hasRemaining()) {
            // TODO does this also need to use byteconverter?
            int newVal = valueToSubtractFrom - buffer.get();
            buffer.put(buffer.position() - 1, (short) newVal);
        }
    }

    @Override
    protected void addToBufferIndex(ShortBuffer buffer, int index, int valueToBeAdded) {
        // TODO does this also need to use byteconverter?
        short shortVal = (short) (buffer.get(index) + valueToBeAdded);
        buffer.put(index, shortVal);
    }

    private static short multiplyBy(short value, double factor) {
        // TODO do we need to cast to an int first, or can it not just be done directly to a short?
        int mult = (int) (ByteConverter.unsignedShortToInt(value) * factor);
        return (short) mult;
    }
}
