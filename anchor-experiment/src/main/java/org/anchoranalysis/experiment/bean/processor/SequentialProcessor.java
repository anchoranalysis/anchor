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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.experiment.task.Task;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.experiment.task.processor.MonitoredSequentialExecutor;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 *  
 * @author Owen Feehan
 *
 * @param <T> input-object type
 * @param <S> shared-object type
 */
public class SequentialProcessor<T extends InputFromManager,S> extends JobProcessor<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN
	@BeanField
	private Task<T,S> task;
	
	@BeanField
	private boolean supressExceptions = true;
	// END BEAN
	
	@Override
	protected TaskStatistics execute(
		BoundOutputManagerRouteErrors rootOutputManager,
		List<T> inputObjects,
		ParametersExperiment paramsExperiment
	) throws ExperimentExecutionException {
		
		S sharedState = task.beforeAnyJobIsExecuted( rootOutputManager, paramsExperiment );
		
		int totalNumJobs = inputObjects.size();
		
		TaskStatistics stats = executeAllJobs(
			inputObjects,
			sharedState,
			paramsExperiment,
			totalNumJobs,
			logReporterForMonitor(paramsExperiment)
		);
		
		task.afterAllJobsAreExecuted( rootOutputManager, sharedState, paramsExperiment.getLogReporterExperiment());
		
		return stats;
	}
	
	private TaskStatistics executeAllJobs(
		List<T> inputObjects,
		S sharedState,
		ParametersExperiment paramsExperiment,
		int totalNumJobs,
		LogReporter logReporterMonitor
	) throws ExperimentExecutionException {
		
		MonitoredSequentialExecutor<T> seqExecutor = new MonitoredSequentialExecutor<T>(
			obj -> executeJobAndLog( obj, sharedState, paramsExperiment ),
			obj -> obj.descriptiveName(),
			logReporterMonitor,
			false
		);
		
		return seqExecutor.executeEachWithMonitor("Job: ", inputObjects);
	}
	
	private boolean executeJobAndLog( T inputObj, S sharedState, ParametersExperiment paramsExperiment ) {
		
		LogReporter logReporter = paramsExperiment.getLogReporterExperiment();
		ErrorReporter errorReporter = new ErrorReporterIntoLog(logReporter);

		try {
			ParametersUnbound<T,S> paramsUnbound = new ParametersUnbound<>(paramsExperiment);
			paramsUnbound.setInputObject(inputObj);
			paramsUnbound.setSharedState(sharedState);
			paramsUnbound.setSupressExceptions(supressExceptions);
			
			return task.executeJob( paramsUnbound );
						
		} catch (JobExecutionException e) {
			errorReporter.recordError(SequentialProcessor.class, e);
			return false;
		}
	}

	public Task<T,S> getTask() {
		return task;
	}

	public void setTask(Task<T,S> task) {
		this.task = task;
	}

	public boolean isSupressExceptions() {
		return supressExceptions;
	}

	public void setSupressExceptions(boolean supressExceptions) {
		this.supressExceptions = supressExceptions;
	}

	@Override
	public boolean hasVeryQuickPerInputExecution() {
		return task.hasVeryQuickPerInputExecution();
	}	
}
