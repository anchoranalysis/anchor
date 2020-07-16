package org.anchoranalysis.core.error;

import org.anchoranalysis.core.error.combinable.AnchorCombinableException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;
import org.anchoranalysis.core.error.friendly.IFriendlyException;

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

public class CreateException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1842384434578361294L;

    public CreateException(String string) {
        super(string);
    }

    public CreateException(Throwable exc) {
        super(exc);
    }

    public CreateException(String message, IFriendlyException cause) {
        super(friendly(message, cause));
    }

    public CreateException(String message, AnchorCombinableException cause) {
        super(combine(message, cause));
    }

    private static String friendly(String message, IFriendlyException cause) {
        return String.format("%s: %s", message, cause.friendlyMessageHierarchy());
    }

    private static String combine(String message, AnchorCombinableException cause) {
        return String.format("%s: %s", message, cause.summarize());
    }
}
