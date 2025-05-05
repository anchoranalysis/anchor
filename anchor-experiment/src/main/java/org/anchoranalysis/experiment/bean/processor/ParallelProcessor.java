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

package org.anchoranalysis.experiment.bean.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.core.value.LanguageUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.experiment.task.processor.CallableJob;
import org.anchoranalysis.experiment.task.processor.ConcurrentJobMonitor;
import org.anchoranalysis.experiment.task.processor.JobDescription;
import org.anchoranalysis.experiment.task.processor.JobStateMonitor;
import org.anchoranalysis.experiment.task.processor.SubmittedJob;
import org.anchoranalysis.inference.concurrency.ConcurrencyPlan;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Executes jobs in parallel across cores on the system.
 *
 * <p>Each input is processed in a separate thread on an available core.
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-state type
 */
public class ParallelProcessor<T extends InputFromManager, S> extends JobProcessor<T, S> {

    // START BEAN PROPERTIES
    /**
     * Theoretical maximum number of (CPU) processors to use in parallel.
     *
     * <p>In practice, the effective maximum is {@code
     * min(maxNumberProcessors,numberAvailableProcessors)}, usually lower.
     */
    @BeanField @Getter @Setter private int maxNumberProcessors = 64;

    /**
     * When the number of ongoing jobs is less than this threshold, they are shown in event logs. 0
     * disables.
     */
    @BeanField @Getter @Setter private int showOngoingJobsLessThan = 0;

    /**
     * How many processors to avoid using for the tasks.
     *
     * <p>When using the maximum available number of processors, a certain amount are deliberately
     * not used, to save them for other tasks on the operating system. This is particularly valuable
     * on a desktop PC where other tasks (e.g web browsing) may be ongoing during processing.
     */
    @BeanField @Getter @Setter private int keepProcessorsFree = 1;

    /** How many GPU processors to use when this is possible as a substitute for a CPU processor */
    @BeanField @Getter @Setter
    private int numberGPUProcessors = ConcurrencyPlan.DEFAULT_NUMBER_GPUS;

    // END BEAN PROPERTIES

    private BeanInstanceMap defaultInstances;

