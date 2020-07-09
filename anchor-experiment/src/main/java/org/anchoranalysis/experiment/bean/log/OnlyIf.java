package org.anchoranalysis.experiment.bean.log;

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
import org.anchoranalysis.experiment.bean.require.RequireArguments;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

import lombok.Getter;
import lombok.Setter;

/**
 * Logs messages to a particular location ONLY if certain conditions are fulfilled.
 * 
 * @author Owen Feehan
 *
 */
public class OnlyIf extends LoggingDestination {

	// START BEAN PROPERTIES
	/** The logger to use if conditions are fulfilled */
	@BeanField @Getter @Setter
	private LoggingDestination log;
	
	/** The conditions that must be fulfilled */
	@BeanField  @Getter @Setter
	private RequireArguments requireArguments;
	// END BEAN PROPERTIES
	
	@Override
	public StatefulMessageLogger create(
		BoundOutputManager outputManager,
		ErrorReporter errorReporter,
		ExperimentExecutionArguments arguments,
		boolean detailedLogging
	) {
		if (requireArguments.hasAllRequiredArguments(arguments.isDebugEnabled())) {
			return log.create(outputManager, errorReporter, arguments, detailedLogging);
		} else {
			return new StatefulNullMessageLogger();
		}
	}
}
