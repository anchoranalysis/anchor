package org.anchoranalysis.experiment.task.processor;

/*
 * #%L
 * anchor-experiment
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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
		return state==State.UNSTARTED;
	}
	
	public boolean isExecuting() {
		return state==State.EXECUTING;
	}
	
	public boolean isCompleted() {
		return state==State.COMPLETED_SUCCESS || state==State.COMPLETED_FAILURE;
	}
	
	public boolean isCompletedSuccessfully() {
		return state==State.COMPLETED_SUCCESS;
	}
	
	public boolean isCompletedFailure() {
		return state==State.COMPLETED_FAILURE;
	}
	
	public void markAsExecuting() {
		state = State.EXECUTING;
		stopWatch.start();
	}
	
	public void markAsCompleted( boolean successful ) {
		state = successful ? State.COMPLETED_SUCCESS : State.COMPLETED_FAILURE;
		stopWatch.stop();
	}
	
	public int getTime() {
		return (int) stopWatch.getTime();
	}
}
