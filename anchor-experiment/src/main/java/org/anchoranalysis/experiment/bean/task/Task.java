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
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.system.MemoryUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.bean.io.InputOutputExperiment;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.experiment.task.ErrorReporterForTask;
import org.anchoranalysis.experiment.task.InputBound;
import org.anchoranalysis.experiment.task.InputOutputContextStateful;
import org.anchoranalysis.experiment.task.InputTypesExpected;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.inference.concurrency.ConcurrencyPlan;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bean.enabled.IgnoreUnderscorePrefix;
import org.anchoranalysis.io.output.enabled.OutputEnabledMutable;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.apache.commons.lang.time.StopWatch;

/**
 * Encapsulates some kind of processing to be performed on a set of inputs.
 *
 * <p>A shared-state is created which is then available as <i>common state</i> across all inputs. We
 * avoid remembering state on a field in {@link Task} itself, so that the {@link Task} itself can be
 * freely duplicated across nodes when parallelizing processing.
 *
 * <p>Initially {@link ParametersUnbound} are created as task-wide parameters across inputs, whereas
 * {@link InputBound} are created in a further step, specific to each input.
 *
 * <p>e.g. we move from a logger for the experiment as a whole in {@link ParametersUnbound}, to a
 * logger for the task itself in {@link InputBound}.
 *
 * <p>The following outputs are produced, and depending on parameterization, written to the
 * file-system:
 *
 * <table>
 * <caption></caption>
 * <thead>
 * <tr><th>Output Name</th><th>Default?</th><th>Description</th></tr>
 * </thead>
 * <tbody>
 * <tr><td rowspan="3">outputs from {@link InputOutputExperiment}.</td></tr>
 * </tbody>
 * </table>
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-state type
 */
public abstract class Task<T extends InputFromManager, S> extends AnchorBean<Task<T, S>> {

    /** Is the execution-time of the task per-input expected to be very quick to execute? */
    public abstract boolean hasVeryQuickPerInputExecution();

    /**
     * Called <i>once</i> before all calls to {@link #executeJob}.
     *
     * @param outputter the output-manager for the experiment (not for an individual job)
     * @param concurrencyPlan available numbers of processors that can call {@link #executeJob}
     * @param inputs a list of inputs, each will result in at least one call to {@link
     *     #executeJob(ParametersUnbound)}.
     * @param parameters the experiment-parameters.
     * @return the shared-state that is passed to each call to {@link #executeJob} and to {@link
     *     #afterAllJobsAreExecuted}.
     * @throws ExperimentExecutionException
     */
    public abstract S beforeAnyJobIsExecuted(
            Outputter outputter,
            ConcurrencyPlan concurrencyPlan,
            List<T> inputs,
            ParametersExperiment parameters)
            throws ExperimentExecutionException;

    /**
     * Runs the task on one particular input (a job).
     *
     * @param parametersUnbound parameters for the input (unbound to any output location).
     * @return whether the job finished successfully or not.
     * @throws JobExecutionException if anything goes wrong with the job which is <b>not</b> logged.
     */
    public boolean executeJob(ParametersUnbound<T, S> parametersUnbound)
            throws JobExecutionException {

        // Bind an outputter for the task
        // The outputter is initially created any log and this is attached later.
        OutputterChecked outputterTask =
                TaskOutputterFactory.createOutputterForTask(
                        parametersUnbound.getInput(), parametersUnbound.getParametersExperiment());
        Preconditions.checkArgument(outputterTask.getSettings().hasBeenInitialized());

        // Create bound parameters
        InputBound<T, S> parametersBound = bindOtherParameters(parametersUnbound, outputterTask);

        outputterTask.assignLogger(parametersBound.getLogger());

        return executeJobLogExceptions(parametersBound, parametersUnbound.isSuppressExceptions());
    }

    /**
     * Called <i>once</i> after all calls to {@link #executeJob}.
     *
     * @param sharedState the shared-state
     * @param context IO-context for experiment (not for an individual job)
     * @throws ExperimentExecutionException
     */
    public abstract void afterAllJobsAreExecuted(S sharedState, InputOutputContext context)
            throws ExperimentExecutionException;

