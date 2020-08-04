package org.anchoranalysis.core.error.friendly;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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
 * Categorizes different types of exceptions
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class ExceptionTypes {

    /** Is it the last exception in the chain? */
    public static boolean isFinal(Throwable exc) {
        return exc.getCause() == null || exc.getCause() == exc;
    }

    /** Is it the last exception in the chain? */
    public static boolean isFriendly(Throwable exc) {
        return exc instanceof HasFriendlyErrorMessage;
    }

    /** Does the exception lack a message? */
    public static boolean hasEmptyMessage(Throwable exc) {
        return exc.getMessage() == null || exc.getMessage().isEmpty();
    }
}
