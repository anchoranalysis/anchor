/* (C)2020 */
package org.anchoranalysis.experiment.task;

import java.nio.file.Path;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

class BoundContextSpecify implements BoundIOContext {

    private ExperimentExecutionArguments experimentArguments;
    private BoundOutputManagerRouteErrors outputManager;

    private StatefulMessageLogger messageLogger;
    private Logger logger; // Always related to the above two fields

    public BoundContextSpecify(
            ExperimentExecutionArguments experimentArguments,
            BoundOutputManagerRouteErrors outputManager,
            StatefulMessageLogger logger,
            ErrorReporter errorReporter) {
        super();
        this.experimentArguments = experimentArguments;
        this.outputManager = outputManager;

        this.messageLogger = logger;
        this.logger = new Logger(logger, errorReporter);
    }

    @Override
    public Path getModelDirectory() {
        return experimentArguments.getModelDirectory();
    }

    @Override
    public boolean isDebugEnabled() {
        return experimentArguments.isDebugModeEnabled();
    }

    @Override
    public BoundOutputManagerRouteErrors getOutputManager() {
        return outputManager;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * Exposed as {@link StatefulMessageLogger} rather than as {@link MessageLogger} that is found
     * in {@link Logger}
     */
    public StatefulMessageLogger getStatefulLogReporter() {
        return messageLogger;
    }

    public ExperimentExecutionArguments getExperimentArguments() {
        return experimentArguments;
    }
}
