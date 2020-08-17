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

import java.nio.ShortBuffer;
import loci.common.DataTools;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferShort;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class ShortFromUnsignedShort extends ConvertToShort {

    private int bytesPerPixel = 2;
    private int sizeXY;
    private int sizeBytes;

    private boolean littleEndian;

    public ShortFromUnsignedShort(boolean littleEndian) {
        super();
        this.littleEndian = littleEndian;
    }

    @Override
    protected void setupBefore(ImageDimensions dimensions, int numChnlsPerByteArray) {
        sizeXY = dimensions.x() * dimensions.y();
        sizeBytes = sizeXY * bytesPerPixel;
    }

    @Override
    protected VoxelBuffer<ShortBuffer> convertSingleChnl(byte[] src, int channelRelative) {

        short[] crntChnlBytes = new short[sizeXY];

        int indOut = 0;
        for (int indIn = 0; indIn < sizeBytes; indIn += bytesPerPixel) {
            int s = DataTools.bytesToShort(src, indIn, bytesPerPixel, littleEndian);

            // Make positive
            if (s < 0) {
                s += (VoxelDataTypeUnsignedShort.MAX_VALUE_INT + 1);
            }

            if (s > VoxelDataTypeUnsignedShort.MAX_VALUE_INT) {
                s = VoxelDataTypeUnsignedShort.MAX_VALUE_INT;
            }
            if (s < 0) {
                s = 0;
            }

            crntChnlBytes[indOut++] = (short) s;
        }

        return VoxelBufferShort.wrap(crntChnlBytes);
    }
}
