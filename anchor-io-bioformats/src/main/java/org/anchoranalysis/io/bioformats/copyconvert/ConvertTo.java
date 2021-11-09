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
import java.nio.ByteOrder;
import java.util.function.Function;
import java.util.function.IntFunction;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.io.bioformats.DestinationChannelForIndex;
import org.anchoranalysis.spatial.box.Extent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Converts a subset of raw voxel bytes in a buffer to a possibly a buffer with different data-type.
 *
 * @param <T> destination buffer-type
 */
@RequiredArgsConstructor
public abstract class ConvertTo<T> {

    private static Log log = LogFactory.getLog(ConvertTo.class);

    // START REQUIRED ARGUMENTS
    /** How to convert a {@link VoxelsUntyped} to the specific destination-type. */
    private final Function<VoxelsUntyped, Voxels<T>> functionCast;

    /** A function that creates a new buffer of type {@code T} of a given size. */
    private final IntFunction<T> allocateBuffer;

    /** A function to wrap a buffer of type {@code T} into a {@link VoxelBuffer}. */
    private final Function<T, VoxelBuffer<T>> wrapBuffer;
    // END REQUIRED ARGUMENTS

    protected int sizeXY;

    protected Extent extent;

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
    protected void setupBefore(Dimensions dimensions, int numberChannelsPerArray) {
        this.extent = dimensions.extent();
        this.sizeXY = dimensions.areaXY();
    }

    /**
     * Converts a slice of single-channel into a newly created {@link VoxelBuffer}.
     *
     * @param source source buffer containing the bytes we copy from.
     * @param channelIndexRelative 0 if the buffer is non interleaved, or otherwise the index of the
     *     channel among the interleaved channels.
     * @param orientationCorrection any correction of orientation to be applied as bytes are
     *     converted.
     * @return the converted buffer.
     * @throw IOException when operation is unsupported, given particular parameterization.
     */
    protected VoxelBuffer<T> convertSliceOfSingleChannel(
            ByteBuffer source, int channelIndexRelative, OrientationChange orientationCorrection)
            throws IOException {
        if (!supportsInterleaving() && channelIndexRelative != 0) {
            throw new IOException("interleaving not supported");
        }
        boolean littleEndian = source.order() == ByteOrder.LITTLE_ENDIAN;
        return wrapBuffer.apply(
                convert(source, channelIndexRelative, orientationCorrection, littleEndian));
    }

    protected T convert(
            ByteBuffer source,
            int channelIndexRelative,
            OrientationChange orientationCorrection,
            boolean littleEndian)
            throws IOException {

        T destination = allocateBuffer.apply(sizeXY);

        if (orientationCorrection == OrientationChange.KEEP_UNCHANGED) {
            copyKeepOrientation(source, littleEndian, channelIndexRelative, destination);
        } else {
            copyChangeOrientation(
                    source, littleEndian, channelIndexRelative, destination, orientationCorrection);
        }

        return destination;
    }

    /**
     * Copy the bytes, without changing orientation.
     *
     * <p>This is kept separate to {@link #copyChangeOrientation} as it can be done slightly more
     * efficiently.
     */
    protected abstract void copyKeepOrientation(
            ByteBuffer source, boolean littleEndian, int channelIndexRelative, T destination)
            throws IOException;

    /** Copy the bytes, changing orientation. */
    protected abstract void copyChangeOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            T destination,
            OrientationChange orientationCorrection)
            throws IOException;

    /**
     * Whether interleaving of z-slices is supported.
     *
     * @return true if interleaving is supported, false otherwise.
     */
    protected abstract boolean supportsInterleaving();

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
