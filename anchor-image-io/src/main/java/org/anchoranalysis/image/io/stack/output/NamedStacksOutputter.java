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

package org.anchoranalysis.image.io.stack.output;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Outputs a named-set of stacks, performing appropriate checks on what is enabled or not.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NamedStacksOutputter {

    /**
     * Writes all or a subset from a set of named-stacks to a directory as a raster.
     *
     * <p>A second-level output manager filters which stacks are written.
     *
     * @param stacks the stacks to output (or a subset thereof according to the second-level output
     *     manager)
     * @param outputName name to use for the directory, for checking if it is allowed, and for the
     *     second-level outputs
     * @param suppressSubdirectory if true, a separate subdirectory is not created, and rather the
     *     outputs occur in the parent directory.
     * @param outputter determines where and how the outputting occurs
     * @throws OutputWriteFailedException if the output cannot be written.
     */
    public static void output(
            NamedProvider<Stack> stacks,
            String outputName,
            boolean suppressSubdirectory,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        OutputSequenceStackFactory.NO_RESTRICTIONS.withoutOrderSubset(
                stacks, outputName, suppressSubdirectory, outputter);
    }
}
