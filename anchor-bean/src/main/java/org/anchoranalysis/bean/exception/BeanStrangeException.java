/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.exception;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;

/**
 * When something quite strange happens that we don't usually want to catch.
 *
 * <p>This type of "black swan" exception is for errors where we aren't going to invest much effort
 * in handling, unless it starts becoming thrown regularly.
 *
 * @author Owen Feehan
 */
public class BeanStrangeException extends AnchorFriendlyRuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Creates with a message only.
     *
     * @param message the message.
     */
    public BeanStrangeException(String message) {
        super(message);
    }

    /**
     * Creates with a message and cause.
     *
     * @param message the message.
     * @param cause the cause.
     */
    public BeanStrangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
