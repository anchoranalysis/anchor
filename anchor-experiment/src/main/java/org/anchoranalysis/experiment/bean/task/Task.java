/*-
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.experiment.bean.task;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.concurrency.ConcurrencyPlan;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.memory.MemoryUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.experiment.task.BoundContextSpecify;
import org.anchoranalysis.experiment.task.ErrorReporterForTask;
import org.anchoranalysis.experiment.task.InputBound;
import org.anchoranalysis.experiment.task.InputTypesExpected;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.io.generator.serialized.ObjectOutputStreamGenerator;
import org.anchoranalysis.io.generator.serialized.XStreamGenerator;
import org.anchoranalysis.io.generator.text.StringGenerator;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.OutputterChecked;
import org.anchoranalysis.io.output.bound.Outputter;
import org.anchoranalysis.io.output.writer.WriterRouterErrors;
import org.apache.commons.lang.time.StopWatch;

/**
 * A task which performs some kind of processing on a specific input-object
 *
 * <p>We distinguish between ParametersUnbound which are parameters generally used for tasks in an
 * experiment and ParametersBound which is created in a further step, when several of these
 * parameters are replaced with new more specific-objects for the specific task.
 *
 * <p>e.g. we move from a logger and manifest for the experiment as a whole, to a logger and
 * manifest for the task itself
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-state type
 */
public abstract class Task<T extends InputFromManager, S> extends AnchorBean<Task<T, S>> {

    private static final String OUTPUT_NAME_MANIFEST = "manifest";
    private static final String OUTPUT_NAME_EXECUTION_TIME = "executionTime";

    /** Is the execution-time of the task per-input expected to be very quick to execute? */
    public abstract boolean hasVeryQuickPerInputExecution();

    /**
     * Called <i>once</i> before all calls to {@link #executeJob}.
     *
     * @param outputter the output-manager for the experiment (not for an individual job)
     * @param concurrencyPlan available numbers of processors that can call {@link #executeJob}
     * @param params the experiment-parameters
     * @return the shared-state that is passed to each call to {@link #executeJob} and to {@link
     *     #afterAllJobsAreExecuted}.
     * @throws ExperimentExecutionException
     */
    public abstract S beforeAnyJobIsExecuted(
            Outputter outputter,
            ConcurrencyPlan concurrencyPlan,
            ParametersExperiment params)
            throws ExperimentExecutionException;

    /**
     * Runs the task on one particular input (a job).
     *
     * @param paramsUnbound parameters for the input (unbound to any output location)
     * @return whether the job finished successfully or not
     * @throws JobExecutionException if anything goes wrong with the job which is <b>not</b> logged.
     */
    public boolean executeJob(ParametersUnbound<T, S> paramsUnbound) throws JobExecutionException {
        
        ManifestRecorder manifestTask = new ManifestRecorder();

        // Bind an outputter for the task
        OutputterChecked outputterTask =
                HelperBindOutputManager.createOutputterForTask(
                        paramsUnbound.getInputObject(),
                        Optional.of(manifestTask),
                        paramsUnbound.getParametersExperiment());
        Preconditions.checkArgument(outputterTask.getSettings().hasBeenInit());

        // Create bound parameters
        InputBound<T, S> paramsBound =
                bindOtherParams(paramsUnbound, outputterTask, manifestTask);
        return executeJobLogExceptions(paramsBound, paramsUnbound.isSuppressExceptions());
    }

    /**
     * Called <i>once</i> after all calls to {@link #executeJob}.
     *
     * @param sharedState the shared-state
     * @param context IO-context for experiment (not for an individual job)
     * @throws ExperimentExecutionException
     */
    public abstract void afterAllJobsAreExecuted(S sharedState, BoundIOContext context)
            throws ExperimentExecutionException;

    /** Is an input-object compatible with this particular task? */
    public boolean isInputObjectCompatibleWith(Class<? extends InputFromManager> inputObjectClass) {
        return inputTypesExpected().doesClassInheritFromAny(inputObjectClass);
    }

    /**
     * Highest class(es) that will function as a valid input.
     *
     * <p>This is usually the class of T (or sometimes the absolute base class InputFromManager)
     */
    public abstract InputTypesExpected inputTypesExpected();

    /**
     * Performs the task on a particular input.
     * 
     * @param input the input
     * @throws JobExecutionException
     */
    public abstract void doJobOnInput(InputBound<T, S> input) throws JobExecutionException;

