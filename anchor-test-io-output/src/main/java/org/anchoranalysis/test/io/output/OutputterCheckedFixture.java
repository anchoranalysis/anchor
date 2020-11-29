/*-
 * #%L
 * anchor-test-io-output
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
package org.anchoranalysis.test.io.output;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.path.prefixer.FilePathPrefixerContext;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerException;
import org.anchoranalysis.io.output.recorded.RecordedOutputsWithRules;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OutputterCheckedFixture {

    public static OutputterChecked create(Manifest manifest) throws BindFailedException {
        return createFrom(OutputManagerFixture.createOutputManager(Optional.empty()), manifest);
    }

    public static OutputterChecked createFrom(OutputManager outputManager)
            throws BindFailedException {
        return createFrom(outputManager, new Manifest());
    }

    public static OutputterChecked createFrom(OutputManager outputManager, Manifest manifest)
            throws BindFailedException {
        try {
            return outputManager.createExperimentOutputter(
                    "debug",
                    manifest,
                    new RecordedOutputsWithRules(),
                    Optional.empty(),
                    new FilePathPrefixerContext(false, Optional.empty()));
        } catch (PathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }
}
