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

package org.anchoranalysis.mpp.segment.optscheme.step;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.mpp.segment.kernel.proposer.KernelWithIdentifier;
import org.anchoranalysis.mpp.segment.optscheme.DualState;
import org.anchoranalysis.mpp.segment.optscheme.feedback.ReporterException;

/** Exposes data which is only needed by reporting tools */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Reporting<S> {

    @Getter private int iter;
    private final DualState<S> state;
    @Getter private Optional<S> proposal;

    /** An optional additional marks-Energy that provides an additional explanation of proposed */
    @Getter private Optional<S> proposalSecondary;

    private DescribeData<?> describeData;

    @Getter private boolean accepted;
    private boolean best;

    public boolean isBest() {
        return best;
    }

    public double getTemperature() {
        return describeData.getTemperature();
    }

    public Optional<S> getMarksAfterOptional() {
        return state.getCurrent();
    }

    public S getMarksAfter() throws ReporterException {
        return state.getCurrent()
                .orElseThrow(() -> new ReporterException("No 'after' defined yet"));
    }

    public ProposerFailureDescription getKernelNoProposalDescription() {
        return describeData.getKernelNoProposalDescription();
    }

    public Optional<S> getBest() {
        return state.getBest();
    }

    public long getExecutionTime() {
        return describeData.getExecutionTime();
    }

    public int[] getChangedMarkIDs() {
        return describeData.getChangedMarkIDs();
    }

    public KernelWithIdentifier<?> getKernel() {
        return describeData.getKernel();
    }
}
