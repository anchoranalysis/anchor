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

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * Parameters for executing a task, when the manifest, log etc. have
 *   become bound to the task specifically
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 * @aram <S> shared-state type
 */
public class ParametersBound<T,S> {

	private ManifestRecorder manifest;
	private BoundOutputManagerRouteErrors outputManager;
	private LogErrorReporter logErrorReporter;
	private T inputObject;
	private ExperimentExecutionArguments experimentArguments;
	private S sharedState;
	private StatefulLogReporter logReporterJob;
	private boolean detailedLogging;

	/** Immutably changes the input-object */
	public <U> ParametersBound<U,S> changeInputObject( U inputObjectNew ) {
		ParametersBound<U,S> out = new ParametersBound<U,S>();
		out.setManifest(manifest);
		out.setOutputManager(outputManager);
		out.setLogErrorReporter(logErrorReporter);
		out.setExperimentArguments(experimentArguments);
		out.setSharedState(sharedState);
		out.setLogReporterJob(logReporterJob);
		out.setDetailedLogging(detailedLogging);
		
		// The new input-object
		out.setInputObject(inputObjectNew);
		return out;
	}
	
	public boolean isDetailedLogging() {
		return detailedLogging;
	}

	public void setDetailedLogging(boolean detailedLogging) {
		this.detailedLogging = detailedLogging;
	}

	public ManifestRecorder getManifest() {
		return manifest;
	}

	public void setManifest(ManifestRecorder manifest) {
		this.manifest = manifest;
	}

	public BoundOutputManagerRouteErrors getOutputManager() {
		return outputManager;
	}

	public void setOutputManager(BoundOutputManagerRouteErrors outputManager) {
		this.outputManager = outputManager;
	}

	public LogErrorReporter getLogErrorReporter() {
		return logErrorReporter;
	}

	public void setLogErrorReporter(LogErrorReporter logErrorReporter) {
		this.logErrorReporter = logErrorReporter;
	}

	public T getInputObject() {
		return inputObject;
	}

	public void setInputObject(T inputObject) {
		this.inputObject = inputObject;
	}

	public ExperimentExecutionArguments getExperimentArguments() {
		return experimentArguments;
	}

	public void setExperimentArguments(ExperimentExecutionArguments experimentArguments) {
		this.experimentArguments = experimentArguments;
	}

	public S getSharedState() {
		return sharedState;
	}

	public void setSharedState(S sharedState) {
		this.sharedState = sharedState;
	}

	public StatefulLogReporter getLogReporterJob() {
		return logReporterJob;
	}

	public void setLogReporterJob(StatefulLogReporter logReporterJob) {
		this.logReporterJob = logReporterJob;
	}	
}
