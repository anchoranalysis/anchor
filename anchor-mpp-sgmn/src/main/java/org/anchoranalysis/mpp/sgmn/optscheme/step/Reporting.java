/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.step;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelWithID;
import org.anchoranalysis.mpp.sgmn.optscheme.DualState;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.ReporterException;

/** Exposes data which is only needed by reporting tools */
public class Reporting<S> {

    private int iter;
    private final DualState<S> state;
    private Optional<S> proposed;

    /** An optional additional cfg-NRG that provides an additional explanation of proposed */
    private Optional<S> proposedSecondary;

    private DscrData<?> dscrData;
    private boolean accptd;
    private boolean best;

    Reporting(
            int iter,
            DualState<S> state,
            Optional<S> proposed,
            Optional<S> proposedSecondary,
            DscrData<?> dscrData,
            boolean accptd,
            boolean best) {
        this.iter = iter;
        this.state = state;
        this.proposed = proposed;
        this.proposedSecondary = proposedSecondary;
        this.dscrData = dscrData;
        this.accptd = accptd;
        this.best = best;
    }

    public double getTemperature() {
        return dscrData.getTemperature();
    }

    public boolean isAccptd() {
        return accptd;
    }

    public boolean isBest() {
        return best;
    }

    public Optional<S> getProposal() {
        return proposed;
    }

    public Optional<S> getProposalSecondary() {
        return proposedSecondary;
    }

    public Optional<S> getCfgNRGAfterOptional() {
        return state.getCrnt();
    }

    public S getCfgNRGAfter() throws ReporterException {
        return state.getCrnt().orElseThrow(() -> new ReporterException("No 'after' defined yet"));
    }

    public int getIter() {
        return iter;
    }

    public ProposerFailureDescription getKernelNoProposalDescription() {
        return dscrData.getKernelNoProposalDescription();
    }

    public Optional<S> getBest() {
        return state.getBest();
    }

    public long getExecutionTime() {
        return dscrData.getExecutionTime();
    }

    public int[] getChangedMarkIDs() {
        return dscrData.getChangedMarkIDs();
    }

    public KernelWithID<?> getKernel() {
        return dscrData.getKernel();
    }
}
