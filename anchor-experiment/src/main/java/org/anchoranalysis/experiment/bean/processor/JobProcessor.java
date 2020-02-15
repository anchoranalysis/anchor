package org.anchoranalysis.experiment.bean.processor;

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

import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.Task;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;


/**
 * Processes a job
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 * @param <S> shared-state type
 */
public abstract class JobProcessor<T extends InputFromManager,S> extends AnchorBean<JobProcessor<T,S>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private Task<T,S> task;
	// END BEAN PROPERTIES
		
	/**
	 * Executes the tasks, gathers statistics, and logs them
	 * 
	 * @param rootOutputManager
	 * @param inputObjects
	 * @param paramsExperiment
	 * @return
	 * @throws ExperimentExecutionException
	 */
	public void executeLogStats(
		BoundOutputManagerRouteErrors rootOutputManager,
		List<T> inputObjects,
		ParametersExperiment paramsExperiment
	) throws ExperimentExecutionException {
		TaskStatistics stats = execute(rootOutputManager, inputObjects, paramsExperiment);
		
		if (paramsExperiment.isDetailedLogging()) {
			logStats(stats, paramsExperiment);
		}
	}
	
	/** Is the execution-time of the task per-input expected to be very quick to execute? */
	public boolean hasVeryQuickPerInputExecution() {
		return task.hasVeryQuickPerInputExecution();
	}
	
	/**
	 * The job processor is expected to remove items from the inputObjects List as they are consumed
	 * so as to allow garbage-collection of these items before all jobs are processed (as the list might
	 * be quite large).
	 * 
	 * @param rootOutputManager
	 * @param inputObjects
	 * @param paramsExperiment
	 * @return
	 * @throws ExperimentExecutionException
	 */
	protected abstract TaskStatistics execute(
		BoundOutputManagerRouteErrors rootOutputManager,
		List<T> inputObjects,
		ParametersExperiment paramsExperiment
	) throws ExperimentExecutionException;
		
	protected LogReporter logReporterForMonitor(ParametersExperiment paramsExperiment) {
		if (paramsExperiment.isDetailedLogging()) {
			return paramsExperiment.getLogReporterExperiment();
		} else {
			return null;
		}
	}
		
	private static void logStats( TaskStatistics stats, ParametersExperiment paramsExperiment ) {
		StatisticsLogger statisticsLogger = new StatisticsLogger(
			paramsExperiment.getLogReporterExperiment()
		);
		statisticsLogger.logTextualMessage(stats);		
	}
	
	public Task<T,S> getTask() {
		return task;
	}

	public void setTask(Task<T,S> task) {
		this.task = task;
	}
}
