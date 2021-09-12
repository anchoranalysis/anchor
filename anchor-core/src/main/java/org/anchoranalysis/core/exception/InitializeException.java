package org.anchoranalysis.core.exception;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

/**
 * An exception that occurs when initializing something, and it doesn't succeed
 *
 * @author Owen Feehan
 */
public class InitializeException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Creates with a message, but without a cause.
     *
     * @param message the message.
     */
    public InitializeException(String message) {
        super(message);
    }

    /**
     * Creates with a cause, but without a message.
     *
     * @param cause the cause.
     */
    public InitializeException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates with message and cause.
     *
     * @param message the message.
     * @param cause the cause.
     */
    public InitializeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Wraps exception in a {@link InitializeException}, unless it already its cause is a {@link
     * InitializeException}.
     *
     * @param exception the existing exception to consider wrapping.
     * @return the exception wrapped into a newly-created {@link InitializeException}, or the cause
     *     of {@code exception} if it's already a {@link InitializeException}.
     */
    public static InitializeException createOrReuse(Throwable exception) {
        // If it's an initialization error, we don't create a new one but re-throw
        //  as frequently initialization errors will pass through a recursive train
        if (exception.getCause() instanceof InitializeException) {
            return (InitializeException) exception.getCause();
        } else {
            return new InitializeException(exception);
        }
    }
}
