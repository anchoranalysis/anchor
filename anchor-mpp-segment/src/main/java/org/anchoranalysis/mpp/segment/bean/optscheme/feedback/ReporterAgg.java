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

package org.anchoranalysis.mpp.segment.bean.optscheme.feedback;

import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.mpp.segment.optscheme.feedback.OptimizationFeedbackEndParams;
import org.anchoranalysis.mpp.segment.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.segment.optscheme.feedback.ReporterException;
import org.anchoranalysis.mpp.segment.optscheme.feedback.aggregate.AggregateReceiver;

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
