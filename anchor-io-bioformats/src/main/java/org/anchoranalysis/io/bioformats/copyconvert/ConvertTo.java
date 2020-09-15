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
import java.nio.ByteBuffer;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.io.bioformats.DestinationChannelForIndex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Converts a subset of bytes from a byte[] to one or more destination channels. */
@RequiredArgsConstructor
public abstract class ConvertTo<T> {

    private static Log log = LogFactory.getLog(ConvertTo.class);

    // START REQUIRED ARGUMENTS
    /** how to convert a {@link VoxelsWrapper} to the specific destination-type */
    private final Function<VoxelsWrapper, Voxels<T>> functionCast;
    // END REQUIRED ARGUMENTS

    /**
     * Copies the channels in the source buffer into a particular {@link
     * DestinationChannelForIndex}.
     *
     * @param dimensions scene-dimension
     * @param source the buffer we copy all channels from
     * @param destination finds an appropriate destination channel for a particular
     *     relative-channel-index
     * @param z the current slice we are working on
     * @param numberChannelsPerArray the total number of channels found in any one instance of
     *     {@code source} (more than 1 if interleaving is present)
     * @throws IOException
     */
    public void copyAllChannels(
            Dimensions dimensions,
            ByteBuffer source,
            DestinationChannelForIndex destination,
            int z,
            int numberChannelsPerArray)
            throws IOException {

        log.debug(String.format("copy to byte %d start", z));

        setupBefore(dimensions, numberChannelsPerArray);

        for (int channelIndexRelative = 0;
                channelIndexRelative < numberChannelsPerArray;
                channelIndexRelative++) {

            VoxelBuffer<T> converted = convertSingleChannel(source, channelIndexRelative);
            placeSliceInDestination(converted, functionCast, destination, z, channelIndexRelative);
        }

        log.debug(String.format("copy to byte %d end", z));
    }

    /**
     * Always called before any batch of calls to {@link #convertSingleChannel}.
     *
     * @param dimensions dimension
     * @param numberChannelsPerArray the number of channels that are found in the byte-array that
     *     will be passed to convertSingleChannel
     */
    protected abstract void setupBefore(Dimensions dimensions, int numberChannelsPerArray);

    /**
     * Converts a single-channel only.
     *
     * @param source source buffer containing the bytes we copy from
     * @param channelIndexRelative 0 if the buffer is non interleaved, or otherwise the index of the
     *     channel among the interleaved channels (which is only supported for 8-bit data)
     */
    protected abstract VoxelBuffer<T> convertSingleChannel(
            ByteBuffer source, int channelIndexRelative) throws IOException;

    public static <S> void placeSliceInDestination(
            VoxelBuffer<S> voxelBuffer,
            Function<VoxelsWrapper, Voxels<S>> functionCast,
            DestinationChannelForIndex destination,
            int z,
            int channelIndexRelative) {
        Voxels<S> voxels = functionCast.apply(destination.get(channelIndexRelative).voxels());
        voxels.slices().replaceSlice(z, voxelBuffer);
    }
}
