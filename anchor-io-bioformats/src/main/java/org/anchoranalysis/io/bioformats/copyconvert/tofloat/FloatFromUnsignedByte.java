/*-
 * #%L
 * anchor-io-bioformats
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

package org.anchoranalysis.io.bioformats.copyconvert.tofloat;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.buffer.primitive.PrimitiveConverter;

public class FloatFromUnsignedByte extends ToFloat {

    @Override
    protected float[] convertIntegerBytesToFloatArray(
            Dimensions dimensions, ByteBuffer source, int offsetInSource) {

        float[] out = new float[dimensions.x() * dimensions.y()];

        int indexOut = 0;
        for (int y = 0; y < dimensions.y(); y++) {
            for (int x = 0; x < dimensions.x(); x++) {
                out[indexOut++] =
                        PrimitiveConverter.unsignedByteToInt(source.get(offsetInSource++));
            }
        }
        return out;
    }

    @Override
    protected int bytesPerPixel() {
        return 1;
    }
}
