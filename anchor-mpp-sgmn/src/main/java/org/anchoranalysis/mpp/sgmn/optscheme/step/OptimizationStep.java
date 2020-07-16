/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.step;

import java.util.Optional;
import java.util.function.ToDoubleFunction;
import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelWithID;
import org.anchoranalysis.mpp.sgmn.optscheme.DualState;
import org.anchoranalysis.mpp.sgmn.optscheme.StateReporter;
import org.anchoranalysis.mpp.sgmn.transformer.TransformationContext;

/**
 * @author Owen Feehan
 * @param <S> kernel-type
 * @param <T> optimization-state type
 */
public class OptimizationStep<S, T> {

    // The important state needed for the current algorithm step
    private DualState<T> state;

    private boolean accptd;
    private boolean best;

    private Optional<T> proposal = Optional.empty();

    private DscrData<S> dscrData = new DscrData<>();

    public OptimizationStep() {
        state = new DualState<>();
        state.clearBest();
    }

    public void setTemperature(double temperature) {
        dscrData.setTemperature(temperature);
    }

    public void assignProposal(Optional<T> proposalNew, KernelWithID<S> kid) {

        dscrData.setKernel(kid);

        if (this.proposal.equals(proposalNew)) {
            return;
        }

        this.proposal = proposalNew;
    }

    public void acceptProposal(ToDoubleFunction<T> funcScore) {
        accptd = true;
        assgnCrntFromProposal(funcScore);

        setKernelNoProposalDescription(null);
        markChanged(dscrData.getKernel());
    }

    public void rejectProposal() {
        markRejected();
        setKernelNoProposalDescription(null);
        markChanged(dscrData.getKernel());
    }

    public void markNoProposal(ProposerFailureDescription proposerFailureDescription) {
        proposal = Optional.empty();

        setKernelNoProposalDescription(proposerFailureDescription);
        setChangedMarkIDs(new int[] {});

        markRejected();
    }

    public void setExecutionTime(long executionTime) {
        dscrData.setExecutionTime(executionTime);
    }

    public T releaseKeepBest() throws OperationFailedException {
        T ret = state.releaseKeepBest();
        releaseProposal();
        return ret;
    }

    private void releaseProposal() {
        proposal = Optional.empty();
    }

    private void assgnCrntFromProposal(ToDoubleFunction<T> funcScore) {
        // We can rely that a proposal exists, as it has been accepted
        state.assignCrnt(proposal.get());
        maybeAssignAsBest(funcScore);
    }

    private void setKernelNoProposalDescription(
            ProposerFailureDescription kernelRejectionDescription) {
        dscrData.setKernelNoProposalDescription(kernelRejectionDescription);
    }

    private void maybeAssignAsBest(ToDoubleFunction<T> funcScore) {
        // Is the score from crnt, greater than the score from best?
        if (!state.getBest().isPresent() || scoreCurrentBetterThanBest(funcScore)) {
            state.assignBestFromCrnt();
            best = true;
        }
    }

    private boolean scoreCurrentBetterThanBest(ToDoubleFunction<T> funcScore) {
        if (!state.getCrnt().isPresent()) {
            return false;
        }
        return funcScore.applyAsDouble(state.getCrnt().get())
                > funcScore.applyAsDouble(state.getBest().get()); // NOSONAR
    }

    private void markChanged(KernelWithID<S> kid) {
        setChangedMarkIDs(kid.getKernel().changedMarkIDArray());
    }

    private void setChangedMarkIDs(int[] changedMarkIDs) {
        dscrData.setChangedMarkIDs(changedMarkIDs);
    }

    private void markRejected() {
        accptd = false;
        best = false;
    }

    public KernelWithID<S> getKernel() {
        return dscrData.getKernel();
    }

    public Optional<T> getCrnt() {
        return state.getCrnt();
    }

    public Optional<T> getBest() {
        return state.getBest();
    }

    public Optional<T> getProposal() {
        return proposal;
    }

    /**
     * @param iter
     * @param func Main function for extracting a CfgNRGPixelized from type T
     * @param funcProposalSecondary Function for extracting a secondary proposal
     * @return
     * @throws OperationFailedException
     */
    public <U> Reporting<U> reporting(
            int iter, StateReporter<T, U> stateReporter, TransformationContext context)
            throws OperationFailedException {

        return new Reporting<>(
                iter,
                state.transform(stateReporter.primaryReport(), context),
                OptionalUtilities.map(
                        proposal, p -> stateReporter.primaryReport().transform(p, context)),
                OptionalUtilities.mapBoth(
                        stateReporter.secondaryReport(),
                        proposal,
                        (secondaryReport, prop) -> secondaryReport.transform(prop, context)),
                dscrData,
                accptd,
                best);
    }
}
