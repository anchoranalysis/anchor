/* (C)2020 */
package org.anchoranalysis.experiment.bean.task;

import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.task.InputBound;
import org.anchoranalysis.experiment.task.InputTypesExpected;
import org.anchoranalysis.experiment.task.NoSharedState;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * A dummy task that simply writes a message to all log files, specifically: 1. log file for
 * experiment 2. log file for each input-object
 *
 * <p>The message is: 1. a line saying Hello World 2. a recommendation to replace the task with a
 * specific task
 *
 * <p>TODO: add a URL to the output, pointing guiding the user on the various tasks that exist
 *
 * @author Owen Feehan
 */
public class HelloWorldTask<S extends InputFromManager> extends TaskWithoutSharedState<S> {

    @Override
    public InputTypesExpected inputTypesExpected() {
        return new InputTypesExpected(InputFromManager.class);
    }

    @Override
    public void doJobOnInputObject(InputBound<S, NoSharedState> params)
            throws JobExecutionException {
        printMessage(params.getLogger().messageLogger());
    }

    private void printMessage(MessageLogger logger) {
        logger.log("Hello World");
        logger.log("Consider replacing this task, with one appropriate to your intentions.");
    }

    @Override
    public boolean hasVeryQuickPerInputExecution() {
        return true;
    }
}
