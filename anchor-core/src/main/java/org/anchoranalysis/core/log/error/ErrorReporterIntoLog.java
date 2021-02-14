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

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.friendly.HasFriendlyErrorMessage;
import org.anchoranalysis.core.log.MessageLogger;
import org.apache.commons.lang.exception.ExceptionUtils;

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
@RequiredArgsConstructor
public class ErrorReporterIntoLog implements ErrorReporter {

    // START REQUIRED ARGUMENTS
    private final MessageLogger logger;
    // END REQUIRED ARGUMENTS
    
    /** True if at least one warning has been outputted. */
    private boolean warningOccurred = false;

    @Override
    public void recordError(Class<?> classOriginating, Throwable exc) {

        // Special behaviour if it's a friendly exception
        if (exc instanceof HasFriendlyErrorMessage) {
            HasFriendlyErrorMessage eCast = (HasFriendlyErrorMessage) exc;
            logWithBanner(eCast.friendlyMessageHierarchy(), false);
        } else {
            try {
                logWithBanner(
                        exc.toString()
                                + System.lineSeparator()
                                + ExceptionUtils.getFullStackTrace(exc),
                        classOriginating);
            } catch (Exception e) {
                logger.log("An error occurred while writing an error: " + e.toString());
            }
        }
    }

    @Override
    public void recordError(Class<?> classOriginating, String message) {
        try {
            logWithBanner(message, classOriginating);
        } catch (Exception e) {
            logger.log("An error occurred while writing an error: " + e.toString());
        }
    }
    

    @Override
    public void recordWarning(String message) {
        warningOccurred = true;
        try {
            logWithBanner(message, true);
        } catch (Exception e) {
            logger.log("An error occurred while writing an warning: " + e.toString());
        }
    }

    private void logWithBanner(String logMessage, boolean warning) {
        logger.log( DecorateMessage.decorate(logMessage, warning) );
    }

    private void logWithBanner(String logMessage, Class<?> classOriginating) {
        logger.log( DecorateMessage.decorate(logMessage + classMessage(classOriginating), false));
    }

    private static String classMessage(Class<?> c) {
        return String.format(
                "%nThe error occurred when executing a method in class %s", c.getName());
    }

    @Override
    public boolean hasWarningOccurred() {
        return warningOccurred;
    }
}
