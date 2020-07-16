/* (C)2020 */
package org.anchoranalysis.experiment.bean.log;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.ConsoleMessageLogger;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * The destination(s) to which log-messages are sent.
 *
 * @author Owen Feehan
 */
public abstract class LoggingDestination extends AnchorBean<LoggingDestination> {

    /**
     * Creates a logger for this destination - and if anything goes wrong reporting fallback into
     * the console.
     *
     * <p>Identical to {@link LoggingDestination#createWithLogFallback} but uses a {@link
     * ConsoleReporter} as the {@code fallbackErrorReporter}.
     *
     * @param outputManager the output-manager
     * @param arguments experiment-arguments
     * @param detailedLogging whether detailed logging should occur in this reporter, or a less
     *     detailed version
     * @return a newly created log-reporter
     */
    public StatefulMessageLogger createWithConsoleFallback(
            BoundOutputManager outputManager,
            ExperimentExecutionArguments arguments,
            boolean detailedLogging) {
        return createWithLogFallback(
                outputManager, new ConsoleMessageLogger(), arguments, detailedLogging);
    }

    /**
     * Creates a logger for this destination - and if anything goes wrong reporting fallback into a
     * log.
     *
     * @param outputManager the output-manager
     * @param fallbackErrorReporter where any errors are reported, when trying to create this log.
     * @param arguments experiment-arguments
     * @param detailedLogging whether detailed logging should occur in this reporter, or a less
     *     detailed version
     * @return a newly created log-reporter
     */
    public StatefulMessageLogger createWithLogFallback(
            BoundOutputManager outputManager,
            MessageLogger fallbackErrorReporter,
            ExperimentExecutionArguments arguments,
            boolean detailedLogging) {
        return create(
                outputManager,
                new ErrorReporterIntoLog(fallbackErrorReporter),
                arguments,
                detailedLogging);
    }

    /**
     * Creates a logger for this destination
     *
     * @param outputManager the output-manager
     * @param fallbackErrorReporter where any errors are reported, when trying to create this log.
     * @param arguments experiment-arguments
     * @param detailedLogging whether detailed logging should occur in this reporter, or a less
     *     detailed version
     * @return a newly created log-reporter
     */
    public abstract StatefulMessageLogger create(
            BoundOutputManager outputManager,
            ErrorReporter fallbackErrorReporter,
            ExperimentExecutionArguments arguments,
            boolean detailedLogging);
}
