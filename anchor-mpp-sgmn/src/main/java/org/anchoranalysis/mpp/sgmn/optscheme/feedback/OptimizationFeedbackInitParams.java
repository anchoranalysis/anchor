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
/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback;

import org.anchoranalysis.mpp.sgmn.kernel.proposer.WeightedKernelList;
import org.anchoranalysis.mpp.sgmn.optscheme.OptSchemeContext;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.AggregateTriggerBank;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.period.PeriodTriggerBank;

public class OptimizationFeedbackInitParams<T> {

    private OptSchemeContext initContext;

    private WeightedKernelList<?> kernelFactoryList;
    private PeriodTriggerBank<T> periodTriggerBank;
    private AggregateTriggerBank<T> aggregateTriggerBank;

    public WeightedKernelList<?> getKernelFactoryList() {
        return kernelFactoryList;
    }

    public void setKernelFactoryList(WeightedKernelList<?> kernelFactoryList) {
        this.kernelFactoryList = kernelFactoryList;
    }

    public PeriodTriggerBank<T> getPeriodTriggerBank() {
        return periodTriggerBank;
    }

    public void setPeriodTriggerBank(PeriodTriggerBank<T> periodTriggerBank) {
        this.periodTriggerBank = periodTriggerBank;
    }

    public AggregateTriggerBank<T> getAggregateTriggerBank() {
        return aggregateTriggerBank;
    }

    public void setAggregateTriggerBank(AggregateTriggerBank<T> aggregateTriggerBank) {
        this.aggregateTriggerBank = aggregateTriggerBank;
    }

    public OptSchemeContext getInitContext() {
        return initContext;
    }

    public void setInitContext(OptSchemeContext initContext) {
        this.initContext = initContext;
    }
}
