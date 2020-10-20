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

package org.anchoranalysis.image.io.input;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.identifier.provider.store.StoreSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.input.series.NamedChannelsForSeries;

/**
 * Provides a set of channels as an input, each of which has a name.
 *
 * <p>Only the first time-point is considered from each series.
 *
 * @author Owen Feehan
 */
public abstract class NamedChannelsInput implements ProvidesStackInput {

    /** Number of series that exist. */
    public abstract int numberSeries() throws ImageIOException;

    /** Dimensions of a particular series */
    public abstract Dimensions dimensions(int seriesIndex) throws ImageIOException;

    /** Number of channels */
    public abstract int numberChannels() throws ImageIOException;

    /** Bit-depth of image */
    public abstract int bitDepth() throws ImageIOException;

    public abstract NamedChannelsForSeries createChannelsForSeries(
            int seriesIndex, ProgressReporter progressReporter) throws ImageIOException;

    @Override
    public void addToStoreInferNames(
            NamedProviderStore<TimeSequence> stackCollection,
            int seriesIndex,
            ProgressReporter progressReporter)
            throws OperationFailedException {
        // Adds each channel as a separate stack
        try {
            NamedChannelsForSeries ncc = createChannelsForSeries(seriesIndex, progressReporter);
            // Apply it only to first time-series frame
            ncc.addAsSeparateChannels(stackCollection, 0);

        } catch (ImageIOException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    public void addToStoreWithName(
            String name,
            NamedProviderStore<TimeSequence> stacks,
            int seriesIndex,
            ProgressReporter progressReporter)
            throws OperationFailedException {

        // Adds this stack (cached) under the given name
        stacks.add(
                name,
                StoreSupplier.cache(() -> channelsAsTimeSequence(seriesIndex, progressReporter)));
    }

    @Override
    public int numberFrames() {
        return 1;
    }

    private TimeSequence channelsAsTimeSequence(int seriesNum, ProgressReporter progressReporter)
            throws OperationFailedException {
        // Apply it only to first time-series frame
        try {
            NamedChannelsForSeries namedChannels =
                    createChannelsForSeries(seriesNum, progressReporter);
            return new TimeSequence(namedChannels.allChannelsAsStack(0).get());

        } catch (ImageIOException e) {
            throw new OperationFailedException(e);
        }
    }
}
