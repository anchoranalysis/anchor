/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.stack.input;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.ImageMetadata;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * An image file that has been opened for reading containing one or more series of image-{@link
 * Stack}s.
 *
 * @author Owen Feehan
 */
public interface OpenedImageFile extends AutoCloseable {

    /**
     * Opens a time-series, and checks that it is a particular type.
     *
     * <p>If it's not the correct type, an error is thrown.
     *
     * @param seriesIndex the index of the series to open.
     * @param progress tracks progress when opening.
     * @param channelDataType the expected data-type of the channels.
     * @return a newly created {@link TimeSequence} of images for the series.
     * @throws ImageIOException
     */
    default TimeSequence openCheckType(
            int seriesIndex, Progress progress, VoxelDataType channelDataType)
            throws ImageIOException {

        TimeSequence sequence = open(seriesIndex, progress);

        if (!sequence.allChannelsHaveType(channelDataType)) {
            throw new ImageIOException(
                    String.format("File does not have dataType %s", channelDataType));
        }

        return sequence;
    }

    /**
     * Open the first series when we don't have a specific-type.
     *
     * @return a time-sequence of images.
     */
    default TimeSequence open() throws ImageIOException {
        return open(0);
    }

    /**
     * Open when we don't have a specific-type.
     *
     * @param seriesIndex the index of the series of the open, zero-indexed.
     * @return a time-sequence of images.
     */
    default TimeSequence open(int seriesIndex) throws ImageIOException {
        return open(seriesIndex, ProgressIgnore.get());
    }

    /**
     * Like {@link #open(int)} but additionally tracks progress of the opening.
     *
     * @param seriesIndex the index of the series of the open, zero-indexed.
     * @param progress tracks progress.
     * @return a time-sequence of images.
     */
    TimeSequence open(int seriesIndex, Progress progress) throws ImageIOException;

    /** The number of series (distinct sets of images) in the image-file. */
    int numberSeries();

    /**
     * The names of each channel, if they are known.
     *
     * @return a list of the names, which should correspond (and have the same number of items) as
     *     {@link #numberChannels()}.
     */
    Optional<List<String>> channelNames() throws ImageIOException;

    /** The number of channels in the image-file e.g. 1 for grayscale, 3 for RGB. */
    int numberChannels() throws ImageIOException;

    /** The number of frames in the image-file i.e. distinct images for a particular time-point. */
    int numberFrames() throws ImageIOException;

    /** The bit-depth of the image voxels e.g. 8 for 8-bit, 16 for 16-bit etc. */
    int bitDepth() throws ImageIOException;

    /** The timestamps and file-attributes associated with the image. */
    ImageTimestampsAttributes timestamps() throws ImageIOException;

    /** Whether the image-file has RGB encoded voxels. */
    boolean isRGB() throws ImageIOException;

    /** Closes the opened image-file, removing any intermediate data-structures. */
    void close() throws ImageIOException;

    /**
     * The {@link Dimensions} associated with a particular series.
     *
     * @param seriesIndex the index of the series.
     * @return the corresponding dimensions.
     * @throws ImageIOException if any filesystem-related input-output failure occurs.
     */
    Dimensions dimensionsForSeries(int seriesIndex) throws ImageIOException;

    /**
     * Extracts metadata about the image.
     *
     * <p>This may be called without later retrieving a channel from the image, so it is desirable
     * that it is as computationally efficient as possible, for this use case.
     *
     * @param seriesIndex the index of the series.
     * @return the associated image metadata.
     * @throws ImageIOException if any filesystem-related input-output failure occurs.
     */
    default ImageMetadata metadata(int seriesIndex) throws ImageIOException {
        ImageTimestampsAttributes timestamps = timestamps();
        return new ImageMetadata(
                dimensionsForSeries(seriesIndex),
                numberChannels(),
                numberFrames(),
                isRGB(),
                bitDepth(),
                timestamps.getAttributes(),
                timestamps.getAcqusitionTime());
    }
}
