package org.anchoranalysis.core.log.error;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import org.anchoranalysis.core.log.MessageLogger;

/**
 * Records errors, by writing them into a logger.
 *
 * <p>Error is meant in the broad sense of any problem that occurred, not specifically Java's {@link
 * Error} class and sub-classes. This includes any {@link Exception} and its sub-classes.
 *
 * <p>Some formatting occurs (and sometimes adds a stacktrace) depending on context and
 * exception-type.
 *
 * @author Owen Feehan
 */
public class ErrorReporterIntoLog extends ErrorReporterBase {

    /**
     * Create for a message-logger.
     *
     * @param logger the message logger to record messages into.
     */
    public ErrorReporterIntoLog(MessageLogger logger) {
        super(logger::log);
    }
}
