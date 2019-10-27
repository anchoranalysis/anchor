package ch.ethz.biol.cell.mpp.feedback.reporter;

/*
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.io.Serializable;

import org.anchoranalysis.core.arithmetic.RunningSum;

public class KernelExecutionStats implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6960573557987225176L;
	
	private RunningSum[] kernelExecutionTimeNotProposed;
	private RunningSum[] kernelExecutionTimeAccepted;
	private RunningSum[] kernelExecutionTimeRejected;
	
	private long totalExecutionTime;
	
	public KernelExecutionStats( int numKernel ) {
		kernelExecutionTimeNotProposed = new RunningSum[numKernel];
		kernelExecutionTimeAccepted = new RunningSum[numKernel];
		kernelExecutionTimeRejected = new RunningSum[numKernel];
		
		initRunningSum(kernelExecutionTimeNotProposed);
		initRunningSum(kernelExecutionTimeAccepted);
		initRunningSum(kernelExecutionTimeRejected);
	}
	
	
	private static RunningSum sum( RunningSum[] arr ) {
		
		RunningSum total = new RunningSum();
		for (int i=0; i<arr.length; i++) {
			total.incr( arr[i].getSum(), arr[i].getCnt() );
		}
		return total;
	}
	
	public RunningSum sumNotProposed() {
		return sum( kernelExecutionTimeNotProposed );
	}
	
	public RunningSum sumAccepted() {
		return sum( kernelExecutionTimeAccepted );
	}
	
	public RunningSum sumRejected() {
		return sum( kernelExecutionTimeRejected );
	}
	
	private void initRunningSum( RunningSum[] arr ) {
		
		for (int i=0; i<arr.length; i++) {
			arr[i] = new RunningSum();
		}
	}
	
	public void incrNotProposed( int kernelID, double executionTime ) {
		if (kernelID>=0) {
			kernelExecutionTimeNotProposed[kernelID].add( executionTime );
		}
	}
	
	public void incrAccepted( int kernelID, double executionTime ) {
		if (kernelID>=0) {
			kernelExecutionTimeAccepted[kernelID].add( executionTime );
		}
	}
	
	public void incrRejected( int kernelID, double executionTime ) {
		if (kernelID>=0) {
			kernelExecutionTimeRejected[kernelID].add( executionTime );
		}
	}
	
	public double getProposedTimeForKernelSum( int kernelID ) {
		return kernelExecutionTimeAccepted[kernelID].getSum() + kernelExecutionTimeRejected[kernelID].getSum();
	}
	
	public int getProposedTimeForKernelCnt( int kernelID ) {
		return kernelExecutionTimeAccepted[kernelID].getCnt() + kernelExecutionTimeRejected[kernelID].getCnt();
	}
	
	// Proposed + non-proposed (but can be smaller than TotalExecutionTime due to reporting)
	public double getExecutionTimeForKernelSumExclReporting( int kernelID ) {
		return getProposedTimeForKernelSum(kernelID) + kernelExecutionTimeNotProposed[kernelID].getSum();
	}
	
	public int getExecutionTimeForKernelCntExclReporting( int kernelID ) {
		return getProposedTimeForKernelCnt(kernelID) + kernelExecutionTimeNotProposed[kernelID].getCnt();
	}

	public long getTotalExecutionTime() {
		return totalExecutionTime;
	}

	public void setTotalExecutionTime(long totalExecutionTime) {
		this.totalExecutionTime = totalExecutionTime;
	}

	public RunningSum[] getKernelExecutionTimeNotProposed() {
		return kernelExecutionTimeNotProposed;
	}

	public RunningSum[] getKernelExecutionTimeAccepted() {
		return kernelExecutionTimeAccepted;
	}

	public RunningSum[] getKernelExecutionTimeRejected() {
		return kernelExecutionTimeRejected;
	}
}