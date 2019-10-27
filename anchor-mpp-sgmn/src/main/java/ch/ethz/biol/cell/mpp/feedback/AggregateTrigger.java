package ch.ethz.biol.cell.mpp.feedback;

import org.anchoranalysis.mpp.sgmn.optscheme.extractscoresize.ExtractScoreSize;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

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


class AggregateTrigger<S,T extends IAggregateReceiver<S>>  {
	
	private T receiver;

	// How often we output in practice
	private int aggInterval;

	private Aggregator agg;
	
	private ExtractScoreSize<S> extractScoreSize;
	
	// Period Receiver
	private class PeriodReceiver implements IPeriodReceiver<S> {
		
		@Override
		public void periodStart( Reporting<S> reporting ) {
			agg.reset();
		}
		
		@Override
		public void periodEnd( Reporting<S> reporting ) throws PeriodReceiverException {
			
			agg.div( getAggInterval() );
	
			// Report aggregate
			try {
				receiver.aggReport( reporting, agg.deepCopy() );
			} catch (AggregatorException e) {
				throw new PeriodReceiverException(e);
			}
		}
	}
	
	// Constructor		
	public AggregateTrigger(T receiver, int aggInterval, PeriodTriggerBank<S> periodTriggerBank, ExtractScoreSize<S> extractScoreSize ) {
		super();
		this.receiver = receiver;
		this.aggInterval = aggInterval;
		this.extractScoreSize = extractScoreSize;
		
		periodTriggerBank.obtain( aggInterval, new PeriodReceiver() );
	}

	public void start(  OptimizationFeedbackInitParams<S> initParams ) throws AggregatorException {
		this.agg = new Aggregator( initParams.getKernelFactoryList().size() );
		receiver.aggStart(initParams, this.agg);
	}
	
	public void end() throws AggregatorException {
		receiver.aggEnd(this.agg);
	}
	
	
		
	public void record( Reporting<S> reporting ) {
		
		// If accepted we increase the total for this kernel
		if (reporting.isAccptd()) {
			agg.incrKernelAccpt(reporting.getKernel().getID() );
		}
		
		agg.incrKernelProp( reporting.getKernel().getID() );
		agg.incrNRG( extractScoreSize.extractScore(reporting.getCfgNRGAfter()) );
		agg.incrSize( extractScoreSize.extractSize( reporting.getCfgNRGAfter() ) );
		agg.incrTemperature( reporting.getTemperature() );
	}

	protected int getAggInterval() {
		return aggInterval;
	}

	public T getPeriodReceiver() {
		return receiver;
	}
}