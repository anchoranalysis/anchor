/*-
 * #%L
 * anchor-mpp-sgmn
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

package org.anchoranalysis.mpp.segment.bean.define;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.image.bean.nonbean.init.CreateCombinedStack;
import org.anchoranalysis.image.io.histogram.output.HistogramCSVGenerator;
import org.anchoranalysis.image.io.object.output.hdf5.ObjectCollectionWriter;
import org.anchoranalysis.image.io.stack.output.NamedStacksOutputter;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.collection.NamedProviderOutputter;
import org.anchoranalysis.io.generator.serialized.XStreamGenerator;
import org.anchoranalysis.io.output.enabled.OutputEnabledMutable;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.segment.define.OutputterDirectories;

/**
 * This class will output entities associated with {@link MPPInitParams} in particular directories.
 *
 * <p>These outputs are:
 *
 * <ul>
 *   <li>{@link OutputterDirectories#STACKS}
 *   <li>{@link OutputterDirectories#MARKS}
 *   <li>{@link OutputterDirectories#HISTOGRAMS}
 *   <li>{@link OutputterDirectories#OBJECTS}
 *       <p>Second-level output rules determine whether particular elements in each directory are
 *       written or not.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class ParamsOutputter {

    private MPPInitParams params;
    private boolean suppressSubfolders;
    private OutputterChecked outputter;

    /**
     * Adds all possible output-names to a {@link OutputEnabledMutable}.
     *
     * @param outputEnabled where to add all possible output-names
     */
    public static void addAllOutputNamesTo(OutputEnabledMutable outputEnabled) {
        outputEnabled.addEnabledOutputFirst(
                OutputterDirectories.STACKS,
                OutputterDirectories.MARKS,
                OutputterDirectories.HISTOGRAMS,
                OutputterDirectories.OBJECTS);
    }

    /**
     * Writes (a selection of) entities from {@code params} to the filesystem in particular
     * directories.
     *
     * @throws OutputWriteFailedException
     */
    public void output() throws OutputWriteFailedException {

        if (!outputter.getSettings().hasBeenInitialized()) {
            throw new OutputWriteFailedException(
                    "The Outputter's settings have not yet been initialized");
        }

        stacks();
        marks();
        histograms();
        objects();
    }

    private void stacks() throws OutputWriteFailedException {
        NamedStacksOutputter.output(
                CreateCombinedStack.apply(params.getImage()),
                OutputterDirectories.STACKS,
                suppressSubfolders,
                outputter);
    }

    private void marks() throws OutputWriteFailedException {
        output(
                params.getMarksCollection(),
                new XStreamGenerator<MarkCollection>(Optional.of("marks")),
                OutputterDirectories.MARKS);
    }

    private void histograms() throws OutputWriteFailedException {
        output(
                params.getImage().histograms(),
                new HistogramCSVGenerator(),
                OutputterDirectories.HISTOGRAMS);
    }

    private void objects() throws OutputWriteFailedException {
        output(
                params.getImage().objects(),
                ObjectCollectionWriter.generator(),
                OutputterDirectories.OBJECTS);
    }

    private <T> void output(
            NamedProviderStore<T> store, Generator<T> generator, String directoryName)
            throws OutputWriteFailedException {
        new NamedProviderOutputter<>(store, generator, outputter)
                .output(directoryName, suppressSubfolders);
    }
}
