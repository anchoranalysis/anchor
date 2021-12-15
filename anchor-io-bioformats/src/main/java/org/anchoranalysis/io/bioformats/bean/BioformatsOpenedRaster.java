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

package org.anchoranalysis.io.bioformats.bean;

import static org.anchoranalysis.io.bioformats.bean.MultiplexDataTypes.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.meta.IMetadata;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.CalculateOrientationChange;
import org.anchoranalysis.image.io.stack.input.ImageTimestampsAttributes;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;
import org.anchoranalysis.image.io.stack.time.TimeSequence;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.io.bioformats.DimensionsCreator;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertTo;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertToFactory;
import org.anchoranalysis.io.bioformats.copyconvert.CopyConvert;
import org.anchoranalysis.io.bioformats.copyconvert.ImageFileShape;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An image-file that has been opened with the Bioformats library.
 *
 * @author Owen Feehan
 */
@Accessors(fluent = true)
class BioformatsOpenedRaster implements OpenedImageFile {

    private static final Log LOG = LogFactory.getLog(BioformatsOpenedRaster.class);

    private final IFormatReader reader;
    private final ReadOptions readOptions;

    private final IMetadata metadata;

    private final int sizeT;
    private final boolean rgb;
    private final int bitsPerPixel;

    /** Calculates any change needed in orientation. */
    private final CalculateOrientationChange calculateOrientation;

    /** Stores the result of {@code calculateOrientation}, and is null until this is calculated. */
    private OrientationChange orientation;

    private final CheckedSupplier<ImageTimestampsAttributes, ImageIOException> timestamps;

    /** The number of channels in the image. */
    private final int numberChannels;

    /** A list of channel-names or {@link Optional#empty()} if unavailable. */
    private final Optional<List<String>> channelNames;

    /**
     * Creates with a particular {@link IFormatReader} and associated metadata.
     *
     * @param reader the reader.
     * @param metadata the metadata.
     * @param readOptions parameters that effect how to read the image.
     * @param calculateOrientation any correction of orientation to be applied as bytes are
     *     converted.
     */
    public BioformatsOpenedRaster(
            IFormatReader reader,
            IMetadata metadata,
            ReadOptions readOptions,
            CalculateOrientationChange calculateOrientation,
            CheckedSupplier<ImageTimestampsAttributes, ImageIOException> timestamps) {
        this.reader = reader;
        this.metadata = metadata;
        this.readOptions = readOptions;
        this.calculateOrientation = calculateOrientation;
        this.timestamps = CachedSupplier.cacheChecked(timestamps);

        sizeT = readOptions.sizeT(reader);
        rgb = readOptions.isRGB(reader);
        bitsPerPixel = readOptions.effectiveBitsPerPixel(reader);
        numberChannels = readOptions.sizeC(reader);

        channelNames = readOptions.determineChannelNames(reader);
    }

    @Override
    public TimeSequence open(int seriesIndex, Progress progress, Logger logger)
            throws ImageIOException {

        int pixelType = reader.getPixelType();

        VoxelDataType dataType = multiplexFormat(pixelType);

        return openAsType(seriesIndex, progress, dataType, logger);
    }

    @Override
    public int numberSeries() {
        return reader.getSeriesCount();
    }

    @Override
    public int numberFrames(Logger logger) {
        return sizeT;
    }

    @Override
    public int numberChannels(Logger logger) throws ImageIOException {
        return numberChannels;
    }

    @Override
    public int bitDepth(Logger logger) {
        return bitsPerPixel;
    }

    @Override
    public boolean isRGB() {
        return rgb;
    }

    @Override
    public void close() throws ImageIOException {
        try {
            reader.close();
        } catch (IOException e) {
            throw new ImageIOException("Could not close bioformats file", e);
        }
    }

    @Override
    public Dimensions dimensionsForSeries(int seriesIndex, Logger logger) throws ImageIOException {
        Dimensions dimensions = dimensionsForSeriesWithoutOrientationChange(seriesIndex);
        return calculateOrientation(logger).dimensions(dimensions);
    }

