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
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.test.LoggingFixture;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OutputterFixture {

    public static Outputter outputter(Path pathTempFolder)
            throws BindFailedException {
        return outputter(
                outputterChecked(pathTempFolder));
    }
    
    public static Outputter outputter(OutputManager outputManager)
            throws BindFailedException {
        return outputter(
                outputterChecked(outputManager));
    }

    public static OutputterChecked outputterChecked(Path pathTempFolder)
            throws BindFailedException {
        return outputterChecked(
                OutputManagerFixture.createOutputManager(pathTempFolder));
    }

    public static OutputterChecked outputterChecked(OutputManager outputManager) throws BindFailedException {
        try {
            return outputManager.bindRootFolder(
                    "debug",
                    new ManifestRecorder(),
                    Optional.empty(),
                    new FilePathPrefixerParams(false, Optional.empty()));
        } catch (FilePathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }
    
    private static Outputter outputter(OutputterChecked outputter ) {
        ErrorReporter errorReporter = LoggingFixture.suppressedLogErrorReporter().errorReporter();
        return new Outputter(outputter, errorReporter);
    }
}
