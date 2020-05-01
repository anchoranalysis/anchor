package org.anchoranalysis.core.cache;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;

/**
 * Given an {@link Operation}, it wraps it to make it a cached-operation with progress-reporter
 * 
 * @author Owen Feehan
 *
 * @param <T> return-type of operation
 * @param <E> exception that is thrown if something goes wrong during execution
 */
public class WrapOperationWithProgressReporterAsCached<T, E extends Throwable> extends CachedOperationWithProgressReporter<T, E> {

	private OperationWithProgressReporter<T, E> operation;
	
	public WrapOperationWithProgressReporterAsCached(Operation<T, E> operation) {
		this(
			progressReporter -> operation.doOperation()
		);
	}
	
	public WrapOperationWithProgressReporterAsCached(OperationWithProgressReporter<T, E> operation) {
		super();
		this.operation = operation;
	}

	@Override
	protected T execute(ProgressReporter progressReporter) throws E {
		return operation.doOperation(progressReporter); 
	}

}
