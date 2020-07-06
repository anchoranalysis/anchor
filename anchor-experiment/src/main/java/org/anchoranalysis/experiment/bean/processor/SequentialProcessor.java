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
import java.util.Optional;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.experiment.task.processor.MonitoredSequentialExecutor;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * Executes jobs sequentially
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 * @param <S> shared-object type
 */
public class SequentialProcessor<T extends InputFromManager,S> extends JobProcessor<T,S> {
	
	@Override
	protected TaskStatistics execute(
		BoundOutputManagerRouteErrors rootOutputManager,
		List<T> inputObjects,
		ParametersExperiment paramsExperiment
	) throws ExperimentExecutionException {
		
		S sharedState = getTask().beforeAnyJobIsExecuted( rootOutputManager, paramsExperiment );
		
		TaskStatistics stats = executeAllJobs(
			inputObjects,
			sharedState,
			paramsExperiment,
			loggerForMonitor(paramsExperiment)
		);
		
		getTask().afterAllJobsAreExecuted( sharedState, paramsExperiment.getContext() );
		
		return stats;
	}
	
	private TaskStatistics executeAllJobs(
		List<T> inputObjects,
		S sharedState,
		ParametersExperiment paramsExperiment,
		Optional<MessageLogger> loggerMonitor
	) throws ExperimentExecutionException {
		
		MonitoredSequentialExecutor<T> seqExecutor = new MonitoredSequentialExecutor<>(
			obj -> executeJobAndLog( obj, sharedState, paramsExperiment ),
			T::descriptiveName,
			loggerMonitor,
			false
		);
		
		return seqExecutor.executeEachWithMonitor("Job: ", inputObjects);
	}
	
	private boolean executeJobAndLog( T inputObj, S sharedState, ParametersExperiment paramsExperiment ) {
		
		MessageLogger logger = paramsExperiment.getLoggerExperiment();
		ErrorReporter errorReporter = new ErrorReporterIntoLog(logger);

		try {
			ParametersUnbound<T,S> paramsUnbound = new ParametersUnbound<>(
				paramsExperiment,
				inputObj,
				sharedState,
				isSuppressExceptions()
			);
			return getTask().executeJob( paramsUnbound );
						
		} catch (JobExecutionException e) {
			errorReporter.recordError(SequentialProcessor.class, e);
			return false;
		}
	}
}
