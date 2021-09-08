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

package org.anchoranalysis.image.io.channel.input.series;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.progress.ProgressMultiple;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.core.stack.named.NamedStacks;
import org.anchoranalysis.image.io.ImageIOException;

public class NamedChannelsForSeriesConcatenate implements NamedChannelsForSeries {

    private List<NamedChannelsForSeries> list = new ArrayList<>();

    @Override
    public Channel getChannel(String channelName, int timeIndex, Progress progress)
            throws GetOperationFailedException {

        for (NamedChannelsForSeries item : list) {

            Optional<Channel> channel = item.getChannelOptional(channelName, timeIndex, progress);
            if (channel.isPresent()) {
                return channel.get();
            }
        }

        throw new GetOperationFailedException(
                channelName, String.format("channelName '%s' is not found", channelName));
    }

    @Override
    public Optional<Channel> getChannelOptional(
            String channelName, int timeIndex, Progress progress)
            throws GetOperationFailedException {

        for (NamedChannelsForSeries item : list) {

            Optional<Channel> channel = item.getChannelOptional(channelName, timeIndex, progress);
            if (channel.isPresent()) {
                return channel;
            }
        }

        return Optional.empty();
    }

    public void addAsSeparateChannels(NamedStacks stackCollection, int t, Progress progress)
            throws OperationFailedException {

        try (ProgressMultiple progressMultiple = new ProgressMultiple(progress, list.size())) {

            for (NamedChannelsForSeries item : list) {
                item.addAsSeparateChannels(
                        stackCollection, t, progressMultiple.trackCurrentChild());
                progressMultiple.incrementChild();
            }
        }
    }

    public void addAsSeparateChannels(
            NamedProviderStore<TimeSequence> stackCollection, int timeIndex)
            throws OperationFailedException {
        for (NamedChannelsForSeries item : list) {
            item.addAsSeparateChannels(stackCollection, timeIndex);
        }
    }

    public boolean add(NamedChannelsForSeries e) {
        return list.add(e);
    }

    @Override
    public int numberChannels() {
        return list.stream().mapToInt(NamedChannelsForSeries::numberChannels).sum();
    }

    public Set<String> channelNames() {
        HashSet<String> set = new HashSet<>();
        for (NamedChannelsForSeries item : list) {
            set.addAll(item.channelNames());
        }
        return set;
    }

    public int sizeT(Progress progress) throws ImageIOException {

        int series = 0;
        boolean first = true;

        for (NamedChannelsForSeries item : list) {
            if (first) {
                series = item.sizeT(progress);
                first = false;
            } else {
                series = Math.min(series, item.sizeT(progress));
            }
        }
        return series;
    }

    @Override
    public boolean hasChannel(String channelName) {
        for (NamedChannelsForSeries item : list) {
            if (item.channelNames().contains(channelName)) {
                return true;
            }
        }
        return false;
    }

    public Dimensions dimensions() throws ImageIOException {
        // Assumes dimensions are the same for every item in the list
        return list.get(0).dimensions();
    }

    public Iterator<NamedChannelsForSeries> iteratorFromRaster() {
        return list.iterator();
    }

    @Override
    public StoreSupplier<Stack> allChannelsAsStack(int t) {
        return StoreSupplier.cache(() -> stackAllChannels(t));
    }

    @Override
    public boolean isRGB() throws ImageIOException {
        // Assume once a concatenation happens, it is no longer a RGB file.
        return false;
    }

    private Stack stackAllChannels(int timeIndex) throws OperationFailedException {
        Stack out = new Stack();
        for (NamedChannelsForSeries namedChannels : list) {
            try {
                addAllChannelsFrom(namedChannels.allChannelsAsStack(timeIndex).get(), out);
            } catch (IncorrectImageSizeException e) {
                throw new OperationFailedException(e);
            }
        }
        return out;
    }

    private static void addAllChannelsFrom(Stack source, Stack destination)
            throws IncorrectImageSizeException {
        for (Channel channel : source) {
            destination.addChannel(channel);
        }
    }
}
