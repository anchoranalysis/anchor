package ch.ethz.biol.cell.mpp.feedback;

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

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public abstract class ReporterAgg<T> extends ReporterInterval<T> {
	
	//private static Log log = LogFactory.getLog(ReporterAgg.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7319481577166675797L;

	// START BEAN PROPERTIES
	
	// END BEAN PROPERTIES
	
	private LogErrorReporter logger;
	
	// Constructor
	public ReporterAgg() {
		super();
	}
		
	@Override
	public void reportBegin( OptimizationFeedbackInitParams<T> initParams ) throws ReporterException {
		
		//aggregateTrigger = new AggregateTrigger<IAggregateReceiver>( getAggregateReceiver(), this.getAggInterval(), initParams.getPeriodTriggerBank() );
		
		//AggregateTrigger<AggregateReceiverList> aggregateTrigger = initParams.getAggregateTriggerBank().obtain( this.getAggInterval(), getAggregateReceiver(), initParams.getPeriodTriggerBank() );
		initParams.getAggregateTriggerBank().obtain(
			this.getAggInterval(),
			getAggregateReceiver(),
			initParams.getPeriodTriggerBank()
		);
		
		//aggregateTrigger.start(initParams);
		this.logger = initParams.getInitContext().getLogger();
	}
	
	@Override
	public void reportEnd( OptimizationFeedbackEndParams<T> optStep ) {
		//aggregateTrigger.end();
	}
	
	@Override
	public void reportItr( Reporting<T> reporting ) {
		super.reportItr( reporting );
		//aggregateTrigger.record(optStep);
		
	}

	protected abstract IAggregateReceiver<T> getAggregateReceiver();

	protected LogErrorReporter getLogger() {
		return logger;
	}

}
