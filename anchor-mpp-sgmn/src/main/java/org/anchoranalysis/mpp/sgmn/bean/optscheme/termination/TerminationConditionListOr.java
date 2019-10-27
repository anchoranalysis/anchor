package org.anchoranalysis.mpp.sgmn.bean.optscheme.termination;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.log.LogReporter;

// An OR list of termination conditions
public class TerminationConditionListOr extends TerminationCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4023747161536462292L;
	
	// START BEAN PROPERTIES
	@BeanField
	private List<TerminationCondition> list;
	// END BEAN PROPERTIES

	public TerminationConditionListOr() {
		list = new ArrayList<>();
	}
	
	public TerminationConditionListOr( TerminationCondition cond1, TerminationCondition cond2 ) {
		this();
		list.add(cond1);
		list.add(cond2);
	}
	
	public boolean add( TerminationCondition tc ) {
		return list.add(tc);
	}

	public int size() {
		return list.size();
	}
	
	@Override
	public boolean continueIterations(int crntIter, double score, int size, LogReporter logReporter) {
		
		for (TerminationCondition tc : this.list) {
			if (!tc.continueIterations(crntIter, score, size, logReporter)) {
				return false;
			}
		}
		
		return true;
	}
	
	
	@Override
	public void init() {
		
		for (TerminationCondition tc : this.list) {
			tc.init();
		}
	}
	
	public List<TerminationCondition> getList() {
		return list;
	}

	public void setList(List<TerminationCondition> list) {
		this.list = list;
	}
}
