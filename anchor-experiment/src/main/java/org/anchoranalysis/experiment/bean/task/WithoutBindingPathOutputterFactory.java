package org.anchoranalysis.experiment.bean.task;

import java.util.Optional;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class WithoutBindingPathOutputterFactory {

    public static OutputterChecked createWithoutBindingPath(
            Optional<ManifestRecorder> manifestTask, Outputter outputter) {
        manifestTask.ifPresent(
                mt -> {
                    mt.init(outputter.getOutputFolderPath());
                    outputter.addOperationRecorder(mt.getRootFolder());
                });
        return outputter.getChecked();
    }
}
