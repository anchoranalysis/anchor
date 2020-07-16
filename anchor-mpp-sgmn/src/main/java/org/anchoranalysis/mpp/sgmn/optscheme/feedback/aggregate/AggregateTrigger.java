/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate;

import lombok.Getter;
import org.anchoranalysis.mpp.sgmn.optscheme.ExtractScoreSize;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.ReporterException;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.period.PeriodReceiver;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.period.PeriodReceiverException;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.period.PeriodTriggerBank;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

class AggregateTrigger<S, T extends AggregateReceiver<S>> {

    @Getter private T periodReceiver;

    /** How often we output in practice */
    @Getter private int aggInterval;

    private Aggregator agg;

    private ExtractScoreSize<S> extractScoreSize;

    /** Calls an aggregated report on each period-update */
    private class ReportOnUpdates implements PeriodReceiver<S> {

        @Override
        public void periodStart(Reporting<S> reporting) {
            agg.reset();
        }

        @Override
        public void periodEnd(Reporting<S> reporting) throws PeriodReceiverException {

            agg.div(getAggInterval());

            // Report aggregate
            try {
                periodReceiver.aggReport(reporting, agg.deepCopy());
            } catch (AggregatorException e) {
                throw new PeriodReceiverException(e);
            }
        }
    }

    // Constructor
    public AggregateTrigger(
            T receiver,
            int aggInterval,
            PeriodTriggerBank<S> periodTriggerBank,
            ExtractScoreSize<S> extractScoreSize) {
        this.periodReceiver = receiver;
        this.aggInterval = aggInterval;
        this.extractScoreSize = extractScoreSize;

        periodTriggerBank.obtain(aggInterval, new ReportOnUpdates());
    }

    public void start(OptimizationFeedbackInitParams<S> initParams) throws AggregatorException {
        this.agg = new Aggregator(initParams.getKernelFactoryList().size());
        periodReceiver.aggStart(initParams, this.agg);
    }

    public void record(Reporting<S> reporting) throws ReporterException {

        // If accepted we increase the total for this kernel
        if (reporting.isAccptd()) {
            agg.incrKernelAccpt(reporting.getKernel().getID());
        }

        agg.incrKernelProp(reporting.getKernel().getID());
        agg.incrNRG(extractScoreSize.extractScore(reporting.getCfgNRGAfter()));
        agg.incrSize(extractScoreSize.extractSize(reporting.getCfgNRGAfter()));
        agg.incrTemperature(reporting.getTemperature());
    }

    public void end() throws AggregatorException {
        periodReceiver.aggEnd(this.agg);
    }
}
