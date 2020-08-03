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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.StoreSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.input.series.NamedChannelsForSeries;
import org.anchoranalysis.image.stack.TimeSequence;

/**
 * Provides a set of channels as an input, each of which has a name. Only a single time-point is
 * possible
 *
 * @author Owen Feehan
 */
public abstract class NamedChnlsInput implements ProvidesStackInput {

    /** Number of series */
    public abstract int numSeries() throws RasterIOException;

    /** Dimensions of a particular series */
    public abstract ImageDimensions dim(int seriesIndex) throws RasterIOException;

    /** Number of channels */
    public abstract int numChnl() throws RasterIOException;

    /** Bit-depth of image */
    public abstract int bitDepth() throws RasterIOException;

    // Where most of our time is being taken up when opening a raster
    public abstract NamedChannelsForSeries createChannelsForSeries(
            int seriesNum, ProgressReporter progressReporter) throws RasterIOException;

    @Override
    public void addToStoreInferNames(
            NamedProviderStore<TimeSequence> stackCollection,
            int seriesNum,
            ProgressReporter progressReporter)
            throws OperationFailedException {
        // Adds each channel as a separate stack
        try {
            NamedChannelsForSeries ncc = createChannelsForSeries(seriesNum, progressReporter);
            // Apply it only to first time-series frame
            ncc.addAsSeparateChannels(stackCollection, 0);

        } catch (RasterIOException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    public void addToStoreWithName(
            String name,
            NamedProviderStore<TimeSequence> stackCollection,
            int seriesNum,
            ProgressReporter progressReporter)
            throws OperationFailedException {

        // Adds this stack (cached) under the given name
        stackCollection.add(
                name,
                StoreSupplier.cache(() -> channelsAsTimeSequence(seriesNum, progressReporter)) );
    }

    @Override
    public int numberFrames() {
        return 1;
    }

    private TimeSequence channelsAsTimeSequence(
            int seriesNum, ProgressReporter progressReporter) throws OperationFailedException {
        // Apply it only to first time-series frame
        try {
            NamedChannelsForSeries ncc = createChannelsForSeries(seriesNum, progressReporter);
            return new TimeSequence(ncc.allChannelsAsStack(0).get());

        } catch (RasterIOException e) {
            throw new OperationFailedException(e);
        }
    }
}
