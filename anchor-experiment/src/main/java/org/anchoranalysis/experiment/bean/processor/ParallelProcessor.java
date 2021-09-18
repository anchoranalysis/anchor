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

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.value.LanguageUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.experiment.task.processor.CallableJob;
import org.anchoranalysis.experiment.task.processor.ConcurrentJobMonitor;
import org.anchoranalysis.experiment.task.processor.JobDescription;
import org.anchoranalysis.experiment.task.processor.JobState;
import org.anchoranalysis.experiment.task.processor.SubmittedJob;
import org.anchoranalysis.inference.concurrency.ConcurrencyPlan;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Executes jobs in parallel
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

    @Override
    protected TaskStatistics execute(
            Outputter rootOutputter, List<T> inputs, ParametersExperiment paramsExperiment)
            throws ExperimentExecutionException {
        int numberInputs = inputs.size();

        ProcessorChecker.checkAtLeastOneInput(inputs);

        ConcurrencyPlan concurrencyPlan = createConcurrencyPlan(paramsExperiment);

        S sharedState =
                getTask()
                        .beforeAnyJobIsExecuted(
                                rootOutputter, concurrencyPlan, inputs, paramsExperiment);

        ExecutorService executorService =
                Executors.newFixedThreadPool(concurrencyPlan.numberCPUs());

        int count = 1;

        ConcurrentJobMonitor monitor = new ConcurrentJobMonitor(inputs.size());

        ListIterator<T> iterator = inputs.listIterator();
        while (iterator.hasNext()) {
            T input = iterator.next();
            try {
                submitJob(executorService, input, count, sharedState, paramsExperiment, monitor);
                count++;
            } finally {
                iterator.remove();
            }
        }

        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executorService.shutdown();

        // Wait until all threads are finish
        while (!executorService.isTerminated())
            ;

        if (monitor.numberExecutingJobs() != 0
                || monitor.numberOngoingJobs() != 0
                || monitor.numberCompletedJobs() != numberInputs) {
            paramsExperiment
                    .getLoggerExperiment()
                    .log("At least one experiment ended irregularly!");
        }

        getTask().afterAllJobsAreExecuted(sharedState, paramsExperiment.getContext());
        return monitor.createStatistics();
    }

    private void submitJob(
            ExecutorService executorService,
            T input,
            int index,
            S sharedState,
            ParametersExperiment paramsExperiment,
            ConcurrentJobMonitor monitor) {

        JobDescription description = new JobDescription(input.identifier(), index);

        ParametersUnbound<T, S> paramsUnbound =
                new ParametersUnbound<>(
                        paramsExperiment, input, sharedState, isSuppressExceptions());

        // Task always gets duplicated when it's called
        JobState state = new JobState();
        executorService.submit(
                new CallableJob<>(
                        getTask(),
                        paramsUnbound,
                        state,
                        description,
                        monitor,
                        loggerForMonitor(paramsExperiment),
                        showOngoingJobsLessThan));

        monitor.add(new SubmittedJob(description, state));
    }

    private ConcurrencyPlan createConcurrencyPlan(ParametersExperiment paramsExperiment) {

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        int numberCPUs = selectNumberCPUs(availableProcessors);

        if (paramsExperiment.isDetailedLogging()) {
            paramsExperiment
                    .getLoggerExperiment()
                    .logFormatted(
                            "Preparing jobs to run with common initialization.%nUsing %s CPUs from %d, and if needed and if possible, up to %d simultaneous jobs using a GPU.",
                            LanguageUtilities.prefixPluralizeMaybe(numberCPUs, "processor"),
                            availableProcessors,
                            numberGPUProcessors);
        }
        return ConcurrencyPlan.multipleProcessors(numberCPUs, numberGPUProcessors);
    }

    private int selectNumberCPUs(int availableProcessors) {

        int numberOfProcessors = availableProcessors - keepProcessorsFree;

        if (maxNumberProcessors > 0) {
            numberOfProcessors = Math.min(numberOfProcessors, maxNumberProcessors);
        }

        return numberOfProcessors;
    }
}
