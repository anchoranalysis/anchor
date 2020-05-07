package org.anchoranalysis.experiment.task;

import org.anchoranalysis.core.error.reporter.ErrorReporter;

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
import org.anchoranalysis.io.output.bound.BoundIOContext;

/**
 * Input for executing a task, associated with shared-state and other parameters. 
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 * @aram <S> shared-state type
 */
public class InputBound<T,S> {

	private ManifestRecorder manifest;
	
	private T inputObject;
	private S sharedState;
	
	private boolean detailedLogging;
	
	private BoundContextSpecify context;
	
	public InputBound(
		ExperimentExecutionArguments experimentArguments,
		BoundOutputManagerRouteErrors outputManager,
		StatefulLogReporter logReporter,
		ErrorReporter errorReporter
	) {
		this.context = new BoundContextSpecify(
			experimentArguments,
			outputManager,
			logReporter,
			errorReporter
		);
	}
	
	private InputBound(BoundContextSpecify context) {
		this.context = context;
	}

	/** Immutably changes the input-object */
	public <U> InputBound<U,S> changeInputObject( U inputObjectNew ) {
		InputBound<U,S> out = new InputBound<U,S>(context);
		out.setManifest(manifest);
		out.setSharedState(sharedState);
		out.setDetailedLogging(detailedLogging);
		
		// The new input-object
		out.setInputObject(inputObjectNew);
		return out;
	}

	public BoundIOContext context() {
		return context;
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
		return context.getOutputManager();
	}
	
	public LogErrorReporter getLogger() {
		return context.getLogger();
	}

	public T getInputObject() {
		return inputObject;
	}

	public void setInputObject(T inputObject) {
		this.inputObject = inputObject;
	}

	public S getSharedState() {
		return sharedState;
	}

	public void setSharedState(S sharedState) {
		this.sharedState = sharedState;
	}

	public StatefulLogReporter getLogReporterJob() {
		return context.getStatefulLogReporter();
	}
}
