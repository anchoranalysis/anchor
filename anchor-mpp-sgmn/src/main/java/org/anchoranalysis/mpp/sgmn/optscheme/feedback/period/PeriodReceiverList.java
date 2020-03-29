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
import java.util.List;

import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public class PeriodReceiverList<T> implements IPeriodReceiver<T> {

	private List<IPeriodReceiver<T>> arrayList = new ArrayList<>();

	public boolean add(IPeriodReceiver<T> pr) {
		assert( pr != null);
		return arrayList.add(pr);
	}

	@Override
	public void periodStart( Reporting<T> reporting ) throws PeriodReceiverException {
		for (IPeriodReceiver<T> pr : arrayList) {
			pr.periodStart( reporting );
		}
	}

	@Override
	public void periodEnd(Reporting<T> reporting) throws PeriodReceiverException {
		for (IPeriodReceiver<T> pr : arrayList) {
			pr.periodEnd(reporting);
		}
	}
	
	
}
