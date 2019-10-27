package org.anchoranalysis.experiment.bean.logreporter;

/*
 * #%L
 * anchor-experiment
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
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.LogReporterList;
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

public class LogReporterBeanList extends LogReporterBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN
	@BeanField
	private List<LogReporterBean> list = new ArrayList<>();
	// END BEAN
	
	public LogReporterBeanList() {
		
	}
	
	public LogReporterBeanList( LogReporterBean first, LogReporterBean second ) {
		assert( first!=null );
		assert( second!=null );
		list.add(first);
		list.add(second);
	}

	public List<LogReporterBean> getList() {
		return list;
	}

	public void setList(List<LogReporterBean> list) {
		this.list = list;
	}

	@Override
	public StatefulLogReporter create( BoundOutputManager bom, ErrorReporter errorReporter, ExperimentExecutionArguments expArgs ) {
		LogReporterList out = new LogReporterList();
		for (LogReporterBean logReporter : list) {
			out.add( logReporter.create( bom, errorReporter, expArgs ) );
		}
		return out;
	}
	
}
