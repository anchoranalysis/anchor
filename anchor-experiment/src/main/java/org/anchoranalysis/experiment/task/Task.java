package org.anchoranalysis.experiment.task;



import java.util.Optional;

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



import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.error.reporter.ErrorReporterIntoLog;
import org.anchoranalysis.core.memory.MemoryUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;
import org.anchoranalysis.io.generator.serialized.ObjectOutputStreamGenerator;
import org.anchoranalysis.io.generator.serialized.XStreamGenerator;
import org.anchoranalysis.io.generator.text.StringGenerator;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.writer.WriterRouterErrors;
import org.apache.commons.lang.time.StopWatch;


/**
 * A task which performs some kind of processing on a specific input-object
 * 
 * We distinguish between ParametersUnbound which are parameters generally used for tasks in 
 *  an experiment and ParametersBound which is created in a further step, when several
 *  of these parameters are replaced with new more specific-objects for the specific task.
 *  
 *  e.g. we move from a logger and manifest for the experiment as a whole, to a logger
 *      and manifest for the task itself 
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 * @param <S> shared-state type
 */
public abstract class Task<T extends InputFromManager, S> extends AnchorBean<Task<T,S>> {

	// START BEAN
	@BeanField
	private String outputNameManifest = "manifest";
	
	@BeanField
	private String outputNameExecutionTime = "executionTime";
	// END BEAN
	
	/** Is the execution-time of the task per-input expected to be very quick to execute? */
	public abstract boolean hasVeryQuickPerInputExecution();
	
	// Return object is the shared state if it exists
	public abstract S beforeAnyJobIsExecuted( BoundOutputManagerRouteErrors outputManager, ParametersExperiment params ) throws ExperimentExecutionException;
	
	public abstract void afterAllJobsAreExecuted( S sharedState, BoundIOContext context ) throws ExperimentExecutionException;
	
	// Runs the experiment on a particular file
	public boolean executeJob(	ParametersUnbound<T,S> paramsUnbound ) throws JobExecutionException {
		
		assert paramsUnbound.getParametersExperiment().getLogReporterExperiment() != null;
		
		ManifestRecorder manifestTask = new ManifestRecorder();
		
		// Bind an outputManager for the task
		BoundOutputManager outputManagerTask = HelperBindOutputManager.createOutputManagerForTask(
			paramsUnbound.getInputObject(),
			Optional.of(manifestTask),
			paramsUnbound.getParametersExperiment()
		);
		
		assert(outputManagerTask.getOutputWriteSettings().hasBeenInit());	
		
		// Create other bound arguments
		
		InputBound<T,S> paramsBound = bindOtherParams( paramsUnbound, outputManagerTask, manifestTask );
		return executeJobLogExceptions( paramsBound, paramsUnbound.isSupressExceptions() );
		
	}
	
	/** Is an input-object compatible with this particular task? */
	public boolean isInputObjectCompatibleWith( Class<? extends InputFromManager> inputObjectClass ) {
		return inputTypesExpected().doesClassInheritFromAny(inputObjectClass);
	}
	
	/** Highest class(es) that will function as a valid input.
	 * 
	 * <p>This is usually the class of T (or sometimes the absolute base class InputFromManager)</p>
	 **/
	public abstract InputTypesExpected inputTypesExpected();
		
