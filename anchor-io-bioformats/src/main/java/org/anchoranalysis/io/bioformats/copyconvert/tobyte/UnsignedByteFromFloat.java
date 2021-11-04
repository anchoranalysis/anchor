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

package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import loci.common.DataTools;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Converts data of type <i>float</i> to <i>unsigned byte</i>.
 *
 * <p>Only values in the range {@code 0 <= value <= 255} are preserved. Any values outside this
 * range are clamped to {@code 0} or {@code 255}.
 *
 * <p>Any decimal component is dropped.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class UnsignedByteFromFloat extends ToUnsignedByte {

    @Override
    protected UnsignedByteBuffer convert(ByteBuffer source, int channelIndexRelative) {
        Preconditions.checkArgument(channelIndexRelative == 0, "interleaving not supported");

        UnsignedByteBuffer destination = allocateBuffer();

        byte[] sourceArray = source.array();

        for (int indexIn = 0; indexIn < sizeBytes; indexIn += bytesPerPixel) {
            float value =
                    DataTools.bytesToFloat(
                            sourceArray, indexIn, source.order() == ByteOrder.LITTLE_ENDIAN);

            if (value > 255) {
                value = 255;
            }
            if (value < 0) {
                value = 0;
            }
            destination.putFloat(value);
        }

        return destination;
    }

    @Override
    protected int calculateBytesPerPixel(int numberChannelsPerArray) {
        return 4;
    }
}
