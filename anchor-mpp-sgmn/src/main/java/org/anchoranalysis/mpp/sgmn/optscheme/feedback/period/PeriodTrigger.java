/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback.period;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

class PeriodTrigger<S, T extends PeriodReceiver<S>> {

    private T periodReceiver;

    // How often we output in practice
    private int aggInterval;

    // Running counter of iterations
    private int counter = 0;

    private boolean nextStart = false;

    // Constructor
    public PeriodTrigger(T periodReceiver, int aggInterval) {
        super();
        this.periodReceiver = periodReceiver;
        this.aggInterval = aggInterval;
    }

    public void reset() {
        this.counter = 0;
        nextStart = true;
    }

    public void incr(Reporting<S> reporting) throws OperationFailedException {

        try {

            if (nextStart) {
                periodReceiver.periodStart(reporting);
                nextStart = false;
            }

            counter++;

            if (counter == aggInterval) {
                periodReceiver.periodEnd(reporting);
                reset();
            }
        } catch (PeriodReceiverException e) {
            throw new OperationFailedException(e);
        }
    }

    protected int getAggInterval() {
        return aggInterval;
    }

    public T getPeriodReceiver() {
        return periodReceiver;
    }
}
