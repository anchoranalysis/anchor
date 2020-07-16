/* (C)2020 */
package org.anchoranalysis.experiment.bean.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.apache.commons.lang.time.StopWatch;

/**
 * Helps creating and outputting messages to the log for {@link
 * org.anchoranalysis.experiment.bean.io.OutputExperiment}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class OutputExperimentLogHelper {

    public static void maybeLogStart(ParametersExperiment params) {
        if (params.isDetailedLogging()) {
            params.getLoggerExperiment()
                    .logFormatted(
                            "Experiment %s started writing to %s",
                            params.getExperimentIdentifier(),
                            params.getOutputManager().getOutputFolderPath());
        }
    }

    public static void maybeLogCompleted(
            ParametersExperiment params, StopWatch stopWatchExperiment) {
        if (params.isDetailedLogging()) {
            params.getLoggerExperiment()
                    .logFormatted(
                            "Experiment %s completed (%ds) writing to %s",
                            params.getExperimentIdentifier(),
                            stopWatchExperiment.getTime() / 1000,
                            params.getOutputManager().getOutputFolderPath());
        }
    }
}
