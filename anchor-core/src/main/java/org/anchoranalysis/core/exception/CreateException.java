package org.anchoranalysis.core.exception;

import org.anchoranalysis.core.exception.combinable.AnchorCombinableException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;
import org.anchoranalysis.core.exception.friendly.HasFriendlyErrorMessage;

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
 * A generic exception that be thrown when the creation of a new object fails.
 *
 * @author Owen Feehan
 */
public class CreateException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1842384434578361294L;

    /**
     * Creates with a message, but without a cause.
     *
     * @param message the message.
     */
    public CreateException(String message) {
        super(message);
    }

    /**
     * Creates with a cause, but without a message.
     *
     * @param cause the cause.
     */
    public CreateException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates with a message, and a cause with a user-friendly error message.
     *
     * @param message the message
     * @param cause the cause with a user-friendly error message.
     */
    public CreateException(String message, HasFriendlyErrorMessage cause) {
        super(friendly(message, cause));
    }

    /**
     * Creates with a message, and a combinable-exception as a cause.
     *
     * @param message the message
     * @param cause a combinable-exception as a cause.
     */
    public CreateException(String message, AnchorCombinableException cause) {
        super(combine(message, cause));
    }

    private static String friendly(String message, HasFriendlyErrorMessage cause) {
        return String.format("%s: %s", message, cause.friendlyMessageHierarchy());
    }

    private static String combine(String message, AnchorCombinableException cause) {
        return String.format("%s: %s", message, cause.summarize());
    }
}
