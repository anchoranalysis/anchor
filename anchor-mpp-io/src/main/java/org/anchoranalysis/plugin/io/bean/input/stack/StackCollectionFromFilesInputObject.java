/*-
 * #%L
 * anchor-plugin-io
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

package org.anchoranalysis.plugin.io.bean.input.stack;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.io.input.FileInput;

@RequiredArgsConstructor
class StackCollectionFromFilesInputObject implements StackSequenceInput {

    /** The root object that is used to provide the descriptiveName and pathForBinding */
    private final FileInput delegate;

    private final RasterReader rasterReader;

    /**
     * Uses the last series (from all series) only, and ignores any other series-numbers
     *
     * <p>This is to correct for a problem with formats such as czi where the seriesIndex doesn't
     * indicate the total number of series but rather is incremented with each acquisition, so for
     * our purposes we treat it as if its 0
     */
    private final boolean useLastSeriesIndexOnly;

    // We cache a certain amount of stacks read for particular series
    private OpenedRaster openedRasterMemo = null;

    public int numberSeries() throws RasterIOException {
        if (useLastSeriesIndexOnly) {
            return 1;
        } else {
            return getOpenedRaster().numberSeries();
        }
    }

    @Override
    public int numberFrames() throws OperationFailedException {

        try {
            return getOpenedRaster().numberFrames();
        } catch (RasterIOException e) {
            throw new OperationFailedException(e);
        }
    }

    public TimeSequenceSupplier createStackSequenceForSeries(int seriesNum)
            throws RasterIOException {

        // We always use the last one
        if (useLastSeriesIndexOnly) {
            seriesNum = getOpenedRaster().numberSeries() - 1;
        }
        return openRasterAsOperation(getOpenedRaster(), seriesNum);
    }

    @Override
    public void addToStoreInferNames(
            NamedProviderStore<TimeSequence> stackCollection,
            int seriesNum,
            ProgressReporter progressReporter)
            throws OperationFailedException {
        throw new OperationFailedException("Not supported");
    }

    @Override
    public void addToStoreWithName(
            String name,
            NamedProviderStore<TimeSequence> stackCollection,
            int seriesNum,
            ProgressReporter progressReporter)
            throws OperationFailedException {

        stackCollection.add(
                name,
                () -> {
                    try {
                        return createStackSequenceForSeries(seriesNum).get(progressReporter);
                    } catch (RasterIOException e) {
                        throw new OperationFailedException(e);
                    }
                });
    }

    private static TimeSequenceSupplier openRasterAsOperation(
            final OpenedRaster openedRaster, final int seriesNum) {
        return progressReporter -> {
            try {
                return openedRaster.open(seriesNum, progressReporter);
            } catch (RasterIOException e) {
                throw new OperationFailedException(e);
            }
        };
    }

    @Override
    public String descriptiveName() {
        return delegate.descriptiveName();
    }

    @Override
    public Optional<Path> pathForBinding() {
        return delegate.pathForBinding();
    }

    public RasterReader getRasterReader() {
        return rasterReader;
    }

    public File getFile() {
        return delegate.getFile();
    }

    private OpenedRaster getOpenedRaster() throws RasterIOException {
        if (openedRasterMemo == null) {
            openedRasterMemo =
                    rasterReader.openFile(
                            delegate.pathForBinding()
                                    .orElseThrow(
                                            () ->
                                                    new RasterIOException(
                                                            "A binding-path must be associated with this file")));
        }
        return openedRasterMemo;
    }

    @Override
    public void close(ErrorReporter errorReporter) {
        if (openedRasterMemo != null) {
            try {
                openedRasterMemo.close();
            } catch (RasterIOException e) {
                errorReporter.recordError(StackSequenceInput.class, e);
            }
        }
    }
}