	public abstract void doJobOnInputObject( InputBound<T,S> params ) throws JobExecutionException;
	
	
	/**
	 * Creates other objects needed to have a fully bound set of parameters for the task
	 * 
	 * @param paramsUnbound parameters before being bound for a specific task
	 * @param outputManagerTask a bound output manager for the task
	 * @param manifestTask a bound manifest for the task
	 * @return a complete ParametersBound object with all parameters set to objects bound for the specific task
	 */
	private InputBound<T,S> bindOtherParams( ParametersUnbound<T,S> paramsUnbound, BoundOutputManager outputManagerTask, ManifestRecorder manifestTask ) {
		
		// We create a new log reporter for this job only
		ErrorReporter errorReporterFallback = new ErrorReporterIntoLog( paramsUnbound.getParametersExperiment().getLogReporterExperiment() );
		StatefulLogReporter logReporterJob = paramsUnbound.getParametersExperiment().getLogReporterTaskCreator().create(
			"job_log",
			outputManagerTask,
			errorReporterFallback,
			paramsUnbound.getParametersExperiment().getExperimentArguments(), paramsUnbound.getParametersExperiment().isDetailedLogging()
		);
		
		ErrorReporter errorReporterJob = new ErrorReporterIntoLog(logReporterJob);
		
		// We initialise the output manager
		BoundOutputManagerRouteErrors outputManagerTaskRouteErrors = new BoundOutputManagerRouteErrors(
			outputManagerTask,
			errorReporterJob
		);
		
		// We create new parameters bound specifically to the job
		InputBound<T,S> paramsBound = new InputBound<>(
			paramsUnbound.getParametersExperiment().getExperimentArguments(),
			outputManagerTaskRouteErrors,
			logReporterJob,
			errorReporterJob
		);
		paramsBound.setInputObject(paramsUnbound.getInputObject());
		paramsBound.setSharedState(paramsUnbound.getSharedState());
		paramsBound.setManifest(manifestTask);
		paramsBound.setDetailedLogging(paramsUnbound.isDetailedLogging());
		return paramsBound;
	}
	
	
	private boolean executeJobLogExceptions(
		InputBound<T,S> params,
		boolean supressExceptions
	) throws JobExecutionException {
		
		StatefulLogReporter logReporterJob = params.getLogReporterJob();
		
		StopWatch stopWatchFile = new StopWatch();
		stopWatchFile.start();
		
		boolean successfullyFinished = false;
		try {
			logReporterJob.start();
			
			if (params.isDetailedLogging()) {
				
				params.getLogger().getLogReporter().logFormatted(
					"Output Folder has path: \t%s",
					params.getOutputManager().getOutputFolderPath().toString()
				);
				
				logReporterJob.logFormatted("File processing started: %s", params.getInputObject().descriptiveName());
			}
			
			executeJobAdditionalOutputs( params, stopWatchFile );
			
			successfullyFinished = true;
			
		} catch (Exception e) {
			params.getLogger().getErrorReporter().recordError(Task.class, e);
			logReporterJob.log("This error was fatal. The specific job will end early, but the experiment will otherwise continue.");
			if (!supressExceptions) {
				throw new JobExecutionException("Job encountered a fatal error", e);	
			}
			
		} finally {
			
			stopWatchFile.stop();
			
			if (params.isDetailedLogging()) {
				logReporterJob.logFormatted("File processing ended:   %s (time taken = %ds)", params.getInputObject().descriptiveName(), stopWatchFile.getTime()/1000);
				MemoryUtilities.logMemoryUsage("End file processing", logReporterJob );
			}
						
			logReporterJob.close(successfullyFinished);
		}
		return successfullyFinished;
	}
	
	
	private void executeJobAdditionalOutputs(
		InputBound<T,S> params,
		StopWatch stopWatchFile
	) throws JobExecutionException {

		try {
			doJobOnInputObject( params );
		} catch (ClassCastException e) {
			throw new JobExecutionException("Could not cast one class to another. Have you used a compatible input-manager for the task?", e);
		} finally {
			// We close the input objects as soon as the task is completed, so as to free up file handles
			// NB Deal with this in the future... if a task is never called, then close() might never be called on the InputObject
			params.getInputObject().close( params.getLogger().getErrorReporter() );
		}
		
		WriterRouterErrors writeIfAllowed = params.getOutputManager().getWriterCheckIfAllowed(); 
		writeIfAllowed.write(
			outputNameManifest,
			() -> new XStreamGenerator<Object>(
				params.getManifest(),
				Optional.empty())	// Don't put into the manifest
		);
		writeIfAllowed.write(
			outputNameManifest,
			() -> new ObjectOutputStreamGenerator<>(
				params.getManifest(),
				Optional.empty()	// Don't put into the manifest
			)
		);
		
		// This is written after the manfiests are already written, so it won't exist in the manifest
		writeIfAllowed.write(
			outputNameExecutionTime,
			() -> new StringGenerator( Long.toString(stopWatchFile.getTime()) )
		);
	}
	
	// START: public
	
	public String getOutputNameManifest() {
		return outputNameManifest;
	}

	public void setOutputNameManifest(String outputNameManifest) {
		this.outputNameManifest = outputNameManifest;
	}

	public String getOutputNameExecutionTime() {
		return outputNameExecutionTime;
	}

	public void setOutputNameExecutionTime(String outputNameExecutionTime) {
		this.outputNameExecutionTime = outputNameExecutionTime;
	}
	// END: public
}
