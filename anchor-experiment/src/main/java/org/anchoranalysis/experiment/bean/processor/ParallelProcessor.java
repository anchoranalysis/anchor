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
/* (C)2020 */
package org.anchoranalysis.experiment.bean.processor;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.text.LanguageUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.experiment.task.processor.CallableJob;
import org.anchoranalysis.experiment.task.processor.ConcurrentJobMonitor;
import org.anchoranalysis.experiment.task.processor.JobDescription;
import org.anchoranalysis.experiment.task.processor.JobState;
import org.anchoranalysis.experiment.task.processor.SubmittedJob;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * Executes jobs in parallel
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-state type
 */
public class ParallelProcessor<T extends InputFromManager, S> extends JobProcessor<T, S> {

    // START BEAN
    @BeanField @Getter @Setter private int maxNumProcessors = 64;

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
    // END BEAN

    @Override
    protected TaskStatistics execute(
            BoundOutputManagerRouteErrors rootOutputManager,
            List<T> inputObjects,
            ParametersExperiment paramsExperiment)
            throws ExperimentExecutionException {

        S sharedState = getTask().beforeAnyJobIsExecuted(rootOutputManager, paramsExperiment);

        int nrOfProcessors =
                selectNumProcessors(
                        paramsExperiment.getLoggerExperiment(),
                        paramsExperiment.isDetailedLogging());
        ExecutorService eservice = Executors.newFixedThreadPool(nrOfProcessors);

        int cnt = 1;

        ConcurrentJobMonitor monitor = new ConcurrentJobMonitor(inputObjects.size());

        ListIterator<T> itr = inputObjects.listIterator();
        while (itr.hasNext()) {
            T inputObj = itr.next();
            try {
                submitJob(eservice, inputObj, cnt, sharedState, paramsExperiment, monitor);
                cnt++;
            } finally {
                itr.remove();
            }
        }

        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        eservice.shutdown();

        // Wait until all threads are finish
        while (!eservice.isTerminated())
            ;

        getTask().afterAllJobsAreExecuted(sharedState, paramsExperiment.getContext());
        return monitor.createStatistics();
    }

    private int selectNumProcessors(MessageLogger logger, boolean detailedLogging) {

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        int nrOfProcessors = availableProcessors - keepProcessorsFree;

        if (maxNumProcessors > 0) {
            nrOfProcessors = Math.min(nrOfProcessors, maxNumProcessors);
        }

        if (detailedLogging) {
            logger.logFormatted(
                    "Using %s from: %d",
                    LanguageUtilities.prefixPluralizeMaybe(nrOfProcessors, "processor"),
                    availableProcessors);
        }

        return nrOfProcessors;
    }

    private void submitJob(
            ExecutorService eservice,
            T inputObj,
            int index,
            S sharedState,
            ParametersExperiment paramsExperiment,
            ConcurrentJobMonitor monitor) {

        JobDescription td = new JobDescription(inputObj.descriptiveName(), index);

        ParametersUnbound<T, S> paramsUnbound =
                new ParametersUnbound<>(
                        paramsExperiment, inputObj, sharedState, isSuppressExceptions());

        // Task always gets duplicated when it's called
        JobState taskState = new JobState();
        eservice.submit(
                new CallableJob<>(
                        getTask(),
                        paramsUnbound,
                        taskState,
                        td,
                        monitor,
                        loggerForMonitor(paramsExperiment),
                        showOngoingJobsLessThan));

        SubmittedJob submittedTask = new SubmittedJob(td, taskState);
        monitor.add(submittedTask);
    }
}
