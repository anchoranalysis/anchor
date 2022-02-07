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
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.named.NamedStacks;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.channel.IndexedChannel;
import org.anchoranalysis.image.io.channel.input.ChannelMap;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;
import org.anchoranalysis.image.io.stack.time.TimeSeries;

/**
 * A set of named {@link Channel}s available from an {@link OpenedImageFile}.
 *
 * <p>{@link Channel}s are added to a {@link Stack} in the order they appear in the {@code
 * channelMap}.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class OpenedNamedChannels implements NamedChannelsMap {

    // END REQUIRED ARGUMENTS
    /** The underlying opened image-file that provides the channels. */
    private final OpenedImageFile openedFile;

    /** A mapping between names to indices of {@link Channel}s. */
    private final ChannelMap channelMap;

    /** Which series to open in {@link OpenedImageFile}. */
    private final int seriesIndex;
    // END REQUIRED ARGUMENTS

    /**
     * The currently opened {@link TimeSeries}.
     *
     * <p>null until first opened.
     */
    private TimeSeries timeSeries;

    @Override
    public Dimensions dimensions(Logger logger) throws ImageIOException {
        return openedFile.dimensionsForSeries(seriesIndex, logger);
    }

    @Override
    public Channel getChannel(String channelName, int timeIndex, Logger logger)
            throws GetOperationFailedException {

        int index = channelMap.get(channelName);
        if (index == -1) {
            throw new GetOperationFailedException(
                    channelName, String.format("'%s' cannot be found", channelName));
        }

        try {
            Stack stack = createTimeSeries(logger).getFrame(timeIndex);

            if (index >= stack.getNumberChannels()) {
                throw new GetOperationFailedException(
                        channelName,
                        String.format(
                                "Stack does not have a channel corresponding to '%s'",
                                channelName));
            }

            return stack.getChannel(channelMap.getException(channelName));

        } catch (OperationFailedException e) {
            throw new GetOperationFailedException(channelName, e);
        }
    }

    @Override
    public Optional<Channel> getChannelOptional(String channelName, int timeIndex, Logger logger)
            throws GetOperationFailedException {

        int index = channelMap.get(channelName);
        if (index == -1) {
            return Optional.empty();
        }

        try {
            Stack stack = createTimeSeries(logger).getFrame(timeIndex);

            if (index >= stack.getNumberChannels()) {
                return Optional.empty();
            }

            return Optional.of(stack.getChannel(index));
        } catch (OperationFailedException e) {
            throw new GetOperationFailedException(channelName, e);
        }
    }

    @Override
    public int sizeT(Logger logger) throws ImageIOException {
        try {
            return createTimeSeries(logger).size();
        } catch (OperationFailedException e) {
            throw new ImageIOException("Cannot establish the size of the time-dimension", e);
        }
    }

    @Override
    public Set<String> channelNames() {
        return channelMap.names();
    }

    @Override
    public int numberChannels() {
        return channelMap.names().size();
    }

    @Override
    public boolean hasChannel(String channelName) {
        return channelMap.names().contains(channelName);
    }

    @Override
    public void addAsSeparateChannels(NamedStacks destination, int timeIndex, Logger logger)
            throws OperationFailedException {

        try {
            // Populate our stack from all the channels
            for (String channelName : channelMap.names()) {
                Channel image = getChannel(channelName, timeIndex, logger);
                destination.add(channelName, new Stack(image));
            }
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    public void addAsSeparateChannels(
            NamedProviderStore<TimeSeries> destination, int timeIndex, Logger logger)
            throws OperationFailedException {
        // Populate our stack from all the channels
        for (String channelName : channelMap.names()) {
            destination.add(
                    channelName,
                    StoreSupplier.cache(
                            () -> extractChannelAsTimeSeries(channelName, timeIndex, logger)));
        }
    }

    @Override
    public boolean isRGB() throws ImageIOException {
        return openedFile.isRGB();
    }

    @Override
    public StoreSupplier<Stack> allChannelsAsStack(int t, Logger logger) {
        return StoreSupplier.cache(() -> stackForAllChannels(t, logger));
    }

    /** Create the {@link TimeSeries} from which channels are extracted. */
    private TimeSeries createTimeSeries(Logger logger) throws OperationFailedException {
        if (timeSeries == null) {
            try {
                timeSeries = openedFile.open(seriesIndex, logger);
            } catch (ImageIOException e) {
                throw new OperationFailedException(e);
            }
        }
        return timeSeries;
    }

    /** Creates a {@link Stack} containing each channel at {@code timeIndex}. */
    private Stack stackForAllChannels(int timeIndex, Logger logger)
            throws OperationFailedException {
        Stack out = new Stack();

        for (IndexedChannel entry : channelMap.values()) {
            try {
                out.addChannel(getChannel(entry.getName(), timeIndex, logger));
            } catch (IncorrectImageSizeException | GetOperationFailedException e) {
                throw new OperationFailedException(e);
            }
        }

        return out;
    }

    /**
     * Creates a {@link TimeSeries} containing a single channel and time-point, extracted from
     * {@code timeIndex}.
     */
    private TimeSeries extractChannelAsTimeSeries(String channelName, int timeIndex, Logger logger)
            throws OperationFailedException {
        try {
            Channel image = getChannel(channelName, timeIndex, logger);
            return new TimeSeries(new Stack(image));
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
