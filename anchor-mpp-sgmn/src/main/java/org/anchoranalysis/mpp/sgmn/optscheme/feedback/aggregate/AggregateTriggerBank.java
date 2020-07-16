/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate;

import java.util.ArrayList;
import java.util.HashMap;
import org.anchoranalysis.mpp.sgmn.optscheme.ExtractScoreSize;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.ReporterException;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.period.PeriodTriggerBank;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public class AggregateTriggerBank<T> {

    private HashMap<Integer, AggregateTrigger<T, AggregateReceiverList<T>>> map = new HashMap<>();
    private ArrayList<AggregateTrigger<T, AggregateReceiverList<T>>> list = new ArrayList<>();

    private ExtractScoreSize<T> extractScoreSize;

    public AggregateTriggerBank(ExtractScoreSize<T> extractScoreSize) {
        super();
        this.extractScoreSize = extractScoreSize;
    }

    public AggregateTrigger<T, AggregateReceiverList<T>> obtain(
            int period, AggregateReceiver<T> receiver, PeriodTriggerBank<T> periodTriggerBank) {

        AggregateTrigger<T, AggregateReceiverList<T>> exst = map.get(period);

        // If we don't already have a trigger for this specific period, we create one
        if (exst == null) {
            AggregateReceiverList<T> listNew = new AggregateReceiverList<>();
            exst = new AggregateTrigger<>(listNew, period, periodTriggerBank, extractScoreSize);
            list.add(exst);
        }

        exst.getPeriodReceiver().add(receiver);
        return exst;
    }

    public void start(OptimizationFeedbackInitParams<T> initParams) throws AggregatorException {
        for (AggregateTrigger<T, AggregateReceiverList<T>> item : list) {
            item.start(initParams);
        }
    }

    public void record(Reporting<T> reporting) throws ReporterException {

        for (AggregateTrigger<T, AggregateReceiverList<T>> item : list) {
            item.record(reporting);
        }
    }

    public void end() throws AggregatorException {
        for (AggregateTrigger<T, AggregateReceiverList<T>> item : list) {
            item.end();
        }
    }
}
