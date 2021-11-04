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
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.extracter.OrientationChange;
import org.anchoranalysis.io.bioformats.DestinationChannelForIndex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Converts a subset of raw voxel bytes in a buffer to a possibly a buffer with different data-type.
 */
@RequiredArgsConstructor
public abstract class ConvertTo<T> {

    private static Log log = LogFactory.getLog(ConvertTo.class);

    // START REQUIRED ARGUMENTS
    /** How to convert a {@link VoxelsUntyped} to the specific destination-type. */
    private final Function<VoxelsUntyped, Voxels<T>> functionCast;
    // END REQUIRED ARGUMENTS

    /**
     * Copies the channels in the source buffer into a particular {@link
     * DestinationChannelForIndex}.
     *
     * @param dimensions scene-dimension.
     * @param source the buffer we copy all channels from.
     * @param destination finds an appropriate destination channel for a particular
     *     relative-channel-index.
     * @param z the current slice we are working on.
     * @param numberChannelsPerArray the total number of channels found in any one instance of
     *     {@code source} (more than 1 if interleaving is present).
     * @param orientationCorrection any correction of orientation to be applied as bytes are
     *     converted.
     * @throws IOException if any error occurs when copying channels.
     */
    public void copyAllChannels(
            Dimensions dimensions,
            ByteBuffer source,
            DestinationChannelForIndex destination,
            int z,
            int numberChannelsPerArray,
            OrientationChange orientationCorrection)
            throws IOException {

        log.debug(String.format("copy to %d start", z));

        setupBefore(dimensions, numberChannelsPerArray);

        for (int channelIndexRelative = 0;
                channelIndexRelative < numberChannelsPerArray;
                channelIndexRelative++) {

            VoxelBuffer<T> converted =
                    convertSliceOfSingleChannel(
                            source, channelIndexRelative, orientationCorrection);
            placeSliceInDestination(converted, functionCast, destination, z, channelIndexRelative);
        }

        log.debug(String.format("copy to byte %d end", z));
    }

    /**
     * Always called before any batch of calls to {@link #convertSliceOfSingleChannel}.
     *
     * @param dimensions the final dimensions of the image.
     * @param numberChannelsPerArray the number of channels that are found in the byte-array that
     *     will be passed to {@link #convertSliceOfSingleChannel}.
     */
    protected abstract void setupBefore(Dimensions dimensions, int numberChannelsPerArray);

    /**
     * Converts a slice of single-channel into a newly created {@link VoxelBuffer}.
     *
     * @param source source buffer containing the bytes we copy from.
     * @param channelIndexRelative 0 if the buffer is non interleaved, or otherwise the index of the
     *     channel among the interleaved channels.
     * @param orientationCorrection any correction of orientation to be applied as bytes are
     *     converted.
     */
    protected abstract VoxelBuffer<T> convertSliceOfSingleChannel(
            ByteBuffer source, int channelIndexRelative, OrientationChange orientationCorrection)
            throws IOException;

    private static <S> void placeSliceInDestination(
            VoxelBuffer<S> voxelBuffer,
            Function<VoxelsUntyped, Voxels<S>> functionCast,
            DestinationChannelForIndex destination,
            int z,
            int channelIndexRelative) {
        Voxels<S> voxels = functionCast.apply(destination.get(channelIndexRelative).voxels());
        voxels.slices().replaceSlice(z, voxelBuffer);
    }
}
