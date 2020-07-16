/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.optscheme.feedback;

import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackEndParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.ReporterException;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.AggregateReceiver;

public abstract class ReporterAgg<T> extends ReporterInterval<T> {

    private Logger logger;

    // Constructor
    public ReporterAgg() {
        super();
    }

    public ReporterAgg(double aggIntervalLog10) {
        super(aggIntervalLog10);
    }

    @Override
    public void reportBegin(OptimizationFeedbackInitParams<T> initParams) throws ReporterException {

        initParams
                .getAggregateTriggerBank()
                .obtain(
                        this.getAggInterval(),
                        getAggregateReceiver(),
                        initParams.getPeriodTriggerBank());

        this.logger = initParams.getInitContext().getLogger();
    }

    @Override
    public void reportEnd(OptimizationFeedbackEndParams<T> optStep) {}

    protected abstract AggregateReceiver<T> getAggregateReceiver();

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
