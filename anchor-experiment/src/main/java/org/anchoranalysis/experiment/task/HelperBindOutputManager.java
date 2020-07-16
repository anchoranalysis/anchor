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
        return new PathWithDescription(
                input.pathForBinding().get(), input.descriptiveName()); // NOSONAR
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
                        params.getExperimentalManifest().get(),
                        boundOutput,
                        input.getPath()); // NOSONAR
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
