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

package org.anchoranalysis.mpp.mark;

import org.anchoranalysis.core.exception.AnchorCheckedException;

/**
 * Exception thrown when an unsupported mark type is encountered.
 * 
 * <p>This exception is used to indicate that a particular type of mark
 * is not supported in the current context or operation.</p>
 */
public class UnsupportedMarkTypeException extends AnchorCheckedException {

    private static final long serialVersionUID = -5855318973518035098L;

    /**
     * Constructs a new UnsupportedMarkTypeException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     */
    public UnsupportedMarkTypeException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnsupportedMarkTypeException with the specified cause.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public UnsupportedMarkTypeException(Exception cause) {
        super(cause);
    }
}