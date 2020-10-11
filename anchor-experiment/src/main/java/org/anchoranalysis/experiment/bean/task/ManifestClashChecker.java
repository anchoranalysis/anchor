/*-
 * #%L
 * anchor-experiment
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

package org.anchoranalysis.experiment.bean.task;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.path.DirectoryWithPrefix;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ManifestClashChecker {

    public static void throwExceptionIfClashes(
            ManifestRecorder manifestExperiment, OutputterChecked boundOutput, Path pathForBinding)
            throws JobExecutionException {
        // Now we do a check, to ensure that our experimentalManifest and manifest are going to
        // write files
        //  to the same folder (without at least having some kind of prefix, to prevent files
        // overwriting each other)
        Path experimentalRoot = manifestExperiment.getRootFolder().getRelativePath();
        if (wouldClashWithExperimentRoot(experimentalRoot, boundOutput.getPrefix())) {
            throw new JobExecutionException(
                    String.format(
                            "There is a clash between the bound-prefixer the root experiment directory.%n   Path for binding: %s%n   Root experiment directory: %s%nThey are writing to the same directory, without any prefix, and outputs may collide.",
                            pathForBinding, experimentalRoot.toString()));
        }
    }

    /**
     * Checks to see if this prefixer would clash with a particular experimentalDirectory by writing
     * files to the same locations that an experiment might do
     *
     * <p>This is particular dangerous for conflicting messagelog.txt
     *
     * <p>To avoid this happening, the prefixer should be either: 1. writing out to a differerent
     * directory 2. have a prefix, which is prepended to all files, and keeps them unique
     *
     * @param experimentalDirectory the root directory associated with the experiment
     * @return true if it would clash, false otherwise
     */
    private static boolean wouldClashWithExperimentRoot(
            Path experimentalDirectory, DirectoryWithPrefix prefix) {
        if (!prefix.getFilenamePrefix().isEmpty()) {
            // We're safe if there's a non-empty filename prefix
            return false;
        }

        // We're also safe if they are different directories
        return prefix.getDirectory().normalize().equals(experimentalDirectory.normalize());
    }
}
