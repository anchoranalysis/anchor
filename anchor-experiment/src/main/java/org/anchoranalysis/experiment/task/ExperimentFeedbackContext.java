package org.anchoranalysis.experiment.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.log.error.ErrorReporterIntoLog;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Objects to give the user feedback about different aspects of the experiment.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ExperimentFeedbackContext {

    /** The logger associated with the experiment. */
    @Getter private final StatefulMessageLogger loggerExperiment;

    /**
     * Iff true, additional log messages are written to describe each job in terms of its unique
     * name, output folder, average execution time etc.
     */
    @Getter private final boolean detailedLogging;

    /** Allows execution-time for particular operations to be recorded. */
    @Getter private final ExecutionTimeStatistics executionTimeStatistics;

    public InputOutputContextStateful inputOutput(
            ExecutionArguments experimentArguments, OutputterChecked outputter) {
        return new InputOutputContextStateful(
                experimentArguments,
                wrapExceptions(outputter, loggerExperiment),
                executionTimeStatistics,
                loggerExperiment,
                new ErrorReporterForTask(loggerExperiment));
    }

    /** Redirects any output-exceptions into the log */
    private static Outputter wrapExceptions(
            OutputterChecked outputterChecked, MessageLogger logger) {
        return new Outputter(outputterChecked, new ErrorReporterIntoLog(logger));
    }
}
