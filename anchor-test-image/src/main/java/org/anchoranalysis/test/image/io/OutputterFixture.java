/*-
 * #%L
 * anchor-test-image
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

package org.anchoranalysis.test.image.io;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.bean.rules.OutputEnabledRules;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.test.LoggerFixture;
import org.anchoranalysis.test.io.output.OutputManagerFixture;
import org.anchoranalysis.test.io.output.OutputterCheckedFixture;

/** Utility class for creating Outputter instances for testing purposes. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OutputterFixture {

    /**
     * Creates an Outputter instance with optionally a path to a temporary directory for outtputting
     * files.
     *
     * @param pathTempDirectory Optional path to a temporary directory. When undefined, an
     *     incrementing number will be used for outtputting files.
     * @return A new Outputter instance.
     * @throws BindFailedException If binding the outputter fails.
     */
    public static Outputter outputter(Optional<Path> pathTempDirectory) throws BindFailedException {
        return outputter(outputterChecked(pathTempDirectory));
    }

    /**
     * Creates an Outputter instance with a specified OutputManager and OutputEnabledRules.
     *
     * @param outputManager The OutputManager to use.
     * @param outputsEnabled The OutputEnabledRules to apply.
     * @return A new Outputter instance.
     * @throws BindFailedException If binding the outputter fails.
     */
    public static Outputter outputter(
            OutputManager outputManager, OutputEnabledRules outputsEnabled)
            throws BindFailedException {
        return outputter(OutputterCheckedFixture.createFrom(outputManager, outputsEnabled));
    }

    /**
     * Creates an OutputterChecked instance with optionally a path to a temporary directory for
     * outputting files.
     *
     * @param pathTempDirectory optional path to a temporary directory. When undefined, an
     *     incrementing number will be used for outtputting files.
     * @return A new OutputterChecked instance.
     * @throws BindFailedException If binding the outputter fails.
     */
    public static OutputterChecked outputterChecked(Optional<Path> pathTempDirectory)
            throws BindFailedException {
        return OutputterCheckedFixture.createFrom(
                OutputManagerFixture.createOutputManager(pathTempDirectory, Optional.empty()));
    }

    /**
     * Creates an Outputter instance from an OutputterChecked instance.
     *
     * @param outputter The OutputterChecked instance to wrap.
     * @return A new Outputter instance.
     */
    private static Outputter outputter(OutputterChecked outputter) {
        ErrorReporter errorReporter = LoggerFixture.suppressedLogger().errorReporter();
        return new Outputter(outputter, errorReporter);
    }
}
