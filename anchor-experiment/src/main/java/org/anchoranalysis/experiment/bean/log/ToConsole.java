/* (C)2020 */
package org.anchoranalysis.experiment.bean.log;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.ConsoleMessageLogger;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Logs messages to the console.
 *
 * @author Owen Feehan
 */
public class ToConsole extends LoggingDestination {

    @Override
    public StatefulMessageLogger create(
            BoundOutputManager outputManager,
            ErrorReporter errorReporter,
            ExperimentExecutionArguments arguments,
            boolean detailedLogging) {
        return new ConsoleMessageLogger();
    }
}
