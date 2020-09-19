/*-
 * #%L
 * anchor-io-generator
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
package org.anchoranalysis.io.generator.sequence;

import lombok.AllArgsConstructor;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

@AllArgsConstructor
public class GeneratorSequenceFactory {

    private static final boolean CHECK_IF_ALLOWED = false;

    /** Name of sub-directory to place sequence in (which is also used as the outputName) */
    private final String subdirectoryName;

    /**
     * Name of the prefix of each file in the sub-directory.
     *
     * <p>The full-name has an increment number appended e.g. <code>$prefix_000000.tif</code> etc.
     */
    private final String filePrefixName;

    public <T> GeneratorSequenceIncrementalRerouteErrors<T> createIncremental(
            Generator<T> generator, BoundIOContext context) {

        return new GeneratorSequenceIncrementalRerouteErrors<>(
                new GeneratorSequenceIncrementalWriter<>(
                        context.getOutputManager().getDelegate(),
                        subdirectoryName,
                        outputNameStyle(filePrefixName),
                        generator,
                        0,
                        CHECK_IF_ALLOWED),
                context.getErrorReporter());
    }

    public <T> GeneratorSequenceNonIncremental<T> createNonIncremental(
            Generator<T> generator, BoundOutputManagerRouteErrors outputManager) {

        return new GeneratorSequenceNonIncrementalWriter<>(
                outputManager.getDelegate(),
                subdirectoryName,
                outputNameStyle(filePrefixName),
                generator,
                CHECK_IF_ALLOWED);
    }

    private static IntegerSuffixOutputNameStyle outputNameStyle(String outputName) {
        return new IntegerSuffixOutputNameStyle(outputName, 6);
    }
}
