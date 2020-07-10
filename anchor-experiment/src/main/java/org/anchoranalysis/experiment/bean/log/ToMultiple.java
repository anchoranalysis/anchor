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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.MessageLoggerList;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Rather than logging to one location, logs to multiple locations (from a list).
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor
public class ToMultiple extends LoggingDestination {

	// START BEAN
	/** The list of loggers to log to */
	@BeanField @Getter @Setter
	private List<LoggingDestination> list = new ArrayList<>();
	// END BEAN
	
	/**
	 * Constructs a logger to two locations
	 * 
	 * @param first first-location
	 * @param second second-location
	 */
	public ToMultiple( LoggingDestination first, LoggingDestination second ) {
		this();
		list.add(first);
		list.add(second);
	}

	@Override
	public StatefulMessageLogger create( BoundOutputManager outputManager, ErrorReporter errorReporter, ExperimentExecutionArguments arguments, boolean detailedLogging ) {
		return new MessageLoggerList(
			list.stream().map( logger->
				logger.create(outputManager, errorReporter, arguments, detailedLogging)
			)
		);
	}
}