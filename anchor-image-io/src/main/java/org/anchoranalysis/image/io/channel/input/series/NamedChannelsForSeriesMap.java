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

import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.core.progress.ProgressMultiple;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.core.stack.named.NamedStacks;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.channel.ChannelEntry;
import org.anchoranalysis.image.io.channel.input.NamedEntries;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;

@RequiredArgsConstructor
public class NamedChannelsForSeriesMap implements NamedChannelsForSeries {

    // END REQUIRED ARGUMENTS
    // Null until the first time we request a channel
    private final OpenedImageFile openedFile;
    private final NamedEntries channelMap;
    private final int seriesIndex;
    // END REQUIRED ARGUMENTS

    private TimeSequence sequence;

    @Override
    public Dimensions dimensions() throws ImageIOException {
        return openedFile.dimensionsForSeries(seriesIndex);
    }

    @Override
    public Channel getChannel(String channelName, int timeIndex, Progress progress)
            throws GetOperationFailedException {

        int index = channelMap.get(channelName);
        if (index == -1) {
            throw new GetOperationFailedException(
                    channelName, String.format("'%s' cannot be found", channelName));
        }

        try {
            Stack stack = createTimeSeries(progress).get(timeIndex);

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
    public Optional<Channel> getChannelOptional(
            String channelName, int timeIndex, Progress progress)
            throws GetOperationFailedException {

        int index = channelMap.get(channelName);
        if (index == -1) {
            return Optional.empty();
        }

        try {
            Stack stack = createTimeSeries(progress).get(timeIndex);

            if (index >= stack.getNumberChannels()) {
                return Optional.empty();
            }

            return Optional.of(stack.getChannel(index));
        } catch (OperationFailedException e) {
            throw new GetOperationFailedException(channelName, e);
        }
    }

    @Override
    public int sizeT(Progress progress) throws ImageIOException {
        try {
            return createTimeSeries(progress).size();
        } catch (OperationFailedException e) {
            throw new ImageIOException(e);
        }
    }

    @Override
    public Set<String> channelNames() {
        return channelMap.keySet();
    }

    @Override
    public int numberChannels() {
        return channelMap.keySet().size();
    }

    @Override
    public boolean hasChannel(String channelName) {
        return channelMap.keySet().contains(channelName);
    }

    @Override
    public void addAsSeparateChannels(NamedStacks stacks, int timeIndex, Progress progress)
            throws OperationFailedException {

        try {
            try (ProgressMultiple progressMultiple =
                    new ProgressMultiple(progress, channelMap.keySet().size())) {

                // Populate our stack from all the channels
                for (String channelName : channelMap.keySet()) {
                    Channel image =
                            getChannel(
                                    channelName, timeIndex, progressMultiple.trackCurrentChild());
                    stacks.add(channelName, new Stack(image));
                    progressMultiple.incrementChild();
                }
            } catch (GetOperationFailedException e) {
                throw new OperationFailedException(e);
            }

        } finally {
            progress.close();
        }
    }

    @Override
    public void addAsSeparateChannels(
            NamedProviderStore<TimeSequence> stackCollection, int timeIndex)
            throws OperationFailedException {
        // Populate our stack from all the channels
        for (String channelName : channelMap.keySet()) {
            stackCollection.add(
                    channelName,
                    StoreSupplier.cache(
                            () -> extractChannelAsTimeSequence(channelName, timeIndex)));
        }
    }

    @Override
    public boolean isRGB() throws ImageIOException {
        return openedFile.isRGB();
    }

    @Override
    public StoreSupplier<Stack> allChannelsAsStack(int t) {
        return StoreSupplier.cache(() -> stackForAllChannels(t));
    }

    private TimeSequence createTimeSeries(Progress progress) throws OperationFailedException {
        if (sequence == null) {
            try {
                sequence = openedFile.open(seriesIndex, progress);
            } catch (ImageIOException e) {
                throw new OperationFailedException(e);
            }
        }
        return sequence;
    }

    private Stack stackForAllChannels(int timeIndex) throws OperationFailedException {
        Stack out = new Stack();

        for (ChannelEntry entry : channelMap.entryCollection()) {
            try {
                out.addChannel(getChannel(entry.getName(), timeIndex, ProgressIgnore.get()));
            } catch (IncorrectImageSizeException | GetOperationFailedException e) {
                throw new OperationFailedException(e);
            }
        }

        return out;
    }

    private TimeSequence extractChannelAsTimeSequence(String channelName, int timeIndex)
            throws OperationFailedException {
        try {
            Channel image = getChannel(channelName, timeIndex, ProgressIgnore.get());
            return new TimeSequence(new Stack(image));
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
