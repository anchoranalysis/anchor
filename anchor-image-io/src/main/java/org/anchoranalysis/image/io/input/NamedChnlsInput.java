/* (C)2020 */
package org.anchoranalysis.image.io.input;

import org.anchoranalysis.core.cache.WrapOperationAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.input.series.NamedChnlCollectionForSeries;
import org.anchoranalysis.image.stack.TimeSequence;

/**
 * Provides a set of channels as an input, each of which has a name. Only a single time-point is
 * possible
 *
 * @author Owen Feehan
 */
public abstract class NamedChnlsInput extends ProvidesStackInput {

    /** Number of series */
    public abstract int numSeries() throws RasterIOException;

    /** Dimensions of a particular series */
    public abstract ImageDimensions dim(int seriesIndex) throws RasterIOException;

    /** Number of channels */
    public abstract int numChnl() throws RasterIOException;

    /** Bit-depth of image */
    public abstract int bitDepth() throws RasterIOException;

    // Where most of our time is being taken up when opening a raster
    public abstract NamedChnlCollectionForSeries createChnlCollectionForSeries(
            int seriesNum, ProgressReporter progressReporter) throws RasterIOException;

    @Override
    public void addToStore(
            NamedProviderStore<TimeSequence> stackCollection,
            int seriesNum,
            ProgressReporter progressReporter)
            throws OperationFailedException {
        // Adds each channel as a separate stack
        try {
            NamedChnlCollectionForSeries ncc =
                    createChnlCollectionForSeries(seriesNum, progressReporter);
            // Apply it only to first time-series frame
            ncc.addAsSeparateChnls(stackCollection, 0);

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
                new WrapOperationAsCached<>(
                        () -> chnlCollectionAsTimeSequence(seriesNum, progressReporter)));
    }

    @Override
    public int numFrames() {
        return 1;
    }

    private TimeSequence chnlCollectionAsTimeSequence(
            int seriesNum, ProgressReporter progressReporter) throws OperationFailedException {
        // Apply it only to first time-series frame
        try {
            NamedChnlCollectionForSeries ncc =
                    createChnlCollectionForSeries(seriesNum, progressReporter);
            return new TimeSequence(ncc.allChnlsAsStack(0).doOperation());

        } catch (RasterIOException e) {
            throw new OperationFailedException(e);
        }
    }
}
