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
                        new ErrorReporterForTask(loggerExperiment));

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
