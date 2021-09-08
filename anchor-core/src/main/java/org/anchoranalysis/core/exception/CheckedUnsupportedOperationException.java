package org.anchoranalysis.core.exception;

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
 * When a particular operation is unsupported in particular circumstances.
 *
 * <p>In constrast to {@link UnsupportedOperationException} this is a checked, not a runtime
 * exception.
 *
 * @author Owen Feehan
 */
public class CheckedUnsupportedOperationException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = 7975790749088346594L;

    /** Creates with a default message indicating that the operation is unsupported. */
    public CheckedUnsupportedOperationException() {
        super("unsupported operation");
    }

    /**
     * Creates with a message explaining that the operation is unsupported.
     *
     * @param message the message.
     */
    public CheckedUnsupportedOperationException(String message) {
        super(message);
    }
}
