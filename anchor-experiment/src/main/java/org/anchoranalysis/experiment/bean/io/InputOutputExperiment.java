package org.anchoranalysis.experiment.bean.io;

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


import java.io.IOException;
import java.util.List;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.processor.JobProcessor;
import org.anchoranalysis.experiment.io.IReplaceInputManager;
import org.anchoranalysis.experiment.io.IReplaceOutputManager;
import org.anchoranalysis.experiment.io.IReplaceTask;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.Task;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 * @param <S> shared-state for job
 */
public class InputOutputExperiment<T extends InputFromManager,S> extends OutputExperiment implements IReplaceInputManager, IReplaceOutputManager, IReplaceTask<T,S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7738798921339761863L;

	// START BEAN PROPERTIES
	@BeanField
	private InputManager<T> inputManager = null;
	
	@BeanField
	private JobProcessor<T,S> taskProcessor;
	// END BEAN PROPERTIES
	
	@Override
	protected void execExperiment( BoundOutputManagerRouteErrors outputManager, ManifestRecorder experimentalManifest, ExperimentExecutionArguments expArgs, LogReporter logReporter ) throws ExperimentExecutionException {
		
		try {
			List<T> inputObjects = getInput().inputObjects(
				expArgs.createInputContext(),
				ProgressReporterNull.get()
			);
			
			ParametersExperiment params = new ParametersExperiment();
			params.setExperimentalManifest(experimentalManifest);
			params.setOutputManager(getOutput());
			params.setExperimentIdentifier(getExperimentIdentifier());
			params.setExperimentArguments(expArgs);
			params.setLogReporterExperiment(logReporter);
			params.setDetailedLogging( useDetailedLogging() );
			
			taskProcessor.executeLogStats(
				outputManager,
				inputObjects,
				params
			);
			
		} catch (IOException | DeserializationFailedException e) {
			throw new ExperimentExecutionException(e);
		}
	}
	
	@Override
	public boolean useDetailedLogging() {

		// Disable detailed-logging if the task has a very quick execution (unless we are in 'force' mode)
		if ( isForceDetailedLogging() ||  !taskProcessor.hasVeryQuickPerInputExecution()) {
			return true;
		}
		
		return super.useDetailedLogging();
	}

	public InputManager<T> getInput() {
		return inputManager;
	}


	public void setInput(InputManager<T> input) {
		this.inputManager = input;
	}
	
	public JobProcessor<T,S> getTaskProcessor() {
		return taskProcessor;
	}

	public void setTaskProcessor(JobProcessor<T,S> taskProcessor) {
		this.taskProcessor = taskProcessor;
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public void replaceInputManager(InputManager<?> inputManager) throws OperationFailedException {
		this.inputManager = (InputManager<T>) inputManager;
	}
	
	@Override
	public void replaceOutputManager(OutputManager outputManager) throws OperationFailedException {
		this.setOutput(outputManager);
	}

	@Override
	public void replaceTask(Task<T, S> task) throws OperationFailedException {
		this.taskProcessor.setTask(task);
		
	}
}
