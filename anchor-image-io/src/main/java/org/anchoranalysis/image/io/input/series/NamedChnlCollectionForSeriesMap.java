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
/* (C)2020 */
package org.anchoranalysis.image.io.input.series;

import java.util.Optional;
import java.util.Set;
import org.anchoranalysis.core.cache.WrapOperationAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.channel.map.ImgChnlMapEntry;
import org.anchoranalysis.image.io.chnl.map.ImgChnlMap;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public class NamedChnlCollectionForSeriesMap implements NamedChnlCollectionForSeries {

    private ImgChnlMap chnlMap;

    // Null until the first time we request a channel
    private OpenedRaster openedRaster;
    private TimeSequence ts = null;
    private int seriesNum;

    public NamedChnlCollectionForSeriesMap(
            OpenedRaster openedRaster, ImgChnlMap chnlMap, int seriesNum) {
        this.chnlMap = chnlMap;
        this.seriesNum = seriesNum;
        this.openedRaster = openedRaster;
    }

    @Override
    public ImageDimensions dimensions() throws RasterIOException {
        return openedRaster.dim(seriesNum);
    }

    // The outputManager is in case we want to do any debugging
    @Override
    public Channel getChnl(String chnlName, int t, ProgressReporter progressReporter)
            throws GetOperationFailedException {

        int index = chnlMap.get(chnlName);
        if (index == -1) {
            throw new GetOperationFailedException(String.format("'%s' cannot be found", chnlName));
        }

        Stack stack = createTs(progressReporter).get(t);

        if (index >= stack.getNumChnl()) {
            throw new GetOperationFailedException(
                    String.format("Stack does not have a channel corresponding to '%s'", chnlName));
        }

        return stack.getChnl(chnlMap.getException(chnlName));
    }

    // The outputManager is in case we want to do any debugging
    @Override
    public Optional<Channel> getChnlOrNull(
            String chnlName, int t, ProgressReporter progressReporter)
            throws GetOperationFailedException {

        int index = chnlMap.get(chnlName);
        if (index == -1) {
            return Optional.empty();
        }

        Stack stack = createTs(progressReporter).get(t);

        if (index >= stack.getNumChnl()) {
            return Optional.empty();
        }

        return Optional.of(stack.getChnl(index));
    }

    @Override
    public int sizeT(ProgressReporter progressReporter) throws RasterIOException {
        try {
            return createTs(progressReporter).size();
        } catch (GetOperationFailedException e) {
            throw new RasterIOException(e);
        }
    }

    @Override
    public Set<String> chnlNames() {
        return chnlMap.keySet();
    }

    @Override
    public boolean hasChnl(String chnlName) {
        return chnlMap.keySet().contains(chnlName);
    }

    @Override
    public void addAsSeparateChnls(
            NamedImgStackCollection stackCollection, int t, ProgressReporter progressReporter)
            throws OperationFailedException {

        try {
            try (ProgressReporterMultiple prm =
                    new ProgressReporterMultiple(progressReporter, chnlMap.keySet().size())) {

                // Populate our stack from all the channels
                for (String chnlName : chnlMap.keySet()) {
                    Channel image = getChnl(chnlName, t, new ProgressReporterOneOfMany(prm));
                    stackCollection.addImageStack(chnlName, new Stack(image));
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
    public void addAsSeparateChnls(NamedProviderStore<TimeSequence> stackCollection, final int t)
            throws OperationFailedException {
        // Populate our stack from all the channels
        for (final String chnlName : chnlMap.keySet()) {
            stackCollection.add(
                    chnlName,
                    new WrapOperationAsCached<TimeSequence, OperationFailedException>(
                            () -> extractChnlAsTimeSequence(chnlName, t)));
        }
    }

    @Override
    public Operation<Stack, OperationFailedException> allChnlsAsStack(int t) {
        return new WrapOperationAsCached<>(() -> stackForAllChnls(t));
    }

    private TimeSequence createTs(ProgressReporter progressReporter)
            throws GetOperationFailedException {
        if (ts == null) {
            try {
                ts = openedRaster.open(seriesNum, progressReporter);
            } catch (RasterIOException e) {
                throw new GetOperationFailedException(e);
            }
        }
        return ts;
    }

    private Stack stackForAllChnls(int t) throws OperationFailedException {
        Stack out = new Stack();

        for (ImgChnlMapEntry entry : chnlMap.entryCollection()) {
            try {
                out.addChnl(getChnl(entry.getName(), t, ProgressReporterNull.get()));
            } catch (IncorrectImageSizeException | GetOperationFailedException e) {
                throw new OperationFailedException(e);
            }
        }

        return out;
    }

    private TimeSequence extractChnlAsTimeSequence(String chnlName, int t)
            throws OperationFailedException {
        try {
            Channel image = getChnl(chnlName, t, ProgressReporterNull.get());
            return new TimeSequence(new Stack(image));
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
