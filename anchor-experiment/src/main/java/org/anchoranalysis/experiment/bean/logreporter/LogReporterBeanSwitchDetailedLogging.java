package org.anchoranalysis.experiment.bean.logreporter;

/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/**
 * Switches between two log-reporters depending on whether detailed logging is switched on or not
 * 
 * @author Owen Feehan
 *
 */
public class LogReporterBeanSwitchDetailedLogging extends LogReporterBean {
	
	// START BEAN PROPERTIES
	@BeanField
	private LogReporterBean detailed;
	
	@BeanField
	private LogReporterBean notDetailed;
	// END BEAN PROPERTIES

	@Override
	public StatefulLogReporter create(String outputName, BoundOutputManager bom,
			ErrorReporter errorReporter, ExperimentExecutionArguments expArgs, boolean detailedLogging) {
		if (detailedLogging) {
			return detailed.create(outputName, bom, errorReporter, expArgs, detailedLogging);
		} else {
			return notDetailed.create(outputName, bom, errorReporter, expArgs, detailedLogging);
		}
	}

	public LogReporterBean getDetailed() {
		return detailed;
	}

	public void setDetailed(LogReporterBean detailed) {
		this.detailed = detailed;
	}

	public LogReporterBean getNotDetailed() {
		return notDetailed;
	}

	public void setNotDetailed(LogReporterBean notDetailed) {
		this.notDetailed = notDetailed;
	}

}
