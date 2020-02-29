package org.anchoranalysis.experiment.task;

import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.LogReporter;

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

import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.bean.logreporter.LogReporterBean;
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * Parameters for executing a task, when the manifest, log etc. are still
 *   bound to the experiment
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 */
public class ParametersExperiment {

	// Parameters for all tasks in general (the experiment)
	private ManifestRecorder experimentalManifest;
	private BoundOutputManagerRouteErrors outputManager;
	private String experimentIdentifier;
	
	// This is an actual log-reporter
	private StatefulLogReporter logReporterExperiment;
	
	// This is a means to create new log-reporters for each task
	private LogReporterBean logReporterTaskCreator;
	
	private ExperimentExecutionArguments experimentArguments;
		
	/**
	 * Iff true, additional log messages are written to describe each job in terms of its unique name,
	 *  output folder, average execution time etc.
	 */
	private boolean detailedLogging;
	
	public ParametersExperiment(
		ExperimentExecutionArguments experimentArguments,
		String experimentIdentifier,
		ManifestRecorder experimentalManifest,
		BoundOutputManager outputManager,
		StatefulLogReporter logReporterExperiment,
		boolean detailedLogging
	) {
		this.experimentArguments = experimentArguments;
		this.experimentIdentifier = experimentIdentifier;
		this.experimentalManifest = experimentalManifest;
		this.detailedLogging = detailedLogging;
		this.outputManager = wrapErrors(outputManager, logReporterExperiment);
		this.logReporterExperiment = logReporterExperiment;
	}
	
	public void setLogReporterTaskCreator(LogReporterBean logReporterTaskCreator) {
		this.logReporterTaskCreator = logReporterTaskCreator;
	}
	
	
	public boolean isDetailedLogging() {
		return detailedLogging;
	}
	
	public ManifestRecorder getExperimentalManifest() {
		return experimentalManifest;
	}

	public BoundOutputManagerRouteErrors getOutputManager() {
		return outputManager;
	}
	
	public StatefulLogReporter getLogReporterExperiment() {
		return logReporterExperiment;
	}
	
	public ExperimentExecutionArguments getExperimentArguments() {
		return experimentArguments;
	}

	public LogReporterBean getLogReporterTaskCreator() {
		return logReporterTaskCreator;
	}

	public String getExperimentIdentifier() {
		return experimentIdentifier;
	}
	
	/** Redirects any output-errors into the log */
	private static BoundOutputManagerRouteErrors wrapErrors( BoundOutputManager rootOutputManagerNoErrors, LogReporter logReporter ) {
		return new BoundOutputManagerRouteErrors(
			rootOutputManagerNoErrors,
			new ErrorReporterIntoLog( logReporter )
		);
	}
}
