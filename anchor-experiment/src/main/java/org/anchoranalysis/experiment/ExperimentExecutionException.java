/*-
 * #%L
 * anchor-experiment
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
package org.anchoranalysis.experiment;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

/**
 * An exception that occurs when an experiment is executed. This should always contain a friendly
 * error message to the user
 *
 * @author Owen Feehan
 */
public class ExperimentExecutionException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = -7030930961354624998L;

    public ExperimentExecutionException(String string) {
        super(string);
    }

    public ExperimentExecutionException(Throwable exc) {
        super(exc);
    }

    /**
     * A string message displayed to the user as well as a stack-trace of the cause
     *
     * @param string the error message displayed to the user
     * @param cause cause of the error, and a stack trace is displayed to theu ser
     */
    public ExperimentExecutionException(String string, Throwable cause) {
        super(string, cause);
    }
}
