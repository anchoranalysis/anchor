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
/* (C)2020 */
package org.anchoranalysis.bean.error;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

/**
 * An exception occurs when the duplication of a bean fails
 *
 * <p>We keep this unchecked, as if a bean is properly configured it should not be thrown.
 *
 * <p>As we already do checks to see if a bean is properly configured it should never (or almost
 * never) occur.
 *
 * <p>We don't want to make needlessly dirty code, as bean duplication occurs, so we keep it as a
 * runtime exception.
 *
 * @author Owen Feehan
 */
public class BeanDuplicateException extends AnchorFriendlyRuntimeException {

    /** */
    private static final long serialVersionUID = 1842384434578361294L;

    public BeanDuplicateException(String string) {
        super(string);
    }

    public BeanDuplicateException(Exception exc) {
        super(exc);
    }

    public BeanDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
