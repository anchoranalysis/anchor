/* (C)2020 */
package org.anchoranalysis.experiment.task;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.bean.log.LoggingDestination;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * Parameters for executing a task, when the manifest, log etc. are still bound to the experiment
 *
 * @author Owen Feehan
 * @param <T> input-object type
 */
public class ParametersExperiment {

    // Parameters for all tasks in general (the experiment)
    @Getter private Optional<ManifestRecorder> experimentalManifest;

    @Getter private String experimentIdentifier;

    // This is a means to create new log-reporters for each task
    @Getter @Setter private LoggingDestination loggerTaskCreator;

    /**
     * Iff true, additional log messages are written to describe each job in terms of its unique
     * name, output folder, average execution time etc.
     */
    @Getter private boolean detailedLogging;

    @Getter private BoundContextSpecify context;

    public ParametersExperiment(
            ExperimentExecutionArguments experimentArguments,
            String experimentIdentifier,
            Optional<ManifestRecorder> experimentalManifest,
            BoundOutputManager outputManager,
            StatefulMessageLogger loggerExperiment,
            boolean detailedLogging) {
        this.context =
                new BoundContextSpecify(
                        experimentArguments,
                        wrapErrors(outputManager, loggerExperiment),
                        loggerExperiment,
                        new ErrorReporterIntoLog(loggerExperiment));

        this.experimentIdentifier = experimentIdentifier;
        this.experimentalManifest = experimentalManifest;
        this.detailedLogging = detailedLogging;
    }

    public BoundOutputManagerRouteErrors getOutputManager() {
        return context.getOutputManager();
    }

    public StatefulMessageLogger getLoggerExperiment() {
        return context.getStatefulLogReporter();
    }

    public ExperimentExecutionArguments getExperimentArguments() {
        return context.getExperimentArguments();
    }

    /** Redirects any output-errors into the log */
    private static BoundOutputManagerRouteErrors wrapErrors(
            BoundOutputManager rootOutputManagerNoErrors, MessageLogger logger) {
        return new BoundOutputManagerRouteErrors(
                rootOutputManagerNoErrors, new ErrorReporterIntoLog(logger));
    }
}
