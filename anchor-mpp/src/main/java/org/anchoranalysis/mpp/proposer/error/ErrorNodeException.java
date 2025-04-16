/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.proposer.error;

import org.anchoranalysis.core.exception.AnchorCheckedException;

/**
 * An exception that adds a string to the current error node.
 *
 * <p>This exception is used to propagate errors while also allowing them to be added to an
 * ErrorNode.
 */
public class ErrorNodeException extends AnchorCheckedException {

    private static final long serialVersionUID = -4000166470139114311L;

    /** The error message associated with this exception. */
    private final String str;

    /**
     * Constructs a new ErrorNodeException with the specified error message.
     *
     * @param str the error message
     */
    public ErrorNodeException(String str) {
        super(str);
        this.str = str;
    }

    /**
     * Constructs a new ErrorNodeException from another exception.
     *
     * @param e the exception to convert into an ErrorNodeException
     */
    public ErrorNodeException(Exception e) {
        super(e.toString());
        this.str = e.toString();
    }

    /**
     * Adds the error message associated with this exception to an ErrorNode.
     *
     * @param errorNode the ErrorNode to which the error message should be added
     */
    public void addToErrorNode(ErrorNode errorNode) {
        errorNode.add(str);
    }
}
