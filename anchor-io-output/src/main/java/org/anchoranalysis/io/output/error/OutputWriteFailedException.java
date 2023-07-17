/*-
 * #%L
 * anchor-io-output
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

package org.anchoranalysis.io.output.error;

import org.anchoranalysis.core.exception.combinable.AnchorCombinableException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;
import org.anchoranalysis.core.exception.friendly.HasFriendlyErrorMessage;

/**
 * When an output could not be successfully written to the file-system.
 *
 * @author Owen Feehan
 */
public class OutputWriteFailedException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1036971819028250342L;

    /**
     * Creates with a message only.
     *
     * @param message the message.
     */
    public OutputWriteFailedException(String message) {
        super(message);
    }

    /**
     * Creates with a cause only.
     *
     * @param cause the cause.
     */
    public OutputWriteFailedException(Throwable cause) {
        super("", cause);
    }

    /**
     * Creates with a message and a cause that provides a friendly error message.
     *
     * @param message the message (which is prefixed together with the friendly error message).
     * @param cause a cause that provides a friendly error message.
     */
    public OutputWriteFailedException(String message, HasFriendlyErrorMessage cause) {
        super(message + ": " + cause.friendlyMessageHierarchy(), null);
    }

    /**
     * Creates with a message and a cause that can be combined with others in summary form.
     *
     * @param message the message (which is prefixed together with the combiend error message).
     * @param cause a cause that provides a summary, when combined with others.
     */
    public OutputWriteFailedException(String message, AnchorCombinableException cause) {
        super(message, cause.summarize());
    }
}
