/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.log.error.ErrorReporterIntoLog;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
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
    @Getter private final ExecutionTimeRecorder executionTimeRecorder;

    public InputOutputContextStateful inputOutput(
            ExecutionArguments experimentArguments, OutputterChecked outputter) {
        return new InputOutputContextStateful(
                experimentArguments,
                wrapExceptions(outputter, loggerExperiment),
                executionTimeRecorder,
                loggerExperiment,
                new ErrorReporterForTask(loggerExperiment));
    }

    /** Redirects any output-exceptions into the log */
    private static Outputter wrapExceptions(
            OutputterChecked outputterChecked, MessageLogger logger) {
        return new Outputter(outputterChecked, new ErrorReporterIntoLog(logger));
    }
}
