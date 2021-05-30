/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.experiment.bean.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.input.InputsWithDirectory;
import org.anchoranalysis.io.input.file.NamedFile;

/**
 * Routines to find non-input files that will be copied to the output-directory at the end of an
 * experiment.
 *
 * <p>The copying operation is delayed until the end of the experiment in case any fatal errors
 * occur when running the experiment.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CopyNonInputs {

    /**
     * Determines, if enabled, the non-input files that will be copied to the output-directory after
     * the experiment.
     *
     * @param inputs the inputs for the experiment
     * @param params the parameters for the experiment
     * @param <T> input-type
     * @return if enabled, the collection of non-input files to copy (with the relative-path to the
     *     input-directory as an identifier), otherwise {@link Optional#empty}.
     * @throws ExperimentExecutionException if the input-directory is unknown
     */
    public static <T extends InputFromManager> Optional<Collection<NamedFile>> prepare(
            InputsWithDirectory<T> inputs, ParametersExperiment params)
            throws ExperimentExecutionException {
        if (params.getExperimentArguments().input().isCopyNonInputs()) {

            if (inputs.directory().isPresent()) {
                try {
                    Collection<NamedFile> files = inputs.findAllNonInputFiles();
                    params.getLoggerExperiment()
                            .logFormatted(
                                    "Preparing %d non-input files to copy to the output directory at end of experiment.",
                                    files.size());
                    return Optional.of(files);
                } catch (OperationFailedException e) {
                    throw new AnchorImpossibleSituationException();
                }
            } else {
                throw new ExperimentExecutionException(
                        "Cannot determine non-input files to copy, as no input directory is known.");
            }

        } else {
            return Optional.empty();
        }
    }

    /**
     * Copy any non-inputs (if they exist) to the output-directory.
     *
     * <p>Any existing file in the output directory with the destination path for copying will be
     * overwritten.
     *
     * @param nonInputs the files to copy to the output directory (where the identifier is a
     *     relative-path to use for copying into the output-directory)
     * @param params the parameters for the experiment
     * @throws ExperimentExecutionException if any IO errors occurs copying a file.
     */
    public static void copy(Collection<NamedFile> nonInputs, ParametersExperiment params)
            throws ExperimentExecutionException {
        params.getLoggerExperiment()
                .logFormatted(
                        "Copying %d non-input files to the output directory.", nonInputs.size());

        Path outputDirectory = params.getOutputter().getOutputDirectory();

        for (NamedFile toCopy : nonInputs) {
            try {
                Path source = toCopy.getFile().toPath();
                Path destination = destinationPath(outputDirectory, toCopy.getIdentifier(), params);
                copyMakeDirectories(source, destination);
            } catch (IOException e) {
                throw new ExperimentExecutionException(
                        String.format("Failed to copy non-input file: %s", toCopy.getPath()), e);
            }
        }
    }
    
    private static Path destinationPath(Path outputDirectory, String identifier, ParametersExperiment params) {
        return outputDirectory.resolve(
            params.getExperimentArguments().output().getPrefixer().maybeSuppressDirectories(identifier, false)
        );
    }

    /** Copy a file making any necessary subdirectories in the destination path. */
    private static void copyMakeDirectories(Path source, Path destination) throws IOException {
        destination.toFile().mkdirs();
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }
}
