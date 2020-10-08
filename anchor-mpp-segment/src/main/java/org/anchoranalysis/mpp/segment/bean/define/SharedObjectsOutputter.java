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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.image.bean.nonbean.init.CreateCombinedStack;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.io.stack.StacksOutputter;
import org.anchoranalysis.io.output.enabled.OutputEnabledMutable;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.mpp.segment.define.OutputterDirectories;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class SharedObjectsOutputter {

    public static void output(
            ImageInitParams imageInit, boolean suppressSubfolders, InputOutputContext context) {
        StacksOutputter.outputSubset(
                CreateCombinedStack.apply(imageInit), suppressSubfolders, context);
    }

    public static void outputWithException(
            ImageInitParams imageInit, boolean suppressSubfolders, InputOutputContext context)
            throws OutputWriteFailedException {
        StacksOutputter.outputSubsetWithException(
                CreateCombinedStack.apply(imageInit), context.getOutputter(), suppressSubfolders);
    }

    public static void output(
            MPPInitParams soMPP, boolean suppressSubfolders, InputOutputContext context) {
        ErrorReporter errorReporter = context.getErrorReporter();
        Outputter outputter = context.getOutputter();

        SubsetOutputterFactory factory =
                new SubsetOutputterFactory(soMPP, outputter, suppressSubfolders);
        factory.marks().outputSubset(errorReporter);
        factory.histograms().outputSubset(errorReporter);
        factory.objects().outputSubset(errorReporter);
    }

    public static void outputWithException(
            MPPInitParams soMPP, Outputter outputter, boolean suppressSubfolders)
            throws OutputWriteFailedException {

        if (!outputter.getSettings().hasBeenInit()) {
            throw new OutputWriteFailedException(
                    "The Outputter's settings have not yet been initialized");
        }

        SubsetOutputterFactory factory =
                new SubsetOutputterFactory(soMPP, outputter, suppressSubfolders);
        factory.marks().outputSubsetChecked();
        factory.histograms().outputSubsetChecked();
        factory.objects().outputSubsetChecked();
    }

    /**
     * Adds all possible output-names to a {@link OutputEnabledMutable}.
     *
     * @param outputEnabled where to add all possible output-names
     */
    public static void addAllOutputs(OutputEnabledMutable outputEnabled) {
        outputEnabled.addEnabledOutputFirst(
                OutputterDirectories.STACKS,
                OutputterDirectories.MARKS,
                OutputterDirectories.HISTOGRAMS,
                OutputterDirectories.OBJECTS);
    }
}
