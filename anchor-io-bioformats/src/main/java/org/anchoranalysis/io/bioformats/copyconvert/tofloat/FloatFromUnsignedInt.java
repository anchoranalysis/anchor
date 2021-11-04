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

import com.google.common.io.LittleEndianDataInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.spatial.box.Extent;

@RequiredArgsConstructor
public class FloatFromUnsignedInt extends ToFloat {

    @Override
    protected float[] convertIntegerBytesToFloatArray(Dimensions dimensions, ByteBuffer source)
            throws IOException {

        // Note that offsetInSource is not used, and this is perhaps incorrect.

        byte[] sourceArray = source.array();

        float[] out = new float[dimensions.volumeXY()];

        ByteArrayInputStream streamByte = new ByteArrayInputStream(sourceArray);

        if (source.order() == ByteOrder.LITTLE_ENDIAN) {
            return copyLittleEndian(streamByte, out, dimensions.extent());
        } else {
            return copyBigEndian(streamByte, out, dimensions.extent());
        }
    }

    @Override
    protected int bytesPerPixel() {
        return 4;
    }

    private static float[] copyLittleEndian(
            ByteArrayInputStream streamByte, float[] out, Extent extent) throws IOException {
        try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(streamByte)) {
            extent.iterateOverXYOffset(index -> out[index] = stream.readInt());
            return out;
        }
    }

    private static float[] copyBigEndian(
            ByteArrayInputStream streamByte, float[] out, Extent extent) throws IOException {
        try (DataInputStream stream = new DataInputStream(streamByte)) {
            extent.iterateOverXYOffset(index -> out[index] = stream.readInt());
            return out;
        }
    }
}
