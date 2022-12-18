/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.io.input.bean;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.io.input.InputContextParameters;

/**
 * Parameters passed to an {@link InputManager} to generate input-objects.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class InputManagerParameters {

    /** Additional parameters that offer context for many beans that provide input-functions. */
    @Getter private final InputContextParameters inputContext;

    /** Allows for logging and recording the execution-time of particular operations. */
    @Getter private final OperationContext operationContext;

    /**
     * Create with only a logger, and using sensible default values for the other fields.
     *
     * @param logger the logger.
     */
    public InputManagerParameters(Logger logger) {
        this(new OperationContext(logger));
    }

    /**
     * Create with only a {@link OperationContext}, and using sensible default values for the other
     * fields.
     *
     * @param operationContext context for logging and recording execution times.
     */
    public InputManagerParameters(OperationContext operationContext) {
        this(new InputContextParameters(), operationContext);
    }

    /**
     * Whether debug-mode has been activated.
     *
     * @return true iff debug-mode has been activated.
     */
    public boolean isDebugModeActivated() {
        return inputContext.getDebugModeParameters().isPresent();
    }

    /**
     * Parameters for debug-mode (only defined if we are in debug mode).
     *
     * @return the parameters, if they exist.
     */
    public Optional<DebugModeParameters> getDebugModeParameters() {
        return inputContext.getDebugModeParameters();
    }

    /**
     * Allows for the execution time of certain operations to be recorded.
     *
     * @return the execution-time-recorder.
     */
    public ExecutionTimeRecorder getExecutionTimeRecorder() {
        return operationContext.getExecutionTimeRecorder();
    }

    /**
     * Where to write informative messages to, and and any non-fatal errors (fatal errors are throw
     * as exceptions).
     *
     * @return the logger.
     */
    public Logger getLogger() {
        return operationContext.getLogger();
    }
}
