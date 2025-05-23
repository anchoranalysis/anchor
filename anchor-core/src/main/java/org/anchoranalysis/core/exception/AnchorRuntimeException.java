package org.anchoranalysis.core.exception;

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

/**
 * A base class for all unchecked (runtime) exceptions used on the Anchor platform.
 *
 * @author Owen Feehan
 */
public abstract class AnchorRuntimeException extends RuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    /** Creates without message or cause. */
    protected AnchorRuntimeException() {
        super();
    }

    /**
     * Creates with message and cause.
     *
     * @param message the message.
     * @param cause the cause.
     */
    protected AnchorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates with only a message.
     *
     * @param message the message.
     */
    protected AnchorRuntimeException(String message) {
        super(message);
        super.initCause(null);
    }

    /**
     * Creates with only a cause.
     *
     * @param cause the cause.
     */
    protected AnchorRuntimeException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        if (getCause() != null) {
            return String.format("%s%n%s", super.toString(), getCause().toString());
        } else {
            return super.toString();
        }
    }
}
