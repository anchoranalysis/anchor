/* (C)2020 */
package org.anchoranalysis.experiment.task.processor;

import org.apache.commons.lang.time.StopWatch;

public class JobState {

    private enum State {
        UNSTARTED,
        EXECUTING,
        COMPLETED_SUCCESS,
        COMPLETED_FAILURE
    }

    private StopWatch stopWatch = new StopWatch();

    private State state = State.UNSTARTED;

    public boolean isUnstarted() {
        return state == State.UNSTARTED;
    }

    public boolean isExecuting() {
        return state == State.EXECUTING;
    }

    public boolean isCompleted() {
        return state == State.COMPLETED_SUCCESS || state == State.COMPLETED_FAILURE;
    }

    public boolean isCompletedSuccessfully() {
        return state == State.COMPLETED_SUCCESS;
    }

    public boolean isCompletedFailure() {
        return state == State.COMPLETED_FAILURE;
    }

    public void markAsExecuting() {
        state = State.EXECUTING;
        stopWatch.start();
    }

    public void markAsCompleted(boolean successful) {
        state = successful ? State.COMPLETED_SUCCESS : State.COMPLETED_FAILURE;
        stopWatch.stop();
    }

    public int getTime() {
        return (int) stopWatch.getTime();
    }
}
