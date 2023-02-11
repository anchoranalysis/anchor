/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.experiment.task;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.core.time.RecordedExecutionTimes;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.experiment.bean.log.LoggingDestination;
import org.anchoranalysis.experiment.io.InitializationContext;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.io.output.bean.path.prefixer.PathPrefixer;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

/**
 * Parameters for executing a task, when the log etc. are still bound to the experiment.
 *
 * @author Owen Feehan
 */
public class ParametersExperiment {

    /** User-supplied arguments that can further specify an experiment's execution. */
    private ExecutionArguments executionArguments;

    @Getter private final String experimentIdentifier;

    @Getter private final InputOutputContextStateful context;

    /** Determines prefixes used in the paths for outputted files. */
    @Getter private final PathPrefixer prefixer;

    /** This is a means to create new log-reporters for each task. */
    @Getter @Setter private LoggingDestination loggerTaskCreator;

    /**
     * Iff true, additional log messages are written to describe each job in terms of its unique
     * name, output folder, average execution time etc.
     */
    @Getter private final boolean detailedLogging;

    /** Allows execution-time for particular operations to be recorded. */
    @Getter private ExecutionTimeRecorder executionTimeRecorder;

    /**
     * Creates with initialization arguments.
     *
     * @param executionArguments user-supplied arguments that can further specify an experiment's
     *     execution.
     * @param experimentIdentifier uniquely identifies an experiment.
     * @param outputter where files are outputted to, and how that output occurs.
     * @param prefixer determines prefixes used in the paths for outputted files.
     * @param feedbackContext allows execution-time for particular operations to be recorded.
     */
    public ParametersExperiment(
            ExecutionArguments executionArguments,
            String experimentIdentifier,
            OutputterChecked outputter,
            PathPrefixer prefixer,
            ExperimentFeedbackContext feedbackContext) {
        this.executionTimeRecorder = feedbackContext.getExecutionTimeRecorder();
        this.executionArguments = executionArguments;
        this.context = feedbackContext.inputOutput(executionArguments, outputter);
        this.experimentIdentifier = experimentIdentifier;
        this.detailedLogging = feedbackContext.isDetailedLogging();
        this.prefixer = prefixer;
    }

    /**
     * The experiment-identifier to use in the output-path, if one should be used.
     *
     * @return the identifier, or {@link Optional#empty} to exclude from output path.
     */
    public Optional<String> experimentIdentifierForOutputPath() {
        if (!executionArguments.output().getPrefixer().isOmitExperimentIdentifier()) {
            return Optional.of(experimentIdentifier);
        } else {
            return Optional.empty();
        }
    }

    /**
     * An outputter that writes to the particular output-directory.
     *
     * @return the outputter.
     */
    public Outputter getOutputter() {
        return context.getOutputter();
    }

    /**
     * The message-logger used for experiment-wide messages.
     *
     * @return the logger.
     */
    public StatefulMessageLogger getLoggerExperiment() {
        return context.getMessageLogger();
    }

    /**
     * User-supplied arguments that can further specify an experiment's execution.
     *
     * @return the arguments.
     */
    public ExecutionArguments getExecutionArguments() {
        return context.getExecutionArguments();
    }

    /**
     * Derives an {@link InitializationContext} from the parameters.
     *
     * @return a newly created {@link InitializationContext}.
     */
    public InitializationContext deriveInitializationContext() {
        return new InitializationContext(context, context.getExecutionArguments().task().getSize());
    }

    /**
     * The execution-times that have been recorded.
     *
     * @return newly created {@link RecordedExecutionTimes} that describes the execution-times.
     */
    public RecordedExecutionTimes executionTimeStatistics() {
        return executionTimeRecorder.recordedTimes();
    }
}
