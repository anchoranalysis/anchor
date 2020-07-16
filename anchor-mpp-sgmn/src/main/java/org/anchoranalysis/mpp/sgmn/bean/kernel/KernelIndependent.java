/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.kernel;

import org.anchoranalysis.mpp.sgmn.kernel.KernelCalcContext;

/**
 * A kernel that makes proposals independently of the current state
 *
 * <p>It receives no feedback on whether proposals are accepted/rejected
 *
 * @author feehano
 */
public abstract class KernelIndependent<T> extends Kernel<T> {

    // Call ONCE before calculating anything
    @Override
    public void initBeforeCalc(KernelCalcContext context) {
        // NOT NEEDED
    }

    @Override
    public void informLatestState(T cfgNRG) {
        // Deliberately DOES NOTHING as all sub-classes should be independent of
        //   current state
    }
}
