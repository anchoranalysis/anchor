/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.kernel;

import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelWithID;
import org.anchoranalysis.mpp.sgmn.optscheme.step.OptimizationStep;
import org.anchoranalysis.mpp.sgmn.transformer.TransformationContext;

/**
 * @author Owen Feehan
 * @param <S> kernel-type
 * @param <T> assignment type
 */
public interface KernelAssigner<S, T> {

    void assignProposal(
            OptimizationStep<S, T> optStep, TransformationContext context, KernelWithID<S> kid)
            throws KernelCalcNRGException;
}
