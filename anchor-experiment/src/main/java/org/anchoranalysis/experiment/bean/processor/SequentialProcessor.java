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
import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.experiment.task.ErrorReporterForTask;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.experiment.task.ParametersUnbound;
import org.anchoranalysis.experiment.task.TaskStatistics;
import org.anchoranalysis.experiment.task.processor.MonitoredSequentialExecutor;
import org.anchoranalysis.inference.concurrency.ConcurrencyPlan;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Executes jobs sequentially, without any parallelism.
 *
 * <p>This is the simplest form of a {@link JobProcessor}.
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-object type
 */
public class SequentialProcessor<T extends InputFromManager, S> extends JobProcessor<T, S> {

    @Override
    protected TaskStatistics execute(
            Outputter rootOutputter, List<T> inputs, ParametersExperiment parametersExperiment)
            throws ExperimentExecutionException {

        ProcessorChecker.checkAtLeastOneInput(inputs);

        ConcurrencyPlan concurrencyPlan = ConcurrencyPlan.singleCPUProcessor();

        S sharedState =
                getTask()
                        .beforeAnyJobIsExecuted(
                                rootOutputter, concurrencyPlan, inputs, parametersExperiment);

        TaskStatistics statistics =
                executeAllJobs(
                        inputs,
                        sharedState,
                        parametersExperiment,
                        ProcessorUtilities.loggerForMonitor(parametersExperiment));

        getTask().afterAllJobsAreExecuted(sharedState, parametersExperiment.getContext());

        return statistics;
    }

    private TaskStatistics executeAllJobs(
            List<T> inputs,
            S sharedState,
            ParametersExperiment parametersExperiment,
            Optional<MessageLogger> loggerMonitor) {

        MonitoredSequentialExecutor<T> executor =
                new MonitoredSequentialExecutor<>(
                        object -> executeJobAndLog(object, sharedState, parametersExperiment),
                        T::identifier,
                        loggerMonitor,
                        false);

        return executor.executeEachWithMonitor("Job: ", inputs);
    }

    private boolean executeJobAndLog(
            T input, S sharedState, ParametersExperiment parametersExperiment) {

        StatefulMessageLogger logger = parametersExperiment.getLoggerExperiment();
        ErrorReporter errorReporter = new ErrorReporterForTask(logger);

        try {
            ParametersUnbound<T, S> parametersUnbound =
                    new ParametersUnbound<>(
                            parametersExperiment, input, sharedState, isSuppressExceptions());
            return getTask().executeJob(parametersUnbound);

        } catch (JobExecutionException e) {
            errorReporter.recordError(SequentialProcessor.class, e);
            return false;
        }
    }
}
