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

import java.nio.file.Path;
import lombok.Getter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * This exists as an implementation of {@link InputOutputContext} that exposes a {@link
 * StatefulMessageLogger}.
 *
 * <p>As {@link StatefulMessageLogger} exists only in this package, and not in the package where
 * {@link InputOutputContext} is defined, the interface and class are deliberately separated.
 *
 * @author Owen Feehan
 */
public class InputOutputContextStateful implements InputOutputContext {

    /** User-supplied arguments that can further specify an experiment's execution. */
    @Getter private final ExecutionArguments executionArguments;

    private final Outputter outputter;

    private final StatefulMessageLogger messageLogger;
    private final ExecutionTimeRecorder executionTimeRecorder;
    private final Logger logger; // Always related to the above two fields

    /**
     * Creates with initialization arguments.
     *
     * @param executionArguments user-supplied arguments that can further specify an experiment's
     *     execution.
     * @param outputter where files are outputted to, and how that output occurs.
     * @param executionTimeRecorder records how long different aspects of an experiment take to
     *     execute.
     * @param logger where log messages written to.
     * @param errorReporter where errors are written to.
     */
    public InputOutputContextStateful(
            ExecutionArguments executionArguments,
            Outputter outputter,
            ExecutionTimeRecorder executionTimeRecorder,
            StatefulMessageLogger logger,
            ErrorReporter errorReporter) {
        this.executionArguments = executionArguments;
        this.outputter = outputter;
        this.executionTimeRecorder = executionTimeRecorder;
        this.messageLogger = logger;
        this.logger = new Logger(logger, errorReporter);
    }

    @Override
    public Path getModelDirectory() {
        return executionArguments.input().getModelDirectory();
    }

    @Override
    public boolean isDebugEnabled() {
        return executionArguments.isDebugModeEnabled();
    }

    @Override
    public Outputter getOutputter() {
        return outputter;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * Exposed as {@link StatefulMessageLogger} rather than as {@link MessageLogger} that is found
     * in {@link Logger}
     *
     * @return the logger.
     */
    public StatefulMessageLogger getMessageLogger() {
        return messageLogger;
    }

    @Override
    public ExecutionTimeRecorder getExecutionTimeRecorder() {
        return executionTimeRecorder;
    }
}