    /** Is an input-object compatible with this particular task? */
    public boolean isInputCompatibleWith(Class<? extends InputFromManager> inputClass) {
        return inputTypesExpected().doesClassInheritFromAny(inputClass);
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
     * If specified, default rules for determine which outputs are enabled or not.
     *
     * @return the default rules if they exist.
     */
    public OutputEnabledMutable defaultOutputs() {
        return new OutputEnabledMutable(IgnoreUnderscorePrefix.INSTANCE);
    }

    /**
     * Creates other objects needed to have a fully bound set of parameters for the task.
     *
     * @param parametersUnbound parameters before being bound for a specific task.
     * @param outputterTaskChecked a bound output manager for the task.
     * @return a complete {@link InputBound} with all parameters set to objects bound for the
     *     specific task.
     */
    private InputBound<T, S> bindOtherParameters(
            ParametersUnbound<T, S> parametersUnbound, OutputterChecked outputterTaskChecked) {

        // We create a new log reporter for this job only
        StatefulMessageLogger loggerJob =
                createJobLog(parametersUnbound.getParametersExperiment(), outputterTaskChecked);

        ErrorReporter errorReporterJob = new ErrorReporterForTask(loggerJob);

        // We initialize the output manager
        Outputter outputterTask = new Outputter(outputterTaskChecked, errorReporterJob);

        // We create new parameters bound specifically to the job
        return new InputBound<>(
                parametersUnbound.getInput(),
                parametersUnbound.getSharedState(),
                parametersUnbound.getParametersExperiment().isDetailedLogging(),
                parametersUnbound.getParametersExperiment().getContext(),
                new InputOutputContextStateful(
                        parametersUnbound.getParametersExperiment().getExperimentArguments(),
                        outputterTask,
                        parametersUnbound.getParametersExperiment().getExecutionTimeRecorder(),
                        loggerJob,
                        errorReporterJob));
    }

    private StatefulMessageLogger createJobLog(
            ParametersExperiment parameters, OutputterChecked outputterTask) {
        return parameters
                .getLoggerTaskCreator()
                .createWithLogFallback(
                        outputterTask,
                        parameters.getLoggerExperiment(),
                        parameters.getExperimentArguments(),
                        parameters.isDetailedLogging());
    }

    private boolean executeJobLogExceptions(InputBound<T, S> parameters, boolean suppressExceptions)
            throws JobExecutionException {

        StatefulMessageLogger loggerJob = parameters.getLogReporterJob();

        StopWatch stopWatchFile = new StopWatch();
        stopWatchFile.start();

        boolean successfullyFinished = false;
        try {
            loggerJob.start();

            if (parameters.isDetailedLogging()) {

                loggerJob.logFormatted(
                        "File processing started: %s", parameters.getInput().identifier());
            }

            executeJobAdditionalOutputs(parameters);

            successfullyFinished = true;
        } catch (AnchorFriendlyCheckedException e) {
            parameters
                    .getLogger()
                    .errorReporter()
                    .recordError(Task.class, e.friendlyMessageHierarchy());
            processExceptionAfterRecordingError(loggerJob, suppressExceptions, e);
        } catch (Throwable e) { // NOSONAR
            // We need to catch both exceptions and errors in order to recover from failure in
            // the specific task. Other tasks will continue executing.
            parameters.getLogger().errorReporter().recordError(Task.class, e);
            processExceptionAfterRecordingError(loggerJob, suppressExceptions, e);
        } finally {

            stopWatchFile.stop();

            if (parameters.isDetailedLogging()) {
                loggerJob.logFormatted(
                        "File processing ended:   %s (time taken = %ds)",
                        parameters.getInput().identifier(), stopWatchFile.getTime() / 1000);
                MemoryUtilities.logMemoryUsage("End file processing", loggerJob);
            }

            loggerJob.close(
                    successfullyFinished,
                    parameters.getLogger().errorReporter().hasWarningOccurred());
        }
        return successfullyFinished;
    }

    private static void processExceptionAfterRecordingError(
            StatefulMessageLogger loggerJob, boolean suppressExceptions, Throwable e)
            throws JobExecutionException {
        loggerJob.log(
                "This error was fatal. The specific job will end early, but the experiment will otherwise continue.");
        if (!suppressExceptions) {
            throw new JobExecutionException("Job encountered a fatal error", e);
        }
    }

    private void executeJobAdditionalOutputs(InputBound<T, S> parameters)
            throws JobExecutionException {

        try {
            doJobOnInput(parameters);
        } catch (ClassCastException e) {
            throw new JobExecutionException(
                    "Could not cast one class to another. Have you used a compatible input-manager for the task?",
                    e);
        } finally {
            // We close the input objects as soon as the task is completed, so as to free up file
            // handles
            // NB Deal with this in the future... if a task is never called, then close() might
            // never be called on the input-object
            parameters.getInput().close(parameters.getLogger().errorReporter());
        }
    }
}
