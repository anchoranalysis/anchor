/*-
 * #%L
 * anchor-mpp-sgmn
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.mpp.segment.optimization.feedback.aggregate;

import lombok.Getter;
import org.anchoranalysis.mpp.segment.bean.optimization.ExtractScoreSize;
import org.anchoranalysis.mpp.segment.optimization.feedback.FeedbackBeginParameters;
import org.anchoranalysis.mpp.segment.optimization.feedback.ReporterException;
import org.anchoranalysis.mpp.segment.optimization.feedback.period.PeriodReceiver;
import org.anchoranalysis.mpp.segment.optimization.feedback.period.PeriodReceiverException;
import org.anchoranalysis.mpp.segment.optimization.feedback.period.PeriodTriggerBank;
import org.anchoranalysis.mpp.segment.optimization.step.Reporting;

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

    public void start(FeedbackBeginParameters<S> initialization) throws AggregatorException {
        this.agg = new Aggregator(initialization.getKernelFactoryList().size());
        periodReceiver.aggStart(initialization, this.agg);
    }

    public void record(Reporting<S> reporting) throws ReporterException {

        // If accepted we increase the total for this kernel
        if (reporting.isAccepted()) {
            agg.incrKernelAccpt(reporting.kernelIdentifier());
        }

        agg.incrKernelProp(reporting.kernelIdentifier());
        agg.incrEnergy(extractScoreSize.extractScore(reporting.getMarksAfter()));
        agg.incrSize(extractScoreSize.extractSize(reporting.getMarksAfter()));
        agg.incrTemperature(reporting.getTemperature());
    }

    public void end() throws AggregatorException {
        periodReceiver.aggEnd(this.agg);
    }
}
