package org.anchoranalysis.experiment.bean.task;

import java.util.Optional;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.io.bean.filepath.prefixer.NamedPath;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.operationrecorder.NullWriteOperationRecorder;
import org.anchoranalysis.io.manifest.operationrecorder.WriteOperationRecorder;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class BindingPathOutputterFactory {

    public static OutputterChecked createWithBindingPath(
            NamedPath path,
            Optional<ManifestRecorder> manifestTask,
            ParametersExperiment params)
            throws BindFailedException, JobExecutionException {
        try {
            FilePathPrefix prefixToAssign = new PrefixForInput(params.getPrefixer(), params.getExperimentArguments().createPrefixerContext()).prefixForFile(
                    path,
                    params.getExperimentIdentifier(),
                    params.getExperimentalManifest()
                    );
    
            // Initializes the manifest to be written
            manifestTask.ifPresent(recorder -> recorder.init(prefixToAssign.getFolderPath()));
            
            OutputterChecked boundOutput = params.getOutputter().getChecked().changePrefix(prefixToAssign, writeRecorder(manifestTask));
            
            if (params.getExperimentalManifest().isPresent()) {
                ManifestClashChecker.throwExceptionIfClashes(
                        params.getExperimentalManifest().get(), // NOSONAR
                        boundOutput,
                        path.getPath());
            }
            return boundOutput;
            
        } catch (FilePathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }
    
    private static WriteOperationRecorder writeRecorder(
            Optional<ManifestRecorder> manifestRecorder) {
        Optional<WriteOperationRecorder> opt =
                manifestRecorder.map(ManifestRecorder::getRootFolder);
        return opt.orElse(new NullWriteOperationRecorder());
    }
}
