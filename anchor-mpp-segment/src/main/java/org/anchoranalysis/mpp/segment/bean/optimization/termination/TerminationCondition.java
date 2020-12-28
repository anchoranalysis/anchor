/*-
 * #%L
 * anchor-mpp-sgmn
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

package org.anchoranalysis.mpp.segment.bean.optimization.termination;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.log.MessageLogger;

/**
 * A condition on whether to terminate the optimization or continue with further steps.
 *
 * @author Owen Feehan
 */
public abstract class TerminationCondition extends AnchorBean<TerminationCondition> {

    /**
     * Whether to continue for an additional iteration step or not.
     * 
     * @param iteration the current iteration of the optimization.
     * @param score the current score associated with the optimization
     * @param size the current size associated with the optimization.
     * @param logger a logger to write messages to
     * @return true iff a continuation should occur.
     */
    public abstract boolean continueFurther(
            int iteration, double score, int size, MessageLogger logger);

    public void init() {}
}
