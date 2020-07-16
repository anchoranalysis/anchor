/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.optscheme.termination;

import org.anchoranalysis.core.log.MessageLogger;

public class TriggerTerminationCondition extends TerminationCondition {

    private boolean triggered = false;

    // We don't bother with synchronize

    @Override
    public boolean continueIterations(int crntIter, double score, int size, MessageLogger logger) {
        return !triggered;
    }

    public void trigger() {
        triggered = true;
    }
}
