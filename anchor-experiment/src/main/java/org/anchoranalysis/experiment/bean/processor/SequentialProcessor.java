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
import java.util.Optional;
import org.anchoranalysis.core.concurrency.ConcurrencyPlan;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.experiment.task.ErrorReporterForTask;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.experiment.task.processor.MonitoredSequentialExecutor;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.bound.Outputter;

/**
 * Executes jobs sequentially
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-object type
 */
public class SequentialProcessor<T extends InputFromManager, S> extends JobProcessor<T, S> {

    @Override
    protected TaskStatistics execute(
            Outputter rootOutputter,
            List<T> inputObjects,
            ParametersExperiment paramsExperiment)
            throws ExperimentExecutionException {

        ConcurrencyPlan concurrencyPlan = ConcurrencyPlan.singleProcessor(1);

        S sharedState =
                getTask()
                        .beforeAnyJobIsExecuted(
                                rootOutputter, concurrencyPlan, paramsExperiment);

        TaskStatistics stats =
                executeAllJobs(
                        inputObjects,
                        sharedState,
                        paramsExperiment,
                        loggerForMonitor(paramsExperiment));

        getTask().afterAllJobsAreExecuted(sharedState, paramsExperiment.getContext());

        return stats;
    }

    private TaskStatistics executeAllJobs(
            List<T> inputObjects,
            S sharedState,
            ParametersExperiment paramsExperiment,
            Optional<MessageLogger> loggerMonitor) {

        MonitoredSequentialExecutor<T> seqExecutor =
                new MonitoredSequentialExecutor<>(
                        object -> executeJobAndLog(object, sharedState, paramsExperiment),
                        T::descriptiveName,
                        loggerMonitor,
                        false);

        return seqExecutor.executeEachWithMonitor("Job: ", inputObjects);
    }

    private boolean executeJobAndLog(
            T inputObject, S sharedState, ParametersExperiment paramsExperiment) {

        StatefulMessageLogger logger = paramsExperiment.getLoggerExperiment();
        ErrorReporter errorReporter = new ErrorReporterForTask(logger);

        try {
            ParametersUnbound<T, S> paramsUnbound =
                    new ParametersUnbound<>(
                            paramsExperiment, inputObject, sharedState, isSuppressExceptions());
            return getTask().executeJob(paramsUnbound);

        } catch (JobExecutionException e) {
            errorReporter.recordError(SequentialProcessor.class, e);
            return false;
        }
    }
}
