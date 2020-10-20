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
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.log.error.ErrorReporterIntoLog;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.bean.log.LoggingDestination;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.path.PathPrefixer;

/**
 * Parameters for executing a task, when the manifest, log etc. are still bound to the experiment
 *
 * @author Owen Feehan
 */
public class ParametersExperiment {

    // Parameters for all tasks in general (the experiment)
    @Getter private final Optional<Manifest> experimentalManifest;

    @Getter private final String experimentIdentifier;

    /**
     * Iff true, additional log messages are written to describe each job in terms of its unique
     * name, output folder, average execution time etc.
     */
    @Getter private final boolean detailedLogging;

    @Getter private final InputOutputContextStateful context;

    /** The {@link OutputManager} associated with the experiment which {@link Outputter} uses. */
    @Getter private final PathPrefixer prefixer;

    // This is a means to create new log-reporters for each task
    @Getter @Setter private LoggingDestination loggerTaskCreator;

    public ParametersExperiment(
            ExperimentExecutionArguments experimentArguments,
            String experimentIdentifier,
            Optional<Manifest> experimentalManifest,
            OutputterChecked outputter,
            PathPrefixer prefixer,
            StatefulMessageLogger loggerExperiment,
            boolean detailedLogging) {
        this.context =
                new InputOutputContextStateful(
                        experimentArguments,
                        wrapExceptions(outputter, loggerExperiment),
                        loggerExperiment,
                        new ErrorReporterForTask(loggerExperiment));

        this.experimentIdentifier = experimentIdentifier;
        this.experimentalManifest = experimentalManifest;
        this.detailedLogging = detailedLogging;
        this.prefixer = prefixer;
    }

    public Outputter getOutputter() {
        return context.getOutputter();
    }

    public StatefulMessageLogger getLoggerExperiment() {
        return context.getMessageLogger();
    }

    public ExperimentExecutionArguments getExperimentArguments() {
        return context.getExperimentArguments();
    }

    /** Redirects any output-exceptions into the log */
    private static Outputter wrapExceptions(
            OutputterChecked outputterChecked, MessageLogger logger) {
        return new Outputter(outputterChecked, new ErrorReporterIntoLog(logger));
    }
}
