package org.anchoranalysis.experiment.task;

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

import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.bean.identifier.ExperimentIdentifier;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bean.OutputManager;

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
	private OutputManager outputManager;
	private ExperimentIdentifier experimentIdentifier;
	private LogReporter logReporterExperiment;
	private ExperimentExecutionArguments experimentArguments;
	
	/**
	 * Iff true, additional log messages are written to describe each job in terms of its unique name,
	 *  output folder, average execution time etc.
	 */
	private boolean detailedLogging;
	
	public boolean isDetailedLogging() {
		return detailedLogging;
	}
	public void setDetailedLogging(boolean detailedLogging) {
		this.detailedLogging = detailedLogging;
	}
	
	public ManifestRecorder getExperimentalManifest() {
		return experimentalManifest;
	}
	public void setExperimentalManifest(ManifestRecorder experimentalManifest) {
		this.experimentalManifest = experimentalManifest;
	}
	public OutputManager getOutputManager() {
		return outputManager;
	}
	public void setOutputManager(OutputManager outputManager) {
		this.outputManager = outputManager;
	}
	public ExperimentIdentifier getExperimentIdentifier() {
		return experimentIdentifier;
	}
	public void setExperimentIdentifier(ExperimentIdentifier experimentIdentifier) {
		this.experimentIdentifier = experimentIdentifier;
	}
	public LogReporter getLogReporterExperiment() {
		return logReporterExperiment;
	}
	public void setLogReporterExperiment(LogReporter logReporterExperiment) {
		this.logReporterExperiment = logReporterExperiment;
	}
	public ExperimentExecutionArguments getExperimentArguments() {
		return experimentArguments;
	}
	public void setExperimentArguments(ExperimentExecutionArguments experimentArguments) {
		this.experimentArguments = experimentArguments;
	}

}
