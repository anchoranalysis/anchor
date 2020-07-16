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
