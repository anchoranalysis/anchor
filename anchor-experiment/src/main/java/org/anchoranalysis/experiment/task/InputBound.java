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
import org.anchoranalysis.experiment.io.InitializationContext;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
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

    /** The input is bound. * */
    @Getter private final T input;

    /** The state shared between inputs. */
    @Getter private final S sharedState;

    /** Whether logging is more detailed (true) or less detailed (false). */
    @Getter private final boolean detailedLogging;

    /** The input-output context associated with the current experiment. */
    @Getter private final InputOutputContextStateful contextExperiment;

    /** The input-output context associated with the current job. */
    @Getter private final InputOutputContextStateful contextJob;

    /**
     * Copies the current instance but changes the input.
     *
     * <p>This an <i>immutable</i> operation that does not alter the existing instance.
     *
     * @param inputToAssign the input in the copy, that replaces the existing input.
     * @return a copy of the current instance, except the input is replaced with {@code
     *     inputToAssign}.
     * @param <U> the type of {@code inputToAssign}.
     */
    public <U> InputBound<U, S> changeInput(U inputToAssign) {
        return new InputBound<>(
                inputToAssign, sharedState, detailedLogging, contextExperiment, contextJob);
    }

    /**
     * Copies the current instance but changes both the input and shared-state.
     *
     * <p>This an <i>immutable</i> operation that does not alter the existing instance.
     *
     * @param inputToAssign the input in the copy, that replaces the existing input.
     * @param sharedStateToAssign the shared-state in the copy, that replaces the existing
     *     shared-state.
     * @return a copy of the current instance, except the input is replaced with {@code
     *     inputToAssign}.
     * @param <U> the type of {@code inputToAssign}.
     * @param <V> the type of {@code sharedStateToAssign}.
     */
    public <U, V> InputBound<U, V> changeInputAndSharedState(
            U inputToAssign, V sharedStateToAssign) {
        return new InputBound<>(
                inputToAssign, sharedStateToAssign, detailedLogging, contextExperiment, contextJob);
    }

    /**
     * Creates an {@link InitializationContext} from the current instance.
     *
     * @return a newly created {@link InitializationContext}.
     */
    public InitializationContext createInitializationContext() {
        return new InitializationContext(
                contextJob, contextExperiment.getExecutionArguments().task().getSize());
    }

    /**
     * The {@link Outputter} associated with this input, as it is being processed as a job.
     *
     * @return the outputter.
     */
    public Outputter getOutputter() {
        return contextJob.getOutputter();
    }

    /**
     * The {@link Logger} associated with this input, as it is being processed as a job.
     *
     * @return the logger.
     */
    public Logger getLogger() {
        return contextJob.getLogger();
    }

    /**
     * The {@link StatefulMessageLogger} associated with the job thati s being processed.
     *
     * @return the logger.
     */
    public StatefulMessageLogger getLogReporterJob() {
        return contextJob.getMessageLogger();
    }

    /**
     * The arguments for the task that is being processed.
     *
     * @return the arguments.
     */
    public TaskArguments getTaskArguments() {
        return contextExperiment.getExecutionArguments().task();
    }
}
