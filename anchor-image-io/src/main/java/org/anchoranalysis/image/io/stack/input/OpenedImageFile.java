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
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.ImageLocation;
import org.anchoranalysis.image.core.stack.ImageMetadata;
import org.anchoranalysis.image.core.stack.ImagePyramidMetadata;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.time.TimeSeries;
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
     * @param channelDataType the expected data-type of the channels.
     * @param logger the logger.
     * @return a newly created {@link TimeSeries} of images for the series.
     * @throws ImageIOException if an error occurs reading the image during this operation.
     */
    default TimeSeries openCheckType(int seriesIndex, VoxelDataType channelDataType, Logger logger)
            throws ImageIOException {

        TimeSeries series = open(seriesIndex, logger);

        if (!series.allChannelsHaveType(channelDataType)) {
            throw new ImageIOException(
                    String.format("File does not have dataType %s", channelDataType));
        }

        return series;
    }

    /**
     * Open the first series when we don't have a specific-type.
     *
     * @param logger the logger.
     * @return a time-sequence of images.
     * @throws ImageIOException if an error occurs reading the image to determine this information.
     */
    default TimeSeries open(Logger logger) throws ImageIOException {
        return open(0, logger);
    }

    /**
     * Open when we don't have a specific-type.
     *
     * @param seriesIndex the index of the series of the open, zero-indexed.
     * @param logger the logger.
     * @return a time-sequence of images.
     * @throws ImageIOException if an error occurs reading the image during this operation.
     */
    TimeSeries open(int seriesIndex, Logger logger) throws ImageIOException;

    /**
     * The number of series (distinct sets of images) in the image-file.
     *
     * @return the number of series.
     */
    int numberSeries();

    /**
     * The names of each channel, if they are known.
     *
     * @param logger the logger.
     * @return a list of the names, which should correspond (and have the same number of items) as
     *     {@link #numberChannels(Logger)}.
     * @throws ImageIOException if an error occurs reading the image to determine this information.
     */
    Optional<List<String>> channelNames(Logger logger) throws ImageIOException;

    /**
     * The number of channels in the image-file e.g. 1 for grayscale, 3 for RGB.
     *
     * @param logger the logger.
     * @return the number of channels.
     * @throws ImageIOException if an error occurs reading the image to determine this information.
     */
    int numberChannels(Logger logger) throws ImageIOException;

    /**
     * The number of frames in the image-file i.e. distinct images for a particular time-point.
     *
     * @param logger the logger.
     * @return the number of frames.
     * @throws ImageIOException if an error occurs reading the image to determine this information.
     */
    int numberFrames(Logger logger) throws ImageIOException;

    /**
     * The bit-depth of the image voxels e.g. 8 for 8-bit, 16 for 16-bit etc.
     *
     * @param logger the logger.
     * @return the bit-depth.
     * @throws ImageIOException if an error occurs reading the image to determine this information.
     */
    int bitDepth(Logger logger) throws ImageIOException;

    /**
     * The timestamps and file-attributes associated with the image.
     *
     * @return timestamps and file-attributes, either newly-created or reused.
     * @throws ImageIOException if an error occurs reading the image to determine this information.
     */
    ImageTimestampsAttributes timestamps() throws ImageIOException;

    /**
     * The location associated with the image, if it is known.
     *
     * @return the image location, or {@link Optional#empty} if no location is known.
     * @throws ImageIOException if an error occurs reading the image to determine this information.
     */
    Optional<ImageLocation> location() throws ImageIOException;

    /**
     * Metadata to describe an image-pyramid, if it exists for this opened-image.
     *
     * @return the metadata, or {@link Optional#empty} if no pyramid exists.
     * @throws ImageIOException if an error occurs reading the image to determine this information.
     */
    Optional<ImagePyramidMetadata> pyramid() throws ImageIOException;

    /**
     * Whether the image-file has RGB encoded voxels.
     *
     * @param logger the logger.
     * @return true if the image has RGB or RGBA encoded voxels, false otherwise.
     * @throws ImageIOException if an error occurs reading the image to determine this information.
     */
    boolean isRGB(Logger logger) throws ImageIOException;

    /** Closes the opened image-file, removing any intermediate data-structures. */
    void close() throws ImageIOException;

    /**
     * The {@link Dimensions} associated with a particular series.
     *
     * @param seriesIndex the index of the series.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return the corresponding dimensions.
     * @throws ImageIOException if any filesystem-related input-output failure occurs.
     */
    Dimensions dimensionsForSeries(int seriesIndex, Logger logger) throws ImageIOException;

    /**
     * Extracts metadata about the image.
     *
     * <p>This may be called without later retrieving a channel from the image, so it is desirable
     * that it is as computationally efficient as possible, for this use case.
     *
     * @param seriesIndex the index of the series.
     * @param logger the logger.
     * @return the associated image metadata.
     * @throws ImageIOException if any filesystem-related input-output failure occurs.
     */
    default ImageMetadata metadata(int seriesIndex, Logger logger) throws ImageIOException {
        ImageTimestampsAttributes timestamps = timestamps();
        return new ImageMetadata(
                dimensionsForSeries(seriesIndex, logger),
                numberChannels(logger),
                numberFrames(logger),
                numberSeries(),
                isRGB(logger),
                bitDepth(logger),
                timestamps.getAttributes(),
                timestamps.getAcqusitionTime(),
                location(), // No currently implemented way to read GPS location
                pyramid());
    }
}
