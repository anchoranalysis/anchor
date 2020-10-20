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

package org.anchoranalysis.image.io.input.series;

import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.NamedStacks;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.channel.map.ChannelEntry;
import org.anchoranalysis.image.io.channel.NamedEntries;
import org.anchoranalysis.image.io.stack.OpenedRaster;

@RequiredArgsConstructor
public class NamedChannelsForSeriesMap implements NamedChannelsForSeries {

    // END REQUIRED ARGUMENTS
    // Null until the first time we request a channel
    private final OpenedRaster openedRaster;
    private final NamedEntries channelMap;
    private final int seriesNum;
    // END REQUIRED ARGUMENTS

    private TimeSequence ts = null;

    @Override
    public Dimensions dimensions() throws ImageIOException {
        return openedRaster.dimensionsForSeries(seriesNum);
    }

    // The outputter is in case we want to do any debugging
    @Override
    public Channel getChannel(String channelName, int timeIndex, ProgressReporter progressReporter)
            throws GetOperationFailedException {

        int index = channelMap.get(channelName);
        if (index == -1) {
            throw new GetOperationFailedException(
                    channelName, String.format("'%s' cannot be found", channelName));
        }

        try {
            Stack stack = createTimeSeries(progressReporter).get(timeIndex);

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

    // The outputter is in case we want to do any debugging
    @Override
    public Optional<Channel> getChannelOptional(
            String channelName, int t, ProgressReporter progressReporter)
            throws GetOperationFailedException {

        int index = channelMap.get(channelName);
        if (index == -1) {
            return Optional.empty();
        }

        try {
            Stack stack = createTimeSeries(progressReporter).get(t);

            if (index >= stack.getNumberChannels()) {
                return Optional.empty();
            }

            return Optional.of(stack.getChannel(index));
        } catch (OperationFailedException e) {
            throw new GetOperationFailedException(channelName, e);
        }
    }

    @Override
    public int sizeT(ProgressReporter progressReporter) throws ImageIOException {
        try {
            return createTimeSeries(progressReporter).size();
        } catch (OperationFailedException e) {
            throw new ImageIOException(e);
        }
    }

    @Override
    public Set<String> channelNames() {
        return channelMap.keySet();
    }

    @Override
    public boolean hasChannel(String channelName) {
        return channelMap.keySet().contains(channelName);
    }

    @Override
    public void addAsSeparateChannels(
            NamedStacks stackCollection, int t, ProgressReporter progressReporter)
            throws OperationFailedException {

        try {
            try (ProgressReporterMultiple prm =
                    new ProgressReporterMultiple(progressReporter, channelMap.keySet().size())) {

                // Populate our stack from all the channels
                for (String channelName : channelMap.keySet()) {
                    Channel image = getChannel(channelName, t, new ProgressReporterOneOfMany(prm));
                    stackCollection.add(channelName, new Stack(image));
                    prm.incrWorker();
                }
            } catch (GetOperationFailedException e) {
                throw new OperationFailedException(e);
            }

        } finally {
            progressReporter.close();
        }
    }

    @Override
    public void addAsSeparateChannels(NamedProviderStore<TimeSequence> stackCollection, final int t)
            throws OperationFailedException {
        // Populate our stack from all the channels
        for (final String channelName : channelMap.keySet()) {
            stackCollection.add(
                    channelName,
                    StoreSupplier.cache(() -> extractChannelAsTimeSequence(channelName, t)));
        }
    }

    @Override
    public StoreSupplier<Stack> allChannelsAsStack(int t) {
        return StoreSupplier.cache(() -> stackForAllChannels(t));
    }

    private TimeSequence createTimeSeries(ProgressReporter progressReporter)
            throws OperationFailedException {
        if (ts == null) {
            try {
                ts = openedRaster.open(seriesNum, progressReporter);
            } catch (ImageIOException e) {
                throw new OperationFailedException(e);
            }
        }
        return ts;
    }

    private Stack stackForAllChannels(int t) throws OperationFailedException {
        Stack out = new Stack();

        for (ChannelEntry entry : channelMap.entryCollection()) {
            try {
                out.addChannel(getChannel(entry.getName(), t, ProgressReporterNull.get()));
            } catch (IncorrectImageSizeException | GetOperationFailedException e) {
                throw new OperationFailedException(e);
            }
        }

        return out;
    }

    private TimeSequence extractChannelAsTimeSequence(String channelName, int t)
            throws OperationFailedException {
        try {
            Channel image = getChannel(channelName, t, ProgressReporterNull.get());
            return new TimeSequence(new Stack(image));
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
