/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.core.time;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.log.Logger;

/**
 * Context objects for general operations that allow for logging and recording execution time.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class OperationContext {

    /** Allows for the execution time of certain operations to be recorded. */
    private final ExecutionTimeRecorder executionTimeRecorder;

    /**
     * Where to write informative messages to, and and any non-fatal errors (fatal errors are throw
     * as exceptions).
     */
    private final Logger logger;

    /**
     * Creates with a {@link Logger} ignoring execution-times.
     *
     * @param logger the logger.
     */
    public OperationContext(Logger logger) {
        this(ExecutionTimeRecorderIgnore.instance(), logger);
    }
}
