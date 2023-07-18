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

    /** The size of the source and destination buffers. */
    protected Extent extent;

    /** The number of elements (bytes) to increment by, when iterating through the source buffer. */
    protected int sourceIncrement;

    /** The total number of elements (always bytes) in the source buffer. */
    protected int sourceSize;

    /**
     * The total number of elements (whatever the destination type is) in the destination buffer.
     */
    protected int destinationSize;

    /**
     * Copies the channels in the source buffer into a particular {@link
     * DestinationChannelForIndex}.
     *
     * @param dimensions scene-dimension.
     * @param source the buffer we copy all channels from.
     * @param destination finds an appropriate destination channel for a particular
     *     relative-channel-index.
     * @param z the current slice we are working on.
     * @param sourceImageEncoding how voxels are stored in the source-image.
     * @param orientationCorrection any correction of orientation to be applied as bytes are
     *     converted.
     * @throws IOException if any error occurs when copying channels.
     */
    public void copyAllChannels(
            Dimensions dimensions,
            ByteBuffer source,
            DestinationChannelForIndex destination,
            int z,
            ImageFileEncoding sourceImageEncoding,
            OrientationChange orientationCorrection)
            throws IOException {

        log.debug(String.format("copy to %d start", z));

        setupBefore(dimensions, sourceImageEncoding);

        for (int channelIndexRelative = 0;
                channelIndexRelative < sourceImageEncoding.getNumberChannelsPerArray();
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
     * @param sourceImageEncoding how voxels are stored in the source-image.
     * @throws IOException if a particular combination of parameters is unsupported.
     */
    protected void setupBefore(Dimensions dimensions, ImageFileEncoding sourceImageEncoding)
            throws IOException {
        this.extent = dimensions.extent();

        int areaXY = dimensions.areaXY();

        int numberDistinctChannelsSource = sourceImageEncoding.numberDistinctChannelsSource();

        if (numberDistinctChannelsSource <= 0) {
            throw new IOException(
                    String.format(
                            "The number of distinct channels in the source must be positive, but is %d",
                            numberDistinctChannelsSource));
        }

        // This is the number of bytes to increment by.
        this.sourceIncrement = bytesPerVoxel() * numberDistinctChannelsSource;

        // This is the number of bytes (which is identical to the number of lements).
        this.sourceSize = areaXY * sourceIncrement;

        // This is the number of elements, not the number of bytes.
        this.destinationSize = areaXY;
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
     * @throws IOException when operation is unsupported, given particular parameterization.
     */
    protected VoxelBuffer<T> convertSliceOfSingleChannel(
            ByteBuffer source, int channelIndexRelative, OrientationChange orientationCorrection)
            throws IOException {
        if (!supportsMultipleChannelsPerSourceBuffer() && channelIndexRelative != 0) {
            throw new IOException("interleaving not supported");
        }
        boolean littleEndian = source.order() == ByteOrder.LITTLE_ENDIAN;
        return wrapBuffer.apply(
                convert(source, channelIndexRelative, orientationCorrection, littleEndian));
    }

    /**
     * Converts a slice of single-channel into a newly created buffer of type T {@code T}.
     *
     * @param source source buffer containing the bytes we copy from.
     * @param channelIndexRelative 0 if the buffer is non interleaved, or otherwise the index of the
     *     channel among the interleaved channels.
     * @param orientationCorrection any correction of orientation to be applied as bytes are
     *     converted.
     * @param littleEndian true iff the bytes in {@code source} are in little-endian order.
     * @return the converted buffer.
     * @throws IOException when operation is unsupported, given particular parameterization.
     */
    protected T convert(
            ByteBuffer source,
            int channelIndexRelative,
            OrientationChange orientationCorrection,
            boolean littleEndian)
            throws IOException {

        T destination = allocateBuffer.apply(destinationSize);

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
     *
     * @param source the buffer we copy all channels from.
     * @param littleEndian true iff the bytes in {@code source} are in little-endian order.
     * @param channelIndexRelative 0 if the buffer is non interleaved, or otherwise the index of the
     *     channel among the interleaved channels.
     * @param destination finds an appropriate destination channel for a particular
     *     relative-channel-index.
     * @throws IOException when the operation fails due to read or write IO problems.
     */
    protected abstract void copyKeepOrientation(
            ByteBuffer source, boolean littleEndian, int channelIndexRelative, T destination)
            throws IOException;

    /**
     * Copy the bytes, changing orientation.
     *
     * @param source the buffer we copy all channels from.
     * @param littleEndian true iff the bytes in {@code source} are in little-endian order.
     * @param channelIndexRelative 0 if the buffer is non interleaved, or otherwise the index of the
     *     channel among the interleaved channels.
     * @param destination finds an appropriate destination channel for a particular
     *     relative-channel-index.
     * @param orientationCorrection any correction of orientation to be applied as bytes are
     *     converted.
     * @throws IOException when the operation fails due to read or write IO problems.
     */
    protected abstract void copyChangeOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            T destination,
            OrientationChange orientationCorrection)
            throws IOException;

    /**
     * Whether the source buffer can encode more than one channel?
     *
     * @return true if this possible (either with interleaving or with RGB-encoded voxels), false if
     *     a buffer will always describe one channel only.
     */
    protected abstract boolean supportsMultipleChannelsPerSourceBuffer();

    /**
     * The number bytes to describe each source-voxel.
     *
     * @return the number of bytes.
     */
    protected abstract int bytesPerVoxel();

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
