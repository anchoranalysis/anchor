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

/**
 * 
 * @author FEEHANO
 *
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
	public void setKernelNoProposalDescription(ProposerFailureDescription kernelNoProposalDescription) {
		this.kernelNoProposalDescription = kernelNoProposalDescription;
	}
}
