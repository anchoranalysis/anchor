/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.step;

import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelWithID;

/**
 * @author Owen Feehan
 * @param <T> kernel-type
 */
class DscrData<T> {

    private double temperature;
    private int[] changedMarkIDs;
    private long executionTime;
    private KernelWithID<T> kernel;
    private ProposerFailureDescription kernelNoProposalDescription;

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int[] getChangedMarkIDs() {
        return changedMarkIDs;
    }

    public void setChangedMarkIDs(int[] changedMarkIDs) {
        this.changedMarkIDs = changedMarkIDs;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public KernelWithID<T> getKernel() {
        return kernel;
    }

    public void setKernel(KernelWithID<T> kernel) {
        this.kernel = kernel;
    }

    public ProposerFailureDescription getKernelNoProposalDescription() {
        return kernelNoProposalDescription;
    }

    public void setKernelNoProposalDescription(
            ProposerFailureDescription kernelNoProposalDescription) {
        this.kernelNoProposalDescription = kernelNoProposalDescription;
    }
}
