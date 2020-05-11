package org.anchoranalysis.experiment.task;

/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.nio.file.Path;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

class BoundContextSpecify implements BoundIOContext {
	
	private ExperimentExecutionArguments experimentArguments;
	private BoundOutputManagerRouteErrors outputManager;
	
	private StatefulLogReporter logReporter;
	private LogErrorReporter logger;	// Always related to the above two fields
	
	public BoundContextSpecify(
		ExperimentExecutionArguments experimentArguments,
		BoundOutputManagerRouteErrors outputManager,
		StatefulLogReporter logReporter,
		ErrorReporter errorReporter
	) {
		super();
		this.experimentArguments = experimentArguments;
		this.outputManager = outputManager;
		
		this.logReporter = logReporter;
		this.logger = new LogErrorReporter(logReporter, errorReporter);
	}
	
	@Override
	public Path getModelDirectory() {
		return experimentArguments.getModelDirectory();
	}

	@Override
	public boolean isDebugEnabled() {
		return experimentArguments.isDebugEnabled();
	}
	
	@Override
	public BoundOutputManagerRouteErrors getOutputManager() {
		return outputManager;
	}
	
	@Override
	public LogErrorReporter getLogger() {
		return logger;
	}

	/** Exposed as {@link StatefulLogReporter} rather than as {@link LogReporter} that is found in {@link LogErrorReporter} */
	public StatefulLogReporter getStatefulLogReporter() {
		return logReporter;
	}

	public ExperimentExecutionArguments getExperimentArguments() {
		return experimentArguments;
	}
}
