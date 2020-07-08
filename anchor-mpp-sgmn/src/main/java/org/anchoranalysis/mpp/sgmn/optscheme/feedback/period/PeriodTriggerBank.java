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


import java.util.ArrayList;
import java.util.HashMap;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public class PeriodTriggerBank<S> {

	private HashMap<Integer, PeriodTrigger<S,PeriodReceiverList<S>> > map = new HashMap< >();
	private ArrayList< PeriodTrigger<S,PeriodReceiverList<S>> > list = new ArrayList< >();
	
	public PeriodTrigger<S,PeriodReceiverList<S>> obtain( int period, PeriodReceiver<S> periodReceiver ) {

		PeriodTrigger<S,PeriodReceiverList<S>> exst = map.get(period);
		
		// If we don't already have a trigger for this specific period, we create one
		if (exst==null) {
			PeriodReceiverList<S> listNew = new PeriodReceiverList<>();
			exst = new PeriodTrigger<>(listNew, period );
			list.add(exst);
		}
		
		exst.getPeriodReceiver().add(periodReceiver);
		return exst;
	}
	
	public void reset() {
		for( PeriodTrigger<S,PeriodReceiverList<S>> pt : list) {
			pt.reset();
		}
	}
	
	public void incr( Reporting<S> reporting ) throws OperationFailedException {
		
		for( PeriodTrigger<S,PeriodReceiverList<S>> pt : list) {
			pt.incr( reporting );
		}
		
	}
	
}
