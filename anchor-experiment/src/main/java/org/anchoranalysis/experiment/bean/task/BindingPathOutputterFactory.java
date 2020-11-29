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

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.manifest.operationrecorder.WriteOperationRecorder;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.path.prefixer.DirectoryWithPrefix;
import org.anchoranalysis.io.output.path.prefixer.NamedPath;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class BindingPathOutputterFactory {

    public static OutputterChecked createWithBindingPath(
            NamedPath path, Optional<Manifest> manifestTask, ParametersExperiment params)
            throws BindFailedException, JobExecutionException {
        try {
            DirectoryWithPrefix prefixToAssign =
                    new PrefixForInput(
                                    params.getPrefixer(),
                                    params.getExperimentArguments().createPrefixerContext())
                            .prefixForFile(
                                    path,
                                    params.getExperimentIdentifier(),
                                    params.getExperimentalManifest());

            // Initializes the manifest to be written
            manifestTask.ifPresent(recorder -> recorder.init(prefixToAssign.getDirectory()));

            OutputterChecked boundOutput =
                    params.getOutputter()
                            .getChecked()
                            .changePrefix(prefixToAssign, writeRecorder(manifestTask));

            if (params.getExperimentalManifest().isPresent()) {
                ManifestClashChecker.throwExceptionIfClashes(
                        params.getExperimentalManifest().get(), // NOSONAR
                        boundOutput,
                        path.getPath());
            }
            return boundOutput;

        } catch (PathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }

    private static Optional<WriteOperationRecorder> writeRecorder(Optional<Manifest> manifest) {
        return manifest.map(Manifest::getRootDirectory);
    }
}
