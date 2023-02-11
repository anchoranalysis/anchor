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

import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.log.error.ErrorReporterIntoLog;

/**
 * An error-reporter that replaces particular errors/exceptions with user-friendly messages.
 *
 * @author Owen Feehan
 */
public class ErrorReporterForTask implements ErrorReporter {

    private ErrorReporter delegate;

    /**
     * Create to use a particular logger to report errors.
     *
     * @param logger the logger.
     */
    public ErrorReporterForTask(MessageLogger logger) {
        delegate = new ErrorReporterIntoLog(logger);
    }

    @Override
    public void recordError(Class<?> classOriginating, Throwable exc) {
        if (exc instanceof OutOfMemoryError) {
            // Special behaviour for out-of-memory errors
            delegate.recordError(
                    classOriginating,
                    "There is insufficient memory available for this task. Consider allocating more memory to the Java Virtual Machine with the -XmX argument.");
        } else {
            delegate.recordError(classOriginating, exc);
        }
    }

    @Override
    public void recordError(Class<?> classOriginating, String message) {
        delegate.recordError(classOriginating, message);
    }

    @Override
    public void recordError(Class<?> classOriginating, String message, Throwable exc) {
        delegate.recordError(classOriginating, message, exc);
    }

    @Override
    public void recordWarning(String message) {
        delegate.recordWarning(message);
    }

    @Override
    public boolean hasWarningOccurred() {
        return delegate.hasWarningOccurred();
    }
}
