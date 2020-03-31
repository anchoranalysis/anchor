package org.anchoranalysis.mpp.sgmn.optscheme.feedback.period;

/*
 * #%L
 * anchor-mpp-sgmn
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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

class PeriodTrigger<S,T extends IPeriodReceiver<S>> {
	
	private T periodReceiver;

	// How often we output in practice
	private int aggInterval;
	
	// Running counter of iterations
	private int counter=0;
	
	private boolean nextStart = false;
	
	// Constructor		
	public PeriodTrigger(T periodReceiver, int aggInterval) {
		super();
		this.periodReceiver = periodReceiver;
		this.aggInterval = aggInterval;
	}

	public void reset() {
		
		this.counter = 0;
		nextStart = true;
	}
	
	public void incr( Reporting<S> reporting ) throws OperationFailedException {
		
		try {
			
			if (nextStart) {
				periodReceiver.periodStart( reporting );
				nextStart = false;
			}
			
			if (++counter==aggInterval) {
				periodReceiver.periodEnd( reporting );
				counter = 0;
				nextStart = true;
			}
		} catch (PeriodReceiverException e) {
			throw new OperationFailedException(e);
		}
	}

	protected int getAggInterval() {
		return aggInterval;
	}

	public T getPeriodReceiver() {
		return periodReceiver;
	}
}