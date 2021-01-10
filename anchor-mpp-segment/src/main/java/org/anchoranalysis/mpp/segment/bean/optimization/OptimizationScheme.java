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

package org.anchoranalysis.mpp.segment.bean.optimization;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.mpp.segment.bean.kernel.proposer.KernelProposer;
import org.anchoranalysis.mpp.segment.optimization.OptimizationContext;
import org.anchoranalysis.mpp.segment.optimization.OptimizationTerminatedEarlyException;
import org.anchoranalysis.mpp.segment.optimization.feedback.FeedbackReceiver;

/**
 * An optimization-scheme that uses kernels to make changes to state.
 *
 * @author Owen Feehan
 * @param <S> state returned from algorithm, and reported to the outside world
 * @param <U> type of kernel proposer
 * @param <V> updatable-state
 */
public abstract class OptimizationScheme<S, U, V> extends AnchorBean<OptimizationScheme<S, U, V>> {

    /**
     * Finds an optimal state using kernel proposals.
     *
     * @param proposer proposes kernels to change current state
     * @param marks
     * @param feedback gives feedback on ongoing state and changed as the optimziation occurs
     * @param context the context in which the scheme runs
     * @return
     * @throws OptimizationTerminatedEarlyException
     */
    public abstract S findOptimum(
            KernelProposer<U, V> proposer,
            V marks,
            FeedbackReceiver<S> feedback,
            OptimizationContext context)
            throws OptimizationTerminatedEarlyException;
}
