package org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate;

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

import org.anchoranalysis.mpp.sgmn.optscheme.ExtractScoreSize;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.period.PeriodTriggerBank;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public class AggregateTriggerBank<T> {

	private HashMap<Integer, AggregateTrigger<T,AggregateReceiverList<T>> > map = new HashMap<>();
	private ArrayList< AggregateTrigger<T,AggregateReceiverList<T>> > list = new ArrayList<>();
	
	private ExtractScoreSize<T> extractScoreSize;
	
	public AggregateTriggerBank(ExtractScoreSize<T> extractScoreSize) {
		super();
		this.extractScoreSize = extractScoreSize;
	}
	
	public AggregateTrigger<T,AggregateReceiverList<T>> obtain( int period, IAggregateReceiver<T> receiver, PeriodTriggerBank<T> periodTriggerBank ) {

		AggregateTrigger<T,AggregateReceiverList<T>> exst = map.get(period);
		
		// If we don't already have a trigger for this specific period, we create one
		if (exst==null) {
			AggregateReceiverList<T> listNew = new AggregateReceiverList<T>();
			exst = new AggregateTrigger<>(listNew, period, periodTriggerBank, extractScoreSize );
			list.add(exst);
		}
		
		exst.getPeriodReceiver().add(receiver);
		return exst;
	}
	
	public void start(  OptimizationFeedbackInitParams<T> initParams ) throws AggregatorException {
		for( AggregateTrigger<T,AggregateReceiverList<T>> item : list) {
			item.start( initParams );
		}
	}
	
	public void record( Reporting<T> reporting ) {
		
		for( AggregateTrigger<T,AggregateReceiverList<T>> item : list) {
			item.record(reporting);
		}
		
	}
	
	public void end() throws AggregatorException {
		for( AggregateTrigger<T,AggregateReceiverList<T>> item : list) {
			item.end();
		}
	}
	
}
