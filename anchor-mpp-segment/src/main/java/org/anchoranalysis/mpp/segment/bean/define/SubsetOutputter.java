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

import lombok.AllArgsConstructor;
import java.util.function.Supplier;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.collection.GeneratorOutputHelper;
import org.anchoranalysis.io.output.SingleLevelOutputEnabled;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

@AllArgsConstructor
class SubsetOutputter<T> {

    private NamedProvider<T> provider;
    private Supplier<SingleLevelOutputEnabled> outputEnabledSecondLevel;
    private Generator<T> generator;
    private OutputterChecked outputter;
    private String outputName;
    private String suffix;
    private boolean suppressSubfoldersIn;

    public void outputSubset(ErrorReporter errorReporter) {

        if (shouldExitEarly()) {
            return;
        }

        GeneratorOutputHelper.output(
                GeneratorOutputHelper.subset(provider, outputEnabledSecondLevel.get(), errorReporter),
                generator,
                outputter,
                outputName,
                suffix,
                errorReporter,
                suppressSubfoldersIn);
    }

    public void outputSubsetChecked() throws OutputWriteFailedException {

        if (shouldExitEarly()) {
            return;
        }

        GeneratorOutputHelper.outputWithException(
                GeneratorOutputHelper.subsetWithException(provider, outputEnabledSecondLevel.get()),
                generator,
                outputter,
                outputName,
                suffix,
                suppressSubfoldersIn);
    }
        
    /** Exit early if the output is disabled, or if there are no providers to output */
    private boolean shouldExitEarly() {
        return provider.isEmpty() || !outputter.getOutputsEnabled().isOutputEnabled(outputName);
    }
}
