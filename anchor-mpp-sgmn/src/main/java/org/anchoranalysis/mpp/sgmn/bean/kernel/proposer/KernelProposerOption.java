/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.kernel.proposer;

import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.WeightedKernel;

public abstract class KernelProposerOption<T> extends AnchorBean<KernelProposerOption<T>> {

    // Add weighted kernel factories to a list, and returns the total weight
    public abstract double addWeightedKernelFactories(List<WeightedKernel<T>> lst);

    public abstract double getWeightPositive();

    public abstract double getWeightNegative();
}
