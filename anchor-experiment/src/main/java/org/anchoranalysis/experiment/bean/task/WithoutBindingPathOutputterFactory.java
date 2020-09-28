package org.anchoranalysis.experiment.bean.task;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WithoutBindingPathOutputterFactory {

    public static OutputterChecked createWithoutBindingPath(
            Optional<ManifestRecorder> manifestTask, Outputter outputter) {
        manifestTask.ifPresent(
                manifest -> {
                    manifest.init(outputter.getOutputFolderPath());
                    outputter.addOperationRecorder(manifest.getRootFolder());
                });
        return outputter.getChecked();
    }
}
