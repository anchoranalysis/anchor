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
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.core.stack.ImageFileAttributes;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;
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
    private final OrientationChange orientationCorrection;
    private final CheckedSupplier<ImageFileAttributes, IOException> timestamps;

    /** The number of channels in the image. */
    @Getter private final int numberChannels;

    /** A list of channel-names or {@link Optional#empty()} if unavailable. */
    @Getter private final Optional<List<String>> channelNames;

    /**
     * Creates with a particular {@link IFormatReader} and associated metadata.
     *
     * @param reader the reader.
     * @param metadata the metadata.
     * @param readOptions parameters that effect how to read the image.
     * @param orientationCorrection any correction of orientation to be applied as bytes are
     *     converted.
     */
    public BioformatsOpenedRaster(
            IFormatReader reader,
            IMetadata metadata,
            ReadOptions readOptions,
            OrientationChange orientationCorrection, CheckedSupplier<ImageFileAttributes, IOException> timestamps) {
        this.reader = reader;
        this.metadata = metadata;
        this.readOptions = readOptions;
        this.orientationCorrection = orientationCorrection;
        this.timestamps = CachedSupplier.cache(timestamps);

        sizeT = readOptions.sizeT(reader);
        rgb = readOptions.isRGB(reader);
        bitsPerPixel = readOptions.effectiveBitsPerPixel(reader);
        numberChannels = readOptions.sizeC(reader);

        channelNames = readOptions.determineChannelNames(reader);
    }

    @Override
    public TimeSequence open(int seriesIndex, Progress progress) throws ImageIOException {

        int pixelType = reader.getPixelType();

        VoxelDataType dataType = multiplexFormat(pixelType);

        return openAsType(seriesIndex, progress, dataType);
    }

    @Override
    public int numberSeries() {
        return reader.getSeriesCount();
    }

    @Override
    public int numberFrames() {
        return sizeT;
    }

    @Override
    public int bitDepth() {
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
            throw new ImageIOException(e);
        }
    }

    @Override
    public Dimensions dimensionsForSeries(int seriesIndex) throws ImageIOException {
        Dimensions dimensions = dimensionsForSeriesWithoutOrientationChange(seriesIndex);
        return orientationCorrection.dimensions(dimensions);
    }

    private Dimensions dimensionsForSeriesWithoutOrientationChange(int seriesIndex)
            throws ImageIOException {
        try {
            return new DimensionsCreator(metadata).apply(reader, readOptions, seriesIndex);
        } catch (CreateException e) {
            throw new ImageIOException(e);
        }
    }

    /** Opens as a specific data-type. */
    private TimeSequence openAsType(int seriesIndex, Progress progress, VoxelDataType dataType)
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
                            dimensions, timeSequence, multiplexVoxelDataType(dataType));

            copyBytesIntoChannels(listAllChannels, dimensions, progress, dataType, readOptions);

            LOG.debug(
                    String.format(
                            "Finished opening series %d as %s with z=%d, t=%d",
                            seriesIndex, dataType, reader.getSizeZ(), reader.getSizeT()));

            return timeSequence;

        } catch (FormatException | IOException | IncorrectImageSizeException | CreateException e) {
            throw new ImageIOException(e);
        }
    }

    private List<Channel> createUninitialisedChannels(
            Dimensions dimensions, TimeSequence timeSequence, ChannelFactorySingleType factory)
            throws IncorrectImageSizeException {

        dimensions = orientationCorrection.dimensions(dimensions);

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
    }

    private void copyBytesIntoChannels(
            List<Channel> listChannels,
            Dimensions dimensions,
            Progress progress,
            VoxelDataType dataType,
            ReadOptions readOptions)
            throws FormatException, IOException, CreateException {

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
                orientationCorrection);
    }

    @Override
    public ImageFileAttributes fileAttributes() throws ImageIOException {
        try {
            return timestamps.get();
        } catch (IOException e) {
            throw new ImageIOException(e);
        }
    }
}
