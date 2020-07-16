/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate;

import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public interface AggregateReceiver<T> {

    default void aggStart(OptimizationFeedbackInitParams<T> initParams, Aggregator agg)
            throws AggregatorException {}

    default void aggEnd(Aggregator agg) throws AggregatorException {}

    void aggReport(Reporting<T> reporting, Aggregator agg) throws AggregatorException;
}
