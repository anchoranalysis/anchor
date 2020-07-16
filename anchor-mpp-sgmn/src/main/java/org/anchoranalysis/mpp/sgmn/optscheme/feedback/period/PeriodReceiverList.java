/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback.period;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public class PeriodReceiverList<T> implements PeriodReceiver<T> {

    private List<PeriodReceiver<T>> arrayList = new ArrayList<>();

    public boolean add(PeriodReceiver<T> pr) {
        return arrayList.add(pr);
    }

    @Override
    public void periodStart(Reporting<T> reporting) throws PeriodReceiverException {
        for (PeriodReceiver<T> pr : arrayList) {
            pr.periodStart(reporting);
        }
    }

    @Override
    public void periodEnd(Reporting<T> reporting) throws PeriodReceiverException {
        for (PeriodReceiver<T> pr : arrayList) {
            pr.periodEnd(reporting);
        }
    }
}
