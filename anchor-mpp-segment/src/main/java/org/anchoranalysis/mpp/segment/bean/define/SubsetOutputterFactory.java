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
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.io.objects.ObjectCollectionWriter;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.histogram.HistogramCSVGenerator;
import org.anchoranalysis.io.generator.serialized.XStreamGenerator;
import org.anchoranalysis.io.output.bean.enabled.IgnoreUnderscorePrefix;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.segment.define.OutputterDirectories;

/**
 * This class will output for certain outputs with second-level optionally defined.
 * 
 * These outputs are:
 * 
 * <ul>
 * <li>{@link OutputterDirectories#STACKS}
 * <li>{@link OutputterDirectories#MARKS}
 * <li>{@link OutputterDirectories#HISTOGRAMS}
 * <li>{@link OutputterDirectories#OBJECTS}
 * 
 * TODO merge OutputterDirectories and StackOutputKeys.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class SubsetOutputterFactory {

    private MPPInitParams soMPP;
    private Outputter outputter;
    private boolean suppressSubfolders;

    public SubsetOutputter<MarkCollection> marks() {
        return create(
                soMPP.getMarksCollection(),
                new XStreamGenerator<MarkCollection>(Optional.of("marks")),
                OutputterDirectories.MARKS);
    }

    public SubsetOutputter<Histogram> histograms() {
        return create(
                soMPP.getImage().histograms(),
                new HistogramCSVGenerator(),
                OutputterDirectories.HISTOGRAMS);
    }

    public SubsetOutputter<ObjectCollection> objects() {
        return create(
                soMPP.getImage().objects(),
                ObjectCollectionWriter.generator(),
                OutputterDirectories.OBJECTS);
    }

    private <T> SubsetOutputter<T> create(
            NamedProviderStore<T> store,
            Generator<T> generator,
            String directoryName) {
        return new SubsetOutputter<>(
                store,
                () -> outputter.outputsEnabled().second(directoryName, IgnoreUnderscorePrefix.INSTANCE),
                generator,
                outputter.getChecked(),
                directoryName,
                "",
                suppressSubfolders);
    }
}
