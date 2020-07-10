package org.anchoranalysis.experiment.bean.log;

/*-
 * #%L
 * anchor-experiment
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

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.reporter.FailureOnlyMessageLogger;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Logs to a text file like with {@link org.anchoranalysis.experiment.bean.log.ToTextFile}
 * but the log is ONLY written if a failure occurs in the experiment.
 * <p>
 * If no failure, occurs no file is outputted on the filesystem at all.
 * <p>
 * This is a convenient means of avoiding write and sotrage costs of files of perhaps
 * minimal value if nothing went wrong.
 * 
 * @author Owen Feehan
  */
public class ToTextFileOnlyIfFailure extends ToTextFileBase {

	@Override
	public StatefulMessageLogger create(
		BoundOutputManager outputManager,
		ErrorReporter errorReporter,
		ExperimentExecutionArguments arguments,
		boolean detailedLogging
	) {
		return new FailureOnlyMessageLogger(getOutputName(), outputManager, errorReporter);
	}
}