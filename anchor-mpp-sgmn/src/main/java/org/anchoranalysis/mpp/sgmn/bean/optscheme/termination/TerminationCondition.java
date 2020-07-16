/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.optscheme.termination;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.log.MessageLogger;

/**
 * A test on whether to terminate the optimization or not
 *
 * @author Owen Feehan
 */
public abstract class TerminationCondition extends AnchorBean<TerminationCondition> {

    public abstract boolean continueIterations(
            int crntIter, double score, int size, MessageLogger logger);

    public void init() {}
}
