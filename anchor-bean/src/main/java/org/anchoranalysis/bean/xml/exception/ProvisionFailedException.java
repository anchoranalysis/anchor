/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.bean.xml.exception;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;

/**
 * If provisioning an object fails.
 *
 * <p>Provisioning is deliberately ambiguous term that can refer to either creating a new object or
 * retrieving an existing object from a cache, either of which can cause failure.
 *
 * @author Owen Feehan
 */
public class ProvisionFailedException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Creates with only a message.
     *
     * @param message the message.
     */
    public ProvisionFailedException(String message) {
        super(message);
    }

    /**
     * Creates with only a cause.
     *
     * @param cause the cause.
     */
    public ProvisionFailedException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates with both a message and cause.
     *
     * @param message the message.
     * @param cause the cause.
     */
    public ProvisionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
