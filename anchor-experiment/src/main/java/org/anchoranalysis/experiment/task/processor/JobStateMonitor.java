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

package org.anchoranalysis.experiment.task.processor;

import org.anchoranalysis.core.exception.OperationFailedRuntimeException;
import org.apache.commons.lang.time.StopWatch;

/**
 * Monitor execution-state of a job, and how long it is taking/took to execute.
 *
 * <p>The state refers to whether a job is unstarted, executed, completed etc.
 *
 * <p>It exists as a state-machine, where a job initially has an <i>unstarted</i> state.
 *
 * <p>Next, a call should occur to {@link #markAsExecuting()} to indicate that the job has started
 * executing.
 *
 * <p>Next, a call should occur to {@link #markAsCompleted(boolean)} to indicate that the job has
 * completed.
 *
 * @author Owen Feehan
 */
public class JobStateMonitor {

    private enum State {
        UNSTARTED,
        EXECUTING,
        COMPLETED_SUCCESS,
        COMPLETED_FAILURE
    }

    private StopWatch stopWatch = new StopWatch();

    private State state = State.UNSTARTED;

    /**
     * Is a job unstarted?
     *
     * @return true if a job has yet to be started, false otherwise.
     */
    public boolean isUnstarted() {
        return state == State.UNSTARTED;
    }

    /**
     * Is a job currently executing?
     *
     * @return true if a job is currently executing, false otherwise.
     */
    public boolean isExecuting() {
        return state == State.EXECUTING;
    }

    /**
     * Is a job completed?
     *
     * @return true if a job is currently completed, false otherwise.
     */
    public boolean isCompleted() {
        return isCompletedSuccessfully() || isCompletedFailure();
    }

    /**
     * Has a job completed with a successful outcome?
     *
     * @return true if a job has completed successfully, false otherwise.
     */
    public boolean isCompletedSuccessfully() {
        return state == State.COMPLETED_SUCCESS;
    }

    /**
     * Has a job completed with a failure?
     *
     * @return true if a job has completed with failure, false otherwise.
     */
    public boolean isCompletedFailure() {
        return state == State.COMPLETED_FAILURE;
    }

    /**
     * Indicates that a job is currently executing.
     *
     * <p>This method records the start-time of execution, later used to help infers the total
     * duration of excution.
     *
     * @throws OperationFailedRuntimeException if called without the job being in an unstarted
     *     state.
     */
    public void markAsExecuting() {

        if (state != State.UNSTARTED) {
            throw new OperationFailedRuntimeException(
                    "The job must be unstarted to call this method.");
        }

        state = State.EXECUTING;
        stopWatch.start();
    }

    /**
     * Indicates that a job has stopped executing, and has completed.
     *
     * <p>This method records the end-time of execution, and infers the total duration of execution.
     *
     * @param successful whether the job completed successfully or not.
     */
    public void markAsCompleted(boolean successful) {

        if (state != State.EXECUTING) {
            throw new OperationFailedRuntimeException(
                    "The job must be executing to call this method.");
        }

        state = successful ? State.COMPLETED_SUCCESS : State.COMPLETED_FAILURE;
        stopWatch.stop();
    }

    /**
     * How long the job took to execute in milliseconds.
     *
     * <p>This method should only be called when the job is in a completed state.
     *
     * @return the duration of the job in milliseconds.
     * @throws OperationFailedRuntimeException if called without the job being in a completed state.
     */
    public int getExecutionDuration() {

        if (state == State.UNSTARTED || state == State.EXECUTING) {
            throw new OperationFailedRuntimeException(
                    "The job must be completed to call this method.");
        }

        return (int) stopWatch.getTime();
    }
}