    @Override
    public ImageTimestampsAttributes timestamps() throws ImageIOException {
        return timestamps.get();
    }

    @Override
    public Optional<List<String>> channelNames(Logger logger) throws ImageIOException {
        return channelNames;
    }

    private Dimensions dimensionsForSeriesWithoutOrientationChange(int seriesIndex)
            throws ImageIOException {
        try {
            return new DimensionsCreator(metadata).apply(reader, readOptions, seriesIndex);
        } catch (CreateException e) {
            throw new ImageIOException("Failed to establish dimensions for the image", e);
        }
    }

    /** Opens as a specific data-type. */
    private TimeSequence openAsType(
            int seriesIndex, Progress progress, VoxelDataType dataType, Logger logger)
            throws ImageIOException {

        try {
            LOG.debug(String.format("Opening series %d as %s", seriesIndex, dataType));

            LOG.debug(String.format("Size T = %d; Size C = %d", sizeT, numberChannels));

            reader.setSeries(seriesIndex);

            TimeSequence timeSequence = new TimeSequence();

            Dimensions dimensions = dimensionsForSeriesWithoutOrientationChange(seriesIndex);

            // Assumes order of time first, and then channels
            List<Channel> listAllChannels =
                    createUninitialisedChannels(
                            dimensions, timeSequence, multiplexVoxelDataType(dataType), logger);

            copyBytesIntoChannels(
                    listAllChannels, dimensions, progress, dataType, readOptions, logger);

            LOG.debug(
                    String.format(
                            "Finished opening series %d as %s with z=%d, t=%d",
                            seriesIndex, dataType, reader.getSizeZ(), reader.getSizeT()));

            return timeSequence;

        } catch (ImageIOException e) {
            throw new ImageIOException(
                    String.format("An error occurred opening series %d", seriesIndex), e);
        }
    }

    private List<Channel> createUninitialisedChannels(
            Dimensions dimensions,
            TimeSequence timeSequence,
            ChannelFactorySingleType factory,
            Logger logger)
            throws ImageIOException {

        try {
            dimensions = calculateOrientation(logger).dimensions(dimensions);

            /** A list of all channels i.e. aggregating the channels associated with each stack */
            List<Channel> listAllChannels = new ArrayList<>();

            for (int t = 0; t < sizeT; t++) {
                Stack stack = new Stack(isRGB());
                for (int c = 0; c < numberChannels; c++) {

                    Channel channel = factory.createEmptyUninitialised(dimensions);

                    stack.addChannel(channel);
                    listAllChannels.add(channel);
                }
                timeSequence.add(stack);
            }

            return listAllChannels;
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    private void copyBytesIntoChannels(
            List<Channel> listChannels,
            Dimensions dimensions,
            Progress progress,
            VoxelDataType dataType,
            ReadOptions readOptions,
            Logger logger)
            throws ImageIOException {

        try {
            // Determine what type to convert to
            ConvertTo<?> convertTo =
                    ConvertToFactory.create(
                            reader, dataType, readOptions.effectiveBitsPerPixel(reader));

            CopyConvert.copyAllFrames(
                    reader,
                    listChannels,
                    progress,
                    new ImageFileShape(dimensions, numberChannels, sizeT),
                    convertTo,
                    readOptions,
                    calculateOrientation(logger));
        } catch (FormatException | IOException | CreateException e) {
            throw new ImageIOException(
                    "An error occurred while copying frames when opening with the BioformatsReader",
                    e);
        }
    }

    /**
     * Lazy evaluation of the orientation, using the logger associated with the job not the
     * experiment.
     */
    private OrientationChange calculateOrientation(Logger logger) throws ImageIOException {
        if (orientation == null) {
            orientation = calculateOrientation.calculateOrientationChange(logger);
        }
        return orientation;
    }
}
