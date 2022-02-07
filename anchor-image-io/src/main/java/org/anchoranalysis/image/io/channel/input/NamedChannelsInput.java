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

package org.anchoranalysis.image.io.channel.input;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.ImageMetadata;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.channel.map.NamedChannelsMap;
import org.anchoranalysis.image.io.stack.input.ProvidesStackInput;
import org.anchoranalysis.image.io.stack.time.TimeSeries;

/**
 * Provides a set of channels as an input, with each channel having a name.
 *
 * <p>Only the first time-point is considered from each series.
 *
 * @author Owen Feehan
 */
public abstract class NamedChannelsInput implements ProvidesStackInput {

    /**
     * Number of series that exist.
     *
     * @return the number of series.
     * @throws ImageIOException if the information cannot be successfully retrieved from the
     *     underlying image.
     */
    public abstract int numberSeries() throws ImageIOException;

    /**
     * Dimensions of a particular series.
     *
     * @param seriesIndex the index of the series.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return the corresponding dimensions.
     * @throws ImageIOException if the information cannot be successfully retrieved from the
     *     underlying image.
     */
    public abstract Dimensions dimensions(int seriesIndex, Logger logger) throws ImageIOException;

    /**
     * Number of channels.
     *
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return the number of channels.
     * @throws ImageIOException if the information cannot be successfully retrieved from the
     *     underlying image.
     */
    public abstract int numberChannels(Logger logger) throws ImageIOException;

    /**
     * Bit-depth of image.
     *
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return the bit-depth.
     * @throws ImageIOException if the information cannot be successfully retrieved from the
     *     underlying image.
     */
    public abstract int bitDepth(Logger logger) throws ImageIOException;

    /**
     * Creates a {@link NamedChannelsMap} representing the channels for a particular series in this
     * input.
     *
     * @param seriesIndex the index of the series to use (beginning at 0).
     * @param logger the logger where informative or non-fatal error messages amy be written.
     * @return a newly created {@link NamedChannelsMap} bound to {@code seriesIndex}.
     * @throws ImageIOException if the operation cannot successfully complete.
     */
    public abstract NamedChannelsMap createChannelsForSeries(int seriesIndex, Logger logger)
            throws ImageIOException;

    /**
     * The image-metadata associated with a particular series.
     *
     * @param seriesIndex the index of the series.
     * @param logger the logger.
     * @return the metadata.
     * @throws ImageIOException if the metadata cannot be calculated.
     */
    public abstract ImageMetadata metadata(int seriesIndex, Logger logger) throws ImageIOException;

    @Override
    public void addToStoreInferNames(
            NamedProviderStore<TimeSeries> stacks, int seriesIndex, Logger logger)
            throws OperationFailedException {
        // Adds each channel as a separate stack
        try {
            NamedChannelsMap namedChannels = createChannelsForSeries(seriesIndex, logger);
            // Apply it only to first time-series frame
            namedChannels.addAsSeparateChannels(stacks, 0, logger);

        } catch (ImageIOException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    public void addToStoreWithName(
            String name, NamedProviderStore<TimeSeries> stacks, int seriesIndex, Logger logger)
            throws OperationFailedException {

        // Adds this stack (cached) under the given name
        stacks.add(name, StoreSupplier.cache(() -> channelsAsTimeSequence(seriesIndex, logger)));
    }

    @Override
    public int numberFrames() {
        return 1;
    }

    /** All channels for a particular series, exposed as a {@link TimeSeries}. */
    private TimeSeries channelsAsTimeSequence(int seriesIndex, Logger logger)
            throws OperationFailedException {
        // Apply it only to first time-series frame
        try {
            NamedChannelsMap namedChannels = createChannelsForSeries(seriesIndex, logger);
            return new TimeSeries(namedChannels.allChannelsAsStack(0, logger).get());

        } catch (ImageIOException e) {
            throw new OperationFailedException(e);
        }
    }
}