    /**
     * Creates other objects needed to have a fully bound set of parameters for the task
     *
     * @param paramsUnbound parameters before being bound for a specific task
     * @param outputterTaskChecked a bound output manager for the task
     * @param manifestTask a bound manifest for the task
     * @return a complete ParametersBound object with all parameters set to objects bound for the
     *     specific task
     */
    private InputBound<T, S> bindOtherParams(
            ParametersUnbound<T, S> paramsUnbound,
            OutputterChecked outputterTaskChecked,
            ManifestRecorder manifestTask) {

        // We create a new log reporter for this job only
        StatefulMessageLogger loggerJob =
                createJobLog(paramsUnbound.getParametersExperiment(), outputterTaskChecked);

        ErrorReporter errorReporterJob = new ErrorReporterForTask(loggerJob);

        // We initialise the output manager
        Outputter outputterTask = new Outputter(outputterTaskChecked, errorReporterJob);

        // We create new parameters bound specifically to the job
        return new InputBound<>(
                paramsUnbound.getInputObject(),
                paramsUnbound.getSharedState(),
                manifestTask,
                paramsUnbound.getParametersExperiment().isDetailedLogging(),
                new BoundContextSpecify(
                        paramsUnbound.getParametersExperiment().getExperimentArguments(),
                        outputterTask,
                        loggerJob,
                        errorReporterJob));
    }

    private StatefulMessageLogger createJobLog(
            ParametersExperiment params, OutputterChecked outputterTask) {
        return params.getLoggerTaskCreator()
                .createWithLogFallback(
                        outputterTask,
                        params.getLoggerExperiment(),
                        params.getExperimentArguments(),
                        params.isDetailedLogging());
    }

    private boolean executeJobLogExceptions(InputBound<T, S> params, boolean suppressExceptions)
            throws JobExecutionException {

        StatefulMessageLogger loggerJob = params.getLogReporterJob();

        StopWatch stopWatchFile = new StopWatch();
        stopWatchFile.start();

        boolean successfullyFinished = false;
        try {
            loggerJob.start();

            if (params.isDetailedLogging()) {

                params.getLogger()
                        .messageLogger()
                        .logFormatted(
                                "Output Folder has path: \t%s",
                                params.getOutputter().getOutputFolderPath().toString());

                loggerJob.logFormatted(
                        "File processing started: %s", params.getInputObject().descriptiveName());
            }

            executeJobAdditionalOutputs(params, stopWatchFile);

            successfullyFinished = true;

        } catch (Throwable e) { // NOSONAR
            // We need to catch both exceptions and errors in order to recover from failure in
            // the specific task. Other tasks will continue executing.
            params.getLogger().errorReporter().recordError(Task.class, e);
            loggerJob.log(
                    "This error was fatal. The specific job will end early, but the experiment will otherwise continue.");
            if (!suppressExceptions) {
                throw new JobExecutionException("Job encountered a fatal error", e);
            }

        } finally {

            stopWatchFile.stop();

            if (params.isDetailedLogging()) {
                loggerJob.logFormatted(
                        "File processing ended:   %s (time taken = %ds)",
                        params.getInputObject().descriptiveName(), stopWatchFile.getTime() / 1000);
                MemoryUtilities.logMemoryUsage("End file processing", loggerJob);
            }

            loggerJob.close(successfullyFinished);
        }
        return successfullyFinished;
    }

    private void executeJobAdditionalOutputs(InputBound<T, S> params, StopWatch stopWatchFile)
            throws JobExecutionException {

        try {
            doJobOnInput(params);
        } catch (ClassCastException e) {
            throw new JobExecutionException(
                    "Could not cast one class to another. Have you used a compatible input-manager for the task?",
                    e);
        } finally {
            // We close the input objects as soon as the task is completed, so as to free up file
            // handles
            // NB Deal with this in the future... if a task is never called, then close() might
            // never be called on the InputObject
            params.getInputObject().close(params.getLogger().errorReporter());
        }

        WriterRouterErrors writeIfAllowed = params.getOutputter().writerSelective();
        writeIfAllowed.write(
                OUTPUT_NAME_MANIFEST,
                () ->
                        new XStreamGenerator<Object>(
                                params.getManifest(),
                                Optional.empty()) // Don't put into the manifest
                );
        writeIfAllowed.write(
                OUTPUT_NAME_MANIFEST,
                () ->
                        new ObjectOutputStreamGenerator<>(
                                params.getManifest(),
                                Optional.empty() // Don't put into the manifest
                                ));
        // This is written after the manfiests are already written, so it won't exist in the
        // manifest
        writeIfAllowed.write(
                OUTPUT_NAME_EXECUTION_TIME,
                () -> new StringGenerator(Long.toString(stopWatchFile.getTime())));
    }
}
