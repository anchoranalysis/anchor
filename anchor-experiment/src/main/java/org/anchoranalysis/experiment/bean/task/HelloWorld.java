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
public class HelloWorld<S extends InputFromManager> extends TaskWithoutSharedState<S> {
    
    @Override
    public InputTypesExpected inputTypesExpected() {
        return new InputTypesExpected(InputFromManager.class);
    }

    @Override
    public void doJobOnInput(InputBound<S, NoSharedState> params) throws JobExecutionException {
        printMessage(params.getLogger().messageLogger());
    }

    @Override
    public boolean hasVeryQuickPerInputExecution() {
        return true;
    }

    private void printMessage(MessageLogger logger) {
        logger.log("Hello World");
        logger.log("Consider replacing this task, with one appropriate to your intentions.");
    }
}
