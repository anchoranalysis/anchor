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

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.ConsoleMessageLogger;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * The destination(s) to which log-messages are sent.
 * 
 * @author Owen Feehan
 *
 */
public abstract class LoggingDestination extends AnchorBean<LoggingDestination> {

	/**
	 * Creates a logger for this destination - and if anything goes wrong reporting fallback into the console.
	 * <p>
	 * Identical to {@link LoggingDestination#createWithLogFallback} but uses a {@link ConsoleReporter}
	 * as the {@code fallbackErrorReporter}.
	 * 
	 * @param  outputManager the output-manager
	 * @param  arguments experiment-arguments
	 * @param  detailedLogging whether detailed logging should occur in this reporter, or a less detailed version
	 * @return a newly created log-reporter
	 */
	public StatefulMessageLogger createWithConsoleFallback(
		BoundOutputManager outputManager,
		ExperimentExecutionArguments arguments,
		boolean detailedLogging
	) {
		return createWithLogFallback(
			outputManager,
			new ConsoleMessageLogger(),
			arguments,
			detailedLogging
		);
	}
	
	/**
	 * Creates a logger for this destination - and if anything goes wrong reporting fallback into a log.
	 * 
	 * @param  outputManager the output-manager
	 * @param  fallbackErrorReporter where any errors are reported, when trying to create this log.
	 * @param  arguments experiment-arguments
	 * @param  detailedLogging whether detailed logging should occur in this reporter, or a less detailed version
	 * @return a newly created log-reporter
	 */
	public StatefulMessageLogger createWithLogFallback(
		BoundOutputManager outputManager,
		MessageLogger fallbackErrorReporter,
		ExperimentExecutionArguments arguments,
		boolean detailedLogging
	) {
		return create(
			outputManager,
			new ErrorReporterIntoLog(fallbackErrorReporter),
			arguments,
			detailedLogging
		);
	}
	
	/**
	 * Creates a logger for this destination
	 * 
	 * @param  outputManager the output-manager
	 * @param  fallbackErrorReporter where any errors are reported, when trying to create this log.
	 * @param  arguments experiment-arguments
	 * @param  detailedLogging whether detailed logging should occur in this reporter, or a less detailed version
	 * 
	 * @return a newly created log-reporter
	 */
	public abstract StatefulMessageLogger create(
		BoundOutputManager outputManager,
		ErrorReporter fallbackErrorReporter,
		ExperimentExecutionArguments arguments,
		boolean detailedLogging
	);
}
