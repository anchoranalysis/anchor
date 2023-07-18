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
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.core.dimensions.OrientationChange;

/**
 * Convert to a <i>float</i> buffer, given an <i>unsigned-int</i> source buffer.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class FloatFromUnsignedInt extends ToFloat {

    @Override
    protected int bytesPerVoxel() {
        return 4;
    }

    @Override
    protected void copyKeepOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            FloatBuffer destination)
            throws IOException {
        byte[] sourceArray = source.array();

        try (ByteArrayInputStream streamByte = new ByteArrayInputStream(sourceArray)) {

            DataInput stream = dataStream(streamByte, littleEndian);
            extent.iterateOverXYOffset(index -> destination.put(index, stream.readInt()));
        }
    }

    @Override
    protected void copyChangeOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            FloatBuffer destination,
            OrientationChange orientationCorrection)
            throws IOException {
        throw new IOException("Orientation-correction is not supported");
    }

    private static DataInput dataStream(ByteArrayInputStream streamByte, boolean littleEndian) {
        if (littleEndian) {
            return new LittleEndianDataInputStream(streamByte);
        } else {
            return new DataInputStream(streamByte);
        }
    }
}
