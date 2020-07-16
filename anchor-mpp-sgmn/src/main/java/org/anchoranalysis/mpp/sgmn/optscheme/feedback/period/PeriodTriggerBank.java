/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback.period;

import java.util.ArrayList;
import java.util.HashMap;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public class PeriodTriggerBank<S> {

    private HashMap<Integer, PeriodTrigger<S, PeriodReceiverList<S>>> map = new HashMap<>();
    private ArrayList<PeriodTrigger<S, PeriodReceiverList<S>>> list = new ArrayList<>();

    public PeriodTrigger<S, PeriodReceiverList<S>> obtain(
            int period, PeriodReceiver<S> periodReceiver) {

        PeriodTrigger<S, PeriodReceiverList<S>> exst = map.get(period);

        // If we don't already have a trigger for this specific period, we create one
        if (exst == null) {
            PeriodReceiverList<S> listNew = new PeriodReceiverList<>();
            exst = new PeriodTrigger<>(listNew, period);
            list.add(exst);
        }

        exst.getPeriodReceiver().add(periodReceiver);
        return exst;
    }

    public void reset() {
        for (PeriodTrigger<S, PeriodReceiverList<S>> pt : list) {
            pt.reset();
        }
    }

    public void incr(Reporting<S> reporting) throws OperationFailedException {

        for (PeriodTrigger<S, PeriodReceiverList<S>> pt : list) {
            pt.incr(reporting);
        }
    }
}
