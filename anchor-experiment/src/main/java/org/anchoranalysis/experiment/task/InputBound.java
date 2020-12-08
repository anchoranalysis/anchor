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

package org.anchoranalysis.experiment.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.arguments.TaskArguments;
import org.anchoranalysis.experiment.io.InitParamsContext;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Input for executing a task, associated with shared-state and other parameters.
 *
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-state type
 */
@AllArgsConstructor
public class InputBound<T, S> {

    @Getter private final T input;

    @Getter private final S sharedState;

    @Getter private final Manifest manifest;

    @Getter private final boolean detailedLogging;

    /** The input-output context associated with the current experiment. */
    @Getter private final InputOutputContextStateful contextExperiment;

    /** The input-output context associated with the current job. */
    @Getter private final InputOutputContextStateful contextJob;

    /** Immutably changes the input-object */
    public <U> InputBound<U, S> changeInput(U inputToAssign) {
        return new InputBound<>(
                inputToAssign,
                sharedState,
                manifest,
                detailedLogging,
                contextExperiment,
                contextJob
        );
    }

    /** Immutably changes the input-object and shared-state */
    public <U, V> InputBound<U, V> changeInputAndSharedState(
            U inputToAssign, V sharedStateToAssign) {
        return new InputBound<>(
                inputToAssign,
                sharedStateToAssign,
                manifest,
                detailedLogging,
                contextExperiment,
                contextJob
                );
    }
    
    public InitParamsContext createInitParamsContext() {
        return new InitParamsContext(contextJob, contextExperiment.getExperimentArguments().task().getResize());
    }

    public Outputter getOutputter() {
        return contextJob.getOutputter();
    }

    public Logger getLogger() {
        return contextJob.getLogger();
    }

    public StatefulMessageLogger getLogReporterJob() {
        return contextJob.getMessageLogger();
    }

    public TaskArguments getTaskArguments() {
        return contextExperiment.getExperimentArguments().task();
    }
}
