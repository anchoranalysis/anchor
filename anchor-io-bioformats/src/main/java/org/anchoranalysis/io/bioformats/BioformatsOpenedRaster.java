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

package org.anchoranalysis.io.bioformats;

import static org.anchoranalysis.io.bioformats.MultiplexDataTypes.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.meta.IMetadata;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertTo;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertToFactory;
import org.anchoranalysis.io.bioformats.copyconvert.CopyConvert;
import org.anchoranalysis.io.bioformats.copyconvert.ImageFileShape;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BioformatsOpenedRaster implements OpenedRaster {

    private final IFormatReader reader;
    private final ReadOptions readOptions;

    private final IMetadata lociMetadata;

    private final int numChannel;
    private final int sizeT;
    private final boolean rgb;
    private final int bitsPerPixel;

    private final Optional<List<String>> channelNames;

    private static final Log LOG = LogFactory.getLog(BioformatsOpenedRaster.class);

    /**
     * @param reader
     * @param lociMetadata
     * @param readOptions
     */
    public BioformatsOpenedRaster(
            IFormatReader reader, IMetadata lociMetadata, ReadOptions readOptions) {
        super();
        this.reader = reader;
        this.lociMetadata = lociMetadata;
        this.readOptions = readOptions;

        sizeT = readOptions.sizeT(reader);
        rgb = readOptions.isRGB(reader);
        bitsPerPixel = readOptions.effectiveBitsPerPixel(reader);
        numChannel = readOptions.sizeC(reader);

        channelNames = readOptions.determineChannelNames(reader);
    }

    @Override
    public TimeSequence open(int seriesIndex, ProgressReporter progressReporter)
            throws RasterIOException {

        int pixelType = reader.getPixelType();

        VoxelDataType dataType = multiplexFormat(pixelType);

        return openAsType(seriesIndex, progressReporter, dataType);
    }

    @Override
    public int numberSeries() {
        return reader.getSeriesCount();
    }

    @Override
    public int numberFrames() {
        return sizeT;
    }

    /** Returns a list of channel-names or null if they are not available */
    public Optional<List<String>> channelNames() {
        return channelNames;
    }

    public int numberChannels() {
        return numChannel;
    }

    @Override
    public int bitDepth() throws RasterIOException {
        return bitsPerPixel;
    }

    @Override
    public boolean isRGB() throws RasterIOException {
        return rgb;
    }

    @Override
    public void close() throws RasterIOException {
        try {
            reader.close();
        } catch (IOException e) {
            throw new RasterIOException(e);
        }
    }

    @Override
    public Dimensions dimensionsForSeries(int seriesIndex) {
        return new DimensionsCreator(lociMetadata).apply(reader, readOptions, seriesIndex);
    }

    /** Opens as a specific data-type */
    private TimeSequence openAsType(
            int seriesIndex, ProgressReporter progressReporter, VoxelDataType dataType)
            throws RasterIOException {

        try {
            LOG.debug(String.format("Opening series %d as %s", seriesIndex, dataType));

            LOG.debug(String.format("Size T = %d; Size C = %d", sizeT, numChannel));

            reader.setSeries(seriesIndex);

            TimeSequence ts = new TimeSequence();

            Dimensions dimensions = dimensionsForSeries(seriesIndex);

            // Assumes order of time first, and then channels
            List<Channel> listAllChannels =
                    createUninitialisedChannels(dimensions, ts, multiplexVoxelDataType(dataType));

            copyBytesIntoChannels(
                    listAllChannels, dimensions, progressReporter, dataType, readOptions);

            LOG.debug(
                    String.format(
                            "Finished opening series %d as %s with z=%d, t=%d",
                            seriesIndex, dataType, reader.getSizeZ(), reader.getSizeT()));

            return ts;

        } catch (FormatException | IOException | IncorrectImageSizeException | CreateException e) {
            throw new RasterIOException(e);
        }
    }

    private List<Channel> createUninitialisedChannels(
            Dimensions dimensions, TimeSequence ts, ChannelFactorySingleType factory)
            throws IncorrectImageSizeException {

        /** A list of all channels i.e. aggregating the channels associated with each stack */
        List<Channel> listAllChannels = new ArrayList<>();

        for (int t = 0; t < sizeT; t++) {
            Stack stack = new Stack();
            for (int c = 0; c < numChannel; c++) {

                Channel channel = factory.createEmptyUninitialised(dimensions);

                stack.addChannel(channel);
                listAllChannels.add(channel);
            }
            ts.add(stack);
        }

        return listAllChannels;
    }

    private void copyBytesIntoChannels(
            List<Channel> listChannels,
            Dimensions dimensions,
            ProgressReporter progressReporter,
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
                progressReporter,
                new ImageFileShape(dimensions, numChannel, sizeT),
                convertTo,
                readOptions);
    }
}
