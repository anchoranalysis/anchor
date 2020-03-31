package org.anchoranalysis.anchor.mpp.graph.execution;

/*
 * #%L
 * anchor-mpp-graph
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


// Stores a KernelExecutionTime for all kernels, and each kernel separately
public class KernelExecutionTimeAllEach {

	private KernelExecutionTime all = null;
	private KernelExecutionTime[] each;
	
	public KernelExecutionTimeAllEach( int numKernels )  {
		each = new KernelExecutionTime[numKernels];
	}

	public KernelExecutionTime getAll() {
		return all;
	}

	public void setAll(KernelExecutionTime all) {
		this.all = all;
	}
	
	public KernelExecutionTime getKernel( int index) {
		return each[index];
	}

	public void setKernel( int index, KernelExecutionTime ket ) {
		each[index] = ket;
	}
	
	public KernelExecutionTime[] getKernelArray() {
		return each;
	}
	
	// -1 indicates all, anything else is an ID for each
	public KernelExecutionTime getForKernelID( int id ) {
		if (id==-1) {
			return all;
		} else {
			return each[id];
		}
	}
}
