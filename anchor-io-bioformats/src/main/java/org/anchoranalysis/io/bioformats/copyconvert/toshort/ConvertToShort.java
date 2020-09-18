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

package org.anchoranalysis.io.bioformats.copyconvert.toshort;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import loci.common.DataTools;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferWrap;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertTo;

public abstract class ConvertToShort extends ConvertTo<UnsignedShortBuffer> {

    private static final int BYTES_PER_PIXEL = 2;

    // START REQUIRED ARGUMENTS
    private final boolean littleEndian;
    // END REQUIRED ARGUMENTS

    private int sizeXY;
    private int sizeBytes;

    public ConvertToShort(boolean littleEndian) {
        super(VoxelsWrapper::asShort);
        this.littleEndian = littleEndian;
    }

    @Override
    protected void setupBefore(Dimensions dimensions, int numberChannelsPerArray) {
        sizeXY = dimensions.x() * dimensions.y();
        sizeBytes = sizeXY * BYTES_PER_PIXEL;
    }

    @Override
    protected VoxelBuffer<UnsignedShortBuffer> convertSingleChannel(
            ByteBuffer sourceBuffer, int channelIndexRelative) {
        Preconditions.checkArgument(
                channelIndexRelative == 0, "interleaving not supported for short data");

        byte[] buffer = sourceBuffer.array();

        UnsignedShortBuffer out = UnsignedShortBuffer.allocate(sizeXY);

        for (int indexIn = 0; indexIn < sizeBytes; indexIn += BYTES_PER_PIXEL) {
            out.putRaw(convertValue(valueFromBuffer(buffer, indexIn)));
        }

        return VoxelBufferWrap.unsignedShortBuffer(out);
    }

    protected abstract short convertValue(short value);

    private short valueFromBuffer(byte[] buffer, int index) {
        return DataTools.bytesToShort(buffer, index, BYTES_PER_PIXEL, littleEndian);
    }
}
