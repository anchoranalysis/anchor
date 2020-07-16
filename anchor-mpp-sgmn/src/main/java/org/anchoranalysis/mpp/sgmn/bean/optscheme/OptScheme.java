/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.optscheme;

import org.anchoranalysis.anchor.mpp.feature.mark.ListUpdatableMarkSetCollection;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;
import org.anchoranalysis.mpp.sgmn.optscheme.OptSchemeContext;
import org.anchoranalysis.mpp.sgmn.optscheme.OptTerminatedEarlyException;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.FeedbackReceiver;

/**
 * An optimization-scheme that uses kernels to make changes to state
 *
 * <p>TODO replace updatableMarkSetCollection with a more generic type.
 *
 * @author Owen Feehan
 * @param <S> state returned from algorithm, and reported to the outside world
 * @param <U> type of kernel proposer
 */
public abstract class OptScheme<S, U> extends AnchorBean<OptScheme<S, U>> {

    /**
     * Finds an optimal state
     *
     * @param kernelProposer proposes kernels to change current state
     * @param updatableMarkSetCollection
     * @param feedbackReceiver gives feedback on ongoing state and changed as the optimziation
     *     occurs
     * @param context the context in which the scheme runs
     * @return
     * @throws OptTerminatedEarlyException
     */
    public abstract S findOpt(
            KernelProposer<U> kernelProposer,
            ListUpdatableMarkSetCollection updatableMarkSetCollection,
            FeedbackReceiver<S> feedbackReceiver,
            OptSchemeContext context)
            throws OptTerminatedEarlyException;
}
