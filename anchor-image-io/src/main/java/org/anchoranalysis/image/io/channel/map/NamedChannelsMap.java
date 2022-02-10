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

package org.anchoranalysis.image.io.channel.map;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.named.NamedStacks;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.channel.input.ChannelGetter;
import org.anchoranalysis.image.io.stack.time.TimeSeries;

/**
 * A collection of {@link Channel}s, each identified by a unique name and a time-index.
 *
 * <p>All contained {@link Channel}s must have the same dimensions, irrespective of name and
 * time-index.
 *
 * @author Owen Feehan
 */
public interface NamedChannelsMap extends ChannelGetter {

    /**
     * Gets a channel if it exists, returning empty if it doesn't.
     *
     * @param channelName name of channel.
     * @param timeIndex point in time-series.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return the channel if it exists, or empty otherwise.
     * @throws GetOperationFailedException if something goes wrong getting an existing channel (but
     *     never if a channel doesn't exist).
     */
    Optional<Channel> getChannelOptional(String channelName, int timeIndex, Logger logger)
            throws GetOperationFailedException;

    /**
     * The number of channels that exist for the series.
     *
     * @return the number of channels.
     */
    int numberChannels();

    /**
     * All channel-names.
     *
     * @return a set view of all channel-names in the map.
     */
    Set<String> channelNames();

    /**
     * The number of frames along the time-axis.
     *
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return the number of time-frames.
     * @throws ImageIOException if unable to successfully determine the number of frames.
     */
    int sizeT(Logger logger) throws ImageIOException;

    /**
     * The dimensions of each {@link Channel}.
     *
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return the dimensions.
     * @throws ImageIOException if unable to successfully determine the dimensions.
     */
    Dimensions dimensions(Logger logger) throws ImageIOException;

    /**
     * Adds each {@link Channel} as a separate {@link Stack} in a {@link NamedStacks}.
     *
     * @param destination the {@link NamedStacks} into which each {@link Channel} is copied.
     * @param timeIndex the index of the time-frame, beginning at zero.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @throws OperationFailedException if unable to add a {@link Channel}.
     */
    void addAsSeparateChannels(NamedStacks destination, int timeIndex, Logger logger)
            throws OperationFailedException;

    /**
     * Adds each {@link Channel} as a separate {@link TimeSeries} in a {@link NamedProviderStore}.
     *
     * <p>Although added as a {@link TimeSeries}, each added {@link Stack} will have only a
     * single-time frame at point 0, representing the channel found at {@code timeIndex}.
     *
     * @param destination the {@link NamedStacks} into which each {@link Channel} is copied.
     * @param timeIndex the index of the time-frame, beginning at zero.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @throws OperationFailedException if unable to add a {@link Channel}.
     */
    void addAsSeparateChannels(
            NamedProviderStore<TimeSeries> destination, int timeIndex, Logger logger)
            throws OperationFailedException;

    /**
     * Combines all channels as a single {@link Stack} at a particular time-frame.
     *
     * @param timeIndex the index of the time-frame, beginning at zero.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return a supplier for a newly created {@link Stack}, containing all {@link Channel}s. The
     *     order depends on implementation.
     */
    StoreSupplier<Stack> allChannelsAsStack(int timeIndex, Logger logger);

    /**
     * Whether the channels describe an RGB image.
     *
     * <p>In this case, there should be exactly three channels, named "red", "green" and "blue".
     *
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return true if the channels describe an RGB image.
     * @throws ImageIOException if this cannot be successfully inferred.
     */
    boolean isRGB(Logger logger) throws ImageIOException;
}
