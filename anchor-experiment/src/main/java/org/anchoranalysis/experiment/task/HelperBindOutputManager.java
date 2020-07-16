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
/* (C)2020 */
package org.anchoranalysis.experiment.task;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.io.bean.filepath.prefixer.PathWithDescription;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BindFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperBindOutputManager {

    // If pathForBinding is null, we bind to the root folder instead
    public static BoundOutputManager createOutputManagerForTask(
            InputFromManager input,
            Optional<ManifestRecorder> manifestTask,
            ParametersExperiment params)
            throws JobExecutionException {
        try {
            Optional<Path> pathForBinding = input.pathForBinding();
            if (pathForBinding.isPresent()) {
                return createWithBindingPath(
                        derivePathWithDescription(input), manifestTask, params);
            } else {
                return createWithoutBindingPath(manifestTask, params.getOutputManager());
            }
        } catch (BindFailedException e) {
            throw new JobExecutionException(
                    String.format(
                            "Cannot bind the outputManager to the specific task with pathForBinding=%s and experimentIdentifier='%s'",
                            describeInputForBinding(input), params.getExperimentIdentifier()),
                    e);
        }
    }

    private static PathWithDescription derivePathWithDescription(InputFromManager input) {
        assert (input.pathForBinding().isPresent());
        return new PathWithDescription(input.pathForBinding().get(), input.descriptiveName());
    }

    private static BoundOutputManager createWithBindingPath(
            PathWithDescription input,
            Optional<ManifestRecorder> manifestTask,
            ParametersExperiment params)
            throws BindFailedException, JobExecutionException {
        try {
            BoundOutputManager boundOutput =
                    params.getOutputManager()
                            .deriveFromInput(
                                    input,
                                    params.getExperimentIdentifier(),
                                    manifestTask,
                                    params.getExperimentalManifest(),
                                    params.getExperimentArguments().createParamsContext());
            if (params.getExperimentalManifest().isPresent()) {
                ManifestClashChecker.throwExceptionIfClashes(
                        params.getExperimentalManifest().get(), boundOutput, input.getPath());
            }
            return boundOutput;
        } catch (FilePathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }

    private static BoundOutputManager createWithoutBindingPath(
            Optional<ManifestRecorder> manifestTask, BoundOutputManagerRouteErrors outputManager) {
        manifestTask.ifPresent(
                mt -> {
                    mt.init(outputManager.getOutputFolderPath());
                    outputManager.addOperationRecorder(mt.getRootFolder());
                });
        return outputManager.getDelegate();
    }

    private static String describeInputForBinding(InputFromManager input) {
        return input.pathForBinding()
                .map(HelperBindOutputManager::quoteString)
                .orElse("<no binding path>");
    }

    private static String quoteString(Path path) {
        return String.format("'%s'", path.toString());
    }
}
