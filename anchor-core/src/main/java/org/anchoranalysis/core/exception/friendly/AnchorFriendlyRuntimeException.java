package org.anchoranalysis.core.exception.friendly;

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

import java.io.IOException;
import java.io.Writer;
import org.anchoranalysis.core.exception.AnchorRuntimeException;

/**
 * A base class for particular runtime-exceptions which are guaranteed to provide a user-friendly
 * error description. See the counterpart AnchorFriendlyCheckedException.
 *
 * @author Owen Feehan
 */
public class AnchorFriendlyRuntimeException extends AnchorRuntimeException
        implements HasFriendlyErrorMessage {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * A user-friendly error message and its cause. The message is displayed to the user.
     *
     * @param message a user-friendly error message to display to the user indicating the problem
     * @param cause the cause of the error
     */
    public AnchorFriendlyRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * A user-friendly error message WITHOUT a cause. This exception is displayed to the user. There
     * are no more nested-errors possible.
     *
     * @param message cause the cause of the error
     */
    public AnchorFriendlyRuntimeException(String message) {
        super(message);
    }

    /**
     * A wrapped exception. We skip it in error reports
     *
     * @param cause the cause of the error
     */
    public AnchorFriendlyRuntimeException(Throwable cause) {
        // We pass an empty message, so that future error message skip it
        // If we simply use the super(cause) constructor, a message will be assigned from the
        // exceptions .toString method
        super("", cause);
    }

    @Override
    public String friendlyMessage() {
        return HelperFriendlyFormatting.friendlyMessageNonHierarchical(this);
    }

    @Override
    public String friendlyMessageHierarchy() {
        return HelperFriendlyFormatting.friendlyMessageHierarchy(this);
    }

    @Override
    public void friendlyMessageHierarchy(
            Writer writer, int wordWrapLimit, boolean showExceptionNames) throws IOException {
        HelperFriendlyFormatting.friendlyMessageHierarchical(
                this, writer, wordWrapLimit, showExceptionNames);
    }
}
