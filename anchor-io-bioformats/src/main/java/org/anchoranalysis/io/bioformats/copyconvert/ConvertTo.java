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

package org.anchoranalysis.io.bioformats.copyconvert;

import java.io.IOException;
import java.nio.Buffer;
import java.util.function.Function;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.io.bioformats.DestChnlForIndex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Converts a subset of bytes from a byte[] to one or more destination Channels */
public abstract class ConvertTo<T extends Buffer> {

    private static Log log = LogFactory.getLog(ConvertTo.class);

    private Function<VoxelsWrapper, Voxels<T>> funcCastWrapper;

    /**
     * Default constructor
     *
     * @param funcCastWrapper how to convert a {@link VoxelsWrapper} to the specific destination-type
     */
    public ConvertTo(Function<VoxelsWrapper, Voxels<T>> funcCastWrapper) {
        super();
        this.funcCastWrapper = funcCastWrapper;
    }

    /**
     * Copies the channels in the source buffer into a particular Chnl
     *
     * @param sd scene-dimension
     * @param src the buffer we copy all channels from
     * @param funcDestChnl finds an appropriate destination channel for a particular
     *     relative-channel-index
     * @param z the current slice we are working on
     * @param numChnlsPerByteArray the total number of channels found in any one instance of src
     * @throws IOException
     */
    public void copyAllChnls(
            ImageDimensions sd, byte[] src, DestChnlForIndex dest, int z, int numChnlsPerByteArray)
            throws IOException {

        log.debug(String.format("copy to byte %d start", z));

        setupBefore(sd, numChnlsPerByteArray);

        for (int channelRelative = 0; channelRelative < numChnlsPerByteArray; channelRelative++) {

            VoxelBuffer<T> converted = convertSingleChnl(src, channelRelative);
            copyBytesIntoDestChnl(converted, funcCastWrapper, dest, z, channelRelative);
        }

        log.debug(String.format("copy to byte %d end", z));
    }

    /**
     * Always called before any batch of calls to convertSingleChnl
     *
     * @param sd dimension
     * @param numChnlsPerByteArray the number of channels that are found in the byte-array that will
     *     be passed to convertSingleChnl
     */
    protected abstract void setupBefore(ImageDimensions sd, int numChnlsPerByteArray);

    /**
     * Converts a single-channel only
     *
     * @param src source buffer containing the bytes we copy from
     * @param cRel 0 if the buffer contains only 1 channel per byte array, or otherwise the index of
     *     the channel
     */
    protected abstract VoxelBuffer<T> convertSingleChnl(byte[] src, int cRel) throws IOException;

    public static <S extends Buffer> void copyBytesIntoDestChnl(
            VoxelBuffer<S> voxelBuffer,
            Function<VoxelsWrapper, Voxels<S>> funcCastWrapper,
            DestChnlForIndex dest,
            int z,
            int cRel) {
        Voxels<S> voxels = funcCastWrapper.apply(dest.get(cRel).voxels());
        voxels.getPlaneAccess().setPixelsForPlane(z, voxelBuffer);
    }
}
