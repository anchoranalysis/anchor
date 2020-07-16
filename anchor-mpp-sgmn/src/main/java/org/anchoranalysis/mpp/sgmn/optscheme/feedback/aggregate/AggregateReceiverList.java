/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate;

import java.util.ArrayList;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public class AggregateReceiverList<T> implements AggregateReceiver<T> {

    private ArrayList<AggregateReceiver<T>> delegate = new ArrayList<>();

    public boolean add(AggregateReceiver<T> e) {
        return delegate.add(e);
    }

    @Override
    public void aggStart(OptimizationFeedbackInitParams<T> initParams, Aggregator agg)
            throws AggregatorException {

        for (AggregateReceiver<T> receiver : delegate) {
            receiver.aggStart(initParams, agg);
        }
    }

    @Override
    public void aggEnd(Aggregator agg) throws AggregatorException {

        for (AggregateReceiver<T> receiver : delegate) {
            receiver.aggEnd(agg);
        }
    }

    @Override
    public void aggReport(Reporting<T> reporting, Aggregator agg) throws AggregatorException {

        for (AggregateReceiver<T> receiver : delegate) {
            receiver.aggReport(reporting, agg);
        }
    }
}
