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

package org.anchoranalysis.io.bioformats.copyconvert.toint;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import loci.common.DataTools;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFactory;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;

@RequiredArgsConstructor
public class UnsignedIntFromUnsignedInt extends ToInt {

    private static final int BYTES_PER_PIXEL = 4;

    // START REQUIRED ARGUMENTS
    private final boolean littleEndian;
    // END REQUIRED ARGUMENTS

    private int sizeXY;
    private int sizeBytes;

    @Override
    protected void setupBefore(Dimensions dimensions, int numberChannelsPerArray) {
        sizeXY = dimensions.x() * dimensions.y();
        sizeBytes = sizeXY * BYTES_PER_PIXEL;
    }

    @Override
    protected VoxelBuffer<UnsignedIntBuffer> convertSliceOfSingleChannel(
            ByteBuffer source, int channelIndexRelative) {
        Preconditions.checkArgument(
                channelIndexRelative == 0, "interleaving not supported for int data");

        byte[] sourceArray = source.array();

        VoxelBuffer<UnsignedIntBuffer> voxels = VoxelBufferFactory.allocateUnsignedInt(sizeXY);
        UnsignedIntBuffer out = voxels.buffer();

        int indexOut = 0;
        for (int indexIn = 0; indexIn < sizeBytes; indexIn += BYTES_PER_PIXEL) {
            out.put(
                    indexOut++,
                    DataTools.bytesToInt(sourceArray, indexIn, BYTES_PER_PIXEL, littleEndian));
        }

        return voxels;
    }
}
