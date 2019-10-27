package org.anchoranalysis.core.progress;

/*
 * #%L
 * anchor-core
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


import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;

/**
 * 
 * @author Owen Feehan
 *
 * @param <R> Result-type
 */
public abstract class CachedOperationWithProgressReporter<R> implements OperationWithProgressReporter<R>, Operation<R> {

	private R result = null;
	private boolean done = false;
	
	@Override
	public synchronized R doOperation( ProgressReporter progressReporter ) throws ExecuteException {
		
		if (!done) {
			result = execute( progressReporter );
			done = true;
		}
		
		return result;
	}
	
	public synchronized void assignFrom( CachedOperationWithProgressReporter<R> src ) {
		this.result = src.result;
		this.done = src.done;
	}
	
	public synchronized void reset() {
		done = false;
		result = null;
	}
	
	public synchronized boolean isDone() {
		return done;
	}
	
	protected abstract R execute( ProgressReporter progressReporter ) throws ExecuteException;

	protected synchronized R getResult() {
		return result;
	}

	@Override
	public R doOperation() throws ExecuteException {
		return doOperation( ProgressReporterNull.get() );
	}
}