    @Override
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        super.checkMisconfigured(defaultInstances);
        this.defaultInstances = defaultInstances;
    }

    /**
     * Executes the parallel processing of jobs.
     *
     * @param rootOutputter the root outputter for the experiment
     * @param inputs the list of inputs to process
     * @param parametersExperiment the experiment parameters
     * @return the statistics of the executed tasks
     * @throws ExperimentExecutionException if an error occurs during execution
     */
    @Override
    protected TaskStatistics execute(
            Outputter rootOutputter, List<T> inputs, ParametersExperiment parametersExperiment)
            throws ExperimentExecutionException {
        int numberInputs = inputs.size();

        ProcessorChecker.checkAtLeastOneInput(inputs);

        ConcurrencyPlan concurrencyPlan = createConcurrencyPlan(parametersExperiment);

        S sharedState =
                getTask()
                        .beforeAnyJobIsExecuted(
                                rootOutputter, concurrencyPlan, inputs, parametersExperiment);

        ExecutorService executorService =
                Executors.newFixedThreadPool(concurrencyPlan.numberCPUs());

        ConcurrentJobMonitor monitor = new ConcurrentJobMonitor(inputs.size());

        submitAllJobs(inputs, parametersExperiment, executorService, sharedState, monitor);

        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executorService.shutdown();

        // Wait until all threads are finish
        while (!executorService.isTerminated())
            ;

        logWhenIrregularlyEnded(monitor, parametersExperiment, numberInputs);

        getTask().afterAllJobsAreExecuted(sharedState, parametersExperiment.getContext());
        return monitor.deriveStatistics();
    }

    /**
     * Submits all jobs to the executor service.
     *
     * @param inputs the list of inputs to process
     * @param parametersExperiment the experiment parameters
     * @param executorService the executor service to submit jobs to
     * @param sharedState the shared state between jobs
     * @param monitor the monitor for concurrent jobs
     */
    private void submitAllJobs(
            List<T> inputs,
            ParametersExperiment parametersExperiment,
            ExecutorService executorService,
            S sharedState,
            ConcurrentJobMonitor monitor) {
        int count = 1;
        List<T> inputsModifiable = new ArrayList<>(inputs);

        ListIterator<T> iterator = inputsModifiable.listIterator();
        while (iterator.hasNext()) {
            T input = iterator.next();
            try {
                submitJob(
                        executorService, input, count, sharedState, parametersExperiment, monitor);
                count++;
            } finally {
                iterator.remove();
            }
        }
    }

    /**
     * Submits a single job to the executor service.
     *
     * @param executorService the executor service to submit the job to
     * @param input the input for the job
     * @param index the index of the job
     * @param sharedState the shared state between jobs
     * @param parametersExperiment the experiment parameters
     * @param monitor the monitor for concurrent jobs
     */
    private void submitJob(
            ExecutorService executorService,
            T input,
            int index,
            S sharedState,
            ParametersExperiment parametersExperiment,
            ConcurrentJobMonitor monitor) {

        JobDescription description = new JobDescription(input.identifier(), index);

        ParametersUnbound<T, S> parametersUnbound =
                new ParametersUnbound<>(
                        parametersExperiment, input, sharedState, isSuppressExceptions());

        // Task always gets duplicated when it's called
        JobStateMonitor state = new JobStateMonitor();
        executorService.submit(
                new CallableJob<>(
                        getTask(),
                        parametersUnbound,
                        defaultInstances,
                        state,
                        description,
                        monitor,
                        ProcessorUtilities.loggerForMonitor(parametersExperiment),
                        showOngoingJobsLessThan));

        monitor.add(new SubmittedJob(description, state));
    }

    /**
     * Creates a concurrency plan based on the available processors and experiment parameters.
     *
     * @param parametersExperiment the experiment parameters
     * @return a {@link ConcurrencyPlan} for the experiment
     */
    private ConcurrencyPlan createConcurrencyPlan(ParametersExperiment parametersExperiment) {

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        int numberCPUs = selectNumberCPUs(availableProcessors);

        if (parametersExperiment.isDetailedLogging()) {
            parametersExperiment
                    .getLoggerExperiment()
                    .logFormatted(
                            "Preparing jobs to run with common initialization.%nMaximally using %d simultaneous %s (from %d available), and up to %d simultaneous %s (if available).",
                            numberCPUs,
                            LanguageUtilities.pluralizeMaybe(numberCPUs, "CPU"),
                            availableProcessors,
                            numberGPUProcessors,
                            LanguageUtilities.pluralizeMaybe(numberGPUProcessors, "GPU"));
        }
        return ConcurrencyPlan.multipleProcessors(numberCPUs, numberGPUProcessors);
    }

    /**
     * Selects the number of CPUs to use based on available processors and configuration.
     *
     * @param availableProcessors the number of available processors
     * @return the number of CPUs to use
     */
    private int selectNumberCPUs(int availableProcessors) {

        int numberOfProcessors = availableProcessors - keepProcessorsFree;

        if (maxNumberProcessors > 0) {
            numberOfProcessors = Math.min(numberOfProcessors, maxNumberProcessors);
        }

        return numberOfProcessors;
    }

    /**
     * Logs a message when the experiment ends irregularly.
     *
     * @param monitor the {@link ConcurrentJobMonitor} tracking job execution
     * @param parametersExperiment the {@link ParametersExperiment} containing experiment parameters
     * @param numberInputs the total number of input jobs
     */
    private void logWhenIrregularlyEnded(
            ConcurrentJobMonitor monitor,
            ParametersExperiment parametersExperiment,
            int numberInputs) {
        if (monitor.numberExecutingJobs() != 0
                || monitor.numberUncompletedJobs() != 0
                || monitor.numberCompletedJobs() != numberInputs) {
            parametersExperiment
                    .getLoggerExperiment()
                    .log("At least one experiment ended irregularly!");
        }
    }
}
