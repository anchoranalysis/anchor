package ch.ethz.biol.cell.mpp.feedback;

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
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public class AggregateReceiverList<T> implements IAggregateReceiver<T> {

	private ArrayList<IAggregateReceiver<T>> delegate = new ArrayList<>();

	public boolean add(IAggregateReceiver<T> e) {
		return delegate.add(e);
	}

	@Override
	public void aggStart(OptimizationFeedbackInitParams<T> initParams, Aggregator agg) throws AggregatorException {
		
		for ( IAggregateReceiver<T> receiver : delegate) {
			receiver.aggStart(initParams, agg);
		}
		
	}

	@Override
	public void aggEnd(Aggregator agg) throws AggregatorException {

		for ( IAggregateReceiver<T> receiver : delegate) {
			receiver.aggEnd(agg);
		}
		
	}

	@Override
	public void aggReport(Reporting<T> reporting, Aggregator agg) throws AggregatorException {

		for ( IAggregateReceiver<T> receiver : delegate) {
			receiver.aggReport(reporting, agg);
		}
		
	}
}
