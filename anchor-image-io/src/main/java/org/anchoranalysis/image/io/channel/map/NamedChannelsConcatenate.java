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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.anchoranalysis.image.io.stack.time.TimeSeries;

/**
 * Exposes one or more instances of a {@link NamedChannelsMap} as a single aggregated {@link
 * NamedChannelsMap}.
 *
 * <p>The aggregated map contains all the channels from each underlying map.
 *
 * <p>If a channel-name is non-unique, it is undefined which channel will be retrieved for this
 * name.
 *
 * <p>{@link Channel}s are added to a {@link Stack} in the order they appear successively in the
 * {@code list}.
 *
 * @author Owen Feehan
 */
public class NamedChannelsConcatenate implements NamedChannelsMap {

    private final List<NamedChannelsMap> list;

    /**
     * Create with arguments to concatenate.
     *
     * @param maps each {@link NamedChannelsMap} to be concatenated.
     */
    public NamedChannelsConcatenate(NamedChannelsMap... maps) {
        this.list = Arrays.stream(maps).collect(Collectors.toList());
    }

    @Override
    public Channel getChannel(String channelName, int timeIndex, Logger logger)
            throws GetOperationFailedException {

        for (NamedChannelsMap item : list) {

            Optional<Channel> channel = item.getChannelOptional(channelName, timeIndex, logger);
            if (channel.isPresent()) {
                return channel.get();
            }
        }

        throw new GetOperationFailedException(
                channelName, String.format("channelName '%s' is not found", channelName));
    }

    @Override
    public Optional<Channel> getChannelOptional(String channelName, int timeIndex, Logger logger)
            throws GetOperationFailedException {

        for (NamedChannelsMap item : list) {

            Optional<Channel> channel = item.getChannelOptional(channelName, timeIndex, logger);
            if (channel.isPresent()) {
                return channel;
            }
        }

        return Optional.empty();
    }

    @Override
    public void addAsSeparateChannels(NamedStacks stackCollection, int t, Logger logger)
            throws OperationFailedException {

        for (NamedChannelsMap item : list) {
            item.addAsSeparateChannels(stackCollection, t, logger);
        }
    }

    @Override
    public void addAsSeparateChannels(
            NamedProviderStore<TimeSeries> stackCollection, int timeIndex, Logger logger)
            throws OperationFailedException {
        for (NamedChannelsMap item : list) {
            item.addAsSeparateChannels(stackCollection, timeIndex, logger);
        }
    }

    @Override
    public int numberChannels() {
        return list.stream().mapToInt(NamedChannelsMap::numberChannels).sum();
    }

    @Override
    public Set<String> channelNames() {
        HashSet<String> set = new HashSet<>();
        for (NamedChannelsMap item : list) {
            set.addAll(item.channelNames());
        }
        return set;
    }

    @Override
    public int sizeT(Logger logger) throws ImageIOException {

        int series = 0;
        boolean first = true;

        for (NamedChannelsMap item : list) {
            if (first) {
                series = item.sizeT(logger);
                first = false;
            } else {
                series = Math.min(series, item.sizeT(logger));
            }
        }
        return series;
    }

    @Override
    public boolean hasChannel(String channelName) {
        for (NamedChannelsMap item : list) {
            if (item.channelNames().contains(channelName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Dimensions dimensions(Logger logger) throws ImageIOException {
        // Assumes dimensions are the same for every item in the list
        return list.get(0).dimensions(logger);
    }

    @Override
    public StoreSupplier<Stack> allChannelsAsStack(int t, Logger logger) {
        return StoreSupplier.cache(() -> stackAllChannels(t, logger));
    }

    @Override
    public boolean isRGB() throws ImageIOException {
        // Assume once a concatenation happens, it is no longer a RGB file.
        return false;
    }

    /** Create a {@link Stack} of all {@link Channel}s at {@code timeIndex}. */
    private Stack stackAllChannels(int timeIndex, Logger logger) throws OperationFailedException {
        Stack out = new Stack();
        for (NamedChannelsMap namedChannels : list) {
            try {
                addAllChannelsFrom(namedChannels.allChannelsAsStack(timeIndex, logger).get(), out);
            } catch (IncorrectImageSizeException e) {
                throw new OperationFailedException(e);
            }
        }
        return out;
    }

    /** Add all {@link Channel}s in {@code source} to {@code destination}. */
    private static void addAllChannelsFrom(Stack source, Stack destination)
            throws IncorrectImageSizeException {
        for (Channel channel : source) {
            destination.addChannel(channel);
        }
    }
}
