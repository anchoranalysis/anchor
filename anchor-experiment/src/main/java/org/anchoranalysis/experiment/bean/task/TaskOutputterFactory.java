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
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.path.prefixer.DirectoryWithPrefix;
import org.anchoranalysis.io.output.path.prefixer.NamedPath;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerException;

/**
 * Helps creates {@link OutputterChecked} for a task.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TaskOutputterFactory {

    /**
     * Creates a {@link OutputterChecked} for a task.
     *
     * <p>If pathForBinding is undefined, then output occurs to experiment's root folder instead.
     *
     * @param input the input for the task.
     * @param parameters the parameters that define the experiment.
     * @return a newly created outputter.
     * @throws JobExecutionException if unable to bind the outputter to the output-directory.
     */
    public static OutputterChecked createOutputterForTask(
            InputFromManager input, ParametersExperiment parameters) throws JobExecutionException {
        try {
            Optional<Path> pathForBinding = input.pathForBinding();
            if (pathForBinding.isPresent()) {
                return createWithBindingPath(derivePathWithDescription(input), parameters);
            } else {
                return parameters.getOutputter().getChecked();
            }
        } catch (BindFailedException e) {
            throw new JobExecutionException(
                    String.format(
                            "Cannot bind an outputter for the specific task with pathForBinding=%s and experimentIdentifier='%s'",
                            describeInputForBinding(input), parameters.getExperimentIdentifier()),
                    e);
        }
    }

    private static NamedPath derivePathWithDescription(InputFromManager input) {
        return new NamedPath(input.identifier(), input.pathForBinding().get()); // NOSONAR
    }

    private static String describeInputForBinding(InputFromManager input) {
        return input.pathForBinding()
                .map(TaskOutputterFactory::quoteString)
                .orElse("<no binding path>");
    }

    private static String quoteString(Path path) {
        return String.format("'%s'", path.toString());
    }

    private static OutputterChecked createWithBindingPath(
            NamedPath path, ParametersExperiment parameters) throws BindFailedException {
        try {
            DirectoryWithPrefix prefixToAssign =
                    parameters
                            .getPrefixer()
                            .outFilePrefix(
                                    path,
                                    parameters.experimentIdentifierForOutputPath(),
                                    parameters.getExecutionArguments().derivePathPrefixerContext());

            return parameters.getOutputter().getChecked().changePrefix(prefixToAssign);

        } catch (PathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }
}
