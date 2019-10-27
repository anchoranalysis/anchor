package org.anchoranalysis.mpp.sgmn.optscheme.step;

/*-
 * #%L
 * anchor-mpp-sgmn
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelWithID;
import org.anchoranalysis.mpp.sgmn.optscheme.DualState;

/** Exposes data which is only needed by reporting tools */
public class Reporting<S> {
	
	private int iter;
	private DualState<S> state;
	private S proposed;
	
	/** An optional additional cfg-NRG that provides an additional explanation of proposed */
	private S proposedSecondary;
	
	private DscrData<?> dscrData;
	private boolean accptd;
	private boolean best;
	
	Reporting(
		int iter,
		DualState<S> state,
		S proposed,
		S proposedSecondary,
		DscrData<?> dscrData,
		boolean accptd,
		boolean best
	) {
		super();
		this.iter = iter;
		this.state = state;
		this.proposed = proposed;
		this.proposedSecondary = proposedSecondary;
		this.dscrData = dscrData;
		this.accptd = accptd;
		this.best = best;
	}
	
	public boolean hasProposal() {
		return proposed!=null;
	}
	
	public boolean hasProposalSecondary() {
		return proposedSecondary!=null;
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
	
	public S getProposal() {
		return proposed;
	}
	
	public S getProposalSecondary() {
		return proposedSecondary;
	}
	
	public S getCfgNRGAfter() {
		if (state.getCrnt()!=null) {
			return state.getCrnt();
		} else {
			return null;
		}
	}
	
	public int getIter() {
		return iter;
	}
	
	public ProposerFailureDescription getKernelNoProposalDescription() {
		return dscrData.getKernelNoProposalDescription();
	}
	
	public S getBest() {
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
