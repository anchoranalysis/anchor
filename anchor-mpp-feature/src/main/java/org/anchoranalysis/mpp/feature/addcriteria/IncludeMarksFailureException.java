/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.mpp.feature.addcriteria;

import org.anchoranalysis.core.exception.AnchorCheckedException;

/**
 * Exception thrown when there's a failure in including marks during the add criteria process.
 *
 * <p>This exception extends {@link AnchorCheckedException} and is used specifically in the context
 * of mark inclusion operations.
 */
public class IncludeMarksFailureException extends AnchorCheckedException {

    /** Serialization version UID. */
    private static final long serialVersionUID = -4475636830799035389L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link
     *     #getMessage()} method)
     */
    public IncludeMarksFailureException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public IncludeMarksFailureException(Exception cause) {
        super(cause);
    }
}
