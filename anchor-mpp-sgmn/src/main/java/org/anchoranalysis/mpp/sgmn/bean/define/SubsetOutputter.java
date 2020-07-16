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
/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.define;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.collection.IterableGeneratorOutputHelper;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

class SubsetOutputter<T> {
    private NamedProvider<T> providers;
    private OutputAllowed oa;
    private IterableGenerator<T> generator;
    private BoundOutputManager outputManager;
    private String outputName;
    private String suffix;
    private boolean suppressSubfoldersIn;

    public SubsetOutputter(
            NamedProvider<T> providers,
            OutputAllowed oa,
            IterableGenerator<T> generator,
            BoundOutputManager outputManager,
            String outputName,
            String suffix,
            boolean suppressSubfoldersIn) {
        super();
        this.providers = providers;
        this.oa = oa;
        this.generator = generator;
        this.outputManager = outputManager;
        this.outputName = outputName;
        this.suffix = suffix;
        this.suppressSubfoldersIn = suppressSubfoldersIn;
    }

    public void outputSubset(ErrorReporter errorReporter) {

        if (!outputManager.isOutputAllowed(outputName)) {
            return;
        }

        IterableGeneratorOutputHelper.output(
                IterableGeneratorOutputHelper.subset(providers, oa, errorReporter),
                generator,
                outputManager,
                outputName,
                suffix,
                errorReporter,
                suppressSubfoldersIn);
    }

    public void outputSubsetWithException() throws OutputWriteFailedException {

        if (!outputManager.isOutputAllowed(outputName)) {
            return;
        }

        IterableGeneratorOutputHelper.outputWithException(
                IterableGeneratorOutputHelper.subsetWithException(providers, oa),
                generator,
                outputManager,
                outputName,
                suffix,
                suppressSubfoldersIn);
    }
}
