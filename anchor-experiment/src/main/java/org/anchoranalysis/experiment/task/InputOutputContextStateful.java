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
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.log.error.ErrorReporter;
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

    private ExecutionArguments experimentArguments;
    private Outputter outputter;

    private StatefulMessageLogger messageLogger;
    private Logger logger; // Always related to the above two fields

    public InputOutputContextStateful(
            ExecutionArguments experimentArguments,
            Outputter outputter,
            StatefulMessageLogger logger,
            ErrorReporter errorReporter) {
        super();
        this.experimentArguments = experimentArguments;
        this.outputter = outputter;

        this.messageLogger = logger;
        this.logger = new Logger(logger, errorReporter);
    }

    @Override
    public Path getModelDirectory() {
        return experimentArguments.input().getModelDirectory();
    }

    @Override
    public boolean isDebugEnabled() {
        return experimentArguments.isDebugModeEnabled();
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
     */
    public StatefulMessageLogger getMessageLogger() {
        return messageLogger;
    }

    public ExecutionArguments getExperimentArguments() {
        return experimentArguments;
    }
}
