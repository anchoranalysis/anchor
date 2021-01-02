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

package org.anchoranalysis.mpp.segment.optimization.step;

import java.util.Optional;
import java.util.function.ToDoubleFunction;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.mpp.segment.bean.optimization.StateReporter;
import org.anchoranalysis.mpp.segment.kernel.proposer.KernelWithIdentifier;
import org.anchoranalysis.mpp.segment.optimization.DualState;
import org.anchoranalysis.mpp.segment.transformer.TransformationContext;

/**
 * @author Owen Feehan
 * @param <S> kernel-type
 * @param <T> optimization-state type
 */
public class OptimizationStep<S, T, V> {

    // The important state needed for the current algorithm step
    private DualState<T> state;

    private boolean accpted;
    private boolean best;

    private Optional<T> proposalOptional = Optional.empty();

    private DescribeData<S, V> describeData = new DescribeData<>();

    public OptimizationStep() {
        state = new DualState<>();
        state.clearBest();
    }

    public void setTemperature(double temperature) {
        describeData.setTemperature(temperature);
    }

    public void assignProposal(Optional<T> proposalToAssign, KernelWithIdentifier<S, V> kid) {

        describeData.setKernel(kid);

        if (this.proposalOptional.equals(proposalToAssign)) {
            return;
        }

        this.proposalOptional = proposalToAssign;
    }

    public void acceptProposal(ToDoubleFunction<T> funcScore) {
        accpted = true;
        assignProposalToCurrent(funcScore);

        setKernelNoProposalDescription(null);
        markChanged(describeData.getKernel());
    }

    public void rejectProposal() {
        markRejected();
        setKernelNoProposalDescription(null);
        markChanged(describeData.getKernel());
    }

    public void markNoProposal(ProposerFailureDescription proposerFailureDescription) {
        proposalOptional = Optional.empty();

        setKernelNoProposalDescription(proposerFailureDescription);
        setChangedMarkIDs(new int[] {});

        markRejected();
    }

    public void setExecutionTime(long executionTime) {
        describeData.setExecutionTime(executionTime);
    }

    public T releaseKeepBest() throws OperationFailedException {
        T ret = state.releaseKeepBest();
        releaseProposal();
        return ret;
    }

    private void releaseProposal() {
        proposalOptional = Optional.empty();
    }

    private void assignProposalToCurrent(ToDoubleFunction<T> funcScore) {
        // We can rely that a proposal exists, as it has been accepted
        state.assignCurrent(proposalOptional);
        maybeAssignAsBest(funcScore);
    }

    private void setKernelNoProposalDescription(
            ProposerFailureDescription kernelRejectionDescription) {
        describeData.setKernelNoProposalDescription(kernelRejectionDescription);
    }

    private void maybeAssignAsBest(ToDoubleFunction<T> funcScore) {
        // Is the score from current, greater than the score from best?
        if (!state.getBest().isPresent() || scoreCurrentBetterThanBest(funcScore)) {
            state.assignBestFromCurrent();
            best = true;
        }
    }

    private boolean scoreCurrentBetterThanBest(ToDoubleFunction<T> funcScore) {
        if (!state.getCurrent().isPresent()) {
            return false;
        }
        return funcScore.applyAsDouble(state.getCurrent().get()) // NOSONAR
                > funcScore.applyAsDouble(state.getBest().get()); // NOSONAR
    }

    private void markChanged(KernelWithIdentifier<S, V> kid) {
        setChangedMarkIDs(kid.getKernel().changedMarkIDArray());
    }

    private void setChangedMarkIDs(int[] changedMarkIDs) {
        describeData.setChangedMarkIDs(changedMarkIDs);
    }

    private void markRejected() {
        accpted = false;
        best = false;
    }

    public KernelWithIdentifier<S, V> getKernel() {
        return describeData.getKernel();
    }

    public Optional<T> getCurrent() {
        return state.getCurrent();
    }

    public Optional<T> getBest() {
        return state.getBest();
    }

    public Optional<T> getProposal() {
        return proposalOptional;
    }

    /**
     * @param iter
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
                        proposalOptional,
                        proposal -> stateReporter.primaryReport().transform(proposal, context)),
                OptionalUtilities.mapBoth(
                        stateReporter.secondaryReport(),
                        proposalOptional,
                        (secondaryReport, proposal) ->
                                secondaryReport.transform(proposal, context)),
                describeData,
                accpted,
                best);
    }
}
