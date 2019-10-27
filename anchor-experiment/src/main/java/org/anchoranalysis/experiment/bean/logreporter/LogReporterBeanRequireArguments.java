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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.bean.RequireArguments;
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

public class LogReporterBeanRequireArguments extends LogReporterBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private LogReporterBean logReporter;
	
	@BeanField
	private RequireArguments requireArguments;
	// END BEAN PROPERTIES
	
	public LogReporterBean getLogReporter() {
		return logReporter;
	}

	public void setLogReporter(LogReporterBean logReporter) {
		this.logReporter = logReporter;
	}

	public RequireArguments getRequireArguments() {
		return requireArguments;
	}

	public void setRequireArguments(RequireArguments requireArguments) {
		this.requireArguments = requireArguments;
	}

	@Override
	public StatefulLogReporter create(BoundOutputManager bom,
			ErrorReporter errorReporter, ExperimentExecutionArguments expArgs) {
		if (requireArguments.hasAllRequiredArguments(expArgs)) {
			return logReporter.create(bom, errorReporter, expArgs);
		} else {
			return new StatefulNullLogReporter();
		}
	}

}
