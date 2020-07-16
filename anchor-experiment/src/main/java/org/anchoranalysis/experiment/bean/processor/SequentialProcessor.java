/* (C)2020 */
package org.anchoranalysis.experiment.bean.processor;

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
 * @param <T> input-object type
 * @param <S> shared-object type
 */
public class SequentialProcessor<T extends InputFromManager, S> extends JobProcessor<T, S> {

    @Override
    protected TaskStatistics execute(
            BoundOutputManagerRouteErrors rootOutputManager,
            List<T> inputObjects,
            ParametersExperiment paramsExperiment)
            throws ExperimentExecutionException {

        S sharedState = getTask().beforeAnyJobIsExecuted(rootOutputManager, paramsExperiment);

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
            T inputObj, S sharedState, ParametersExperiment paramsExperiment) {

        MessageLogger logger = paramsExperiment.getLoggerExperiment();
        ErrorReporter errorReporter = new ErrorReporterIntoLog(logger);

        try {
            ParametersUnbound<T, S> paramsUnbound =
                    new ParametersUnbound<>(
                            paramsExperiment, inputObj, sharedState, isSuppressExceptions());
            return getTask().executeJob(paramsUnbound);

        } catch (JobExecutionException e) {
            errorReporter.recordError(SequentialProcessor.class, e);
            return false;
        }
    }
}
