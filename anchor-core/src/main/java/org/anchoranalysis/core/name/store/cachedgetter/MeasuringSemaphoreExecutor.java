package org.anchoranalysis.core.name.store.cachedgetter;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.memory.MemoryUtilities;
import org.apache.commons.lang.time.StopWatch;


/**
 * Allows for multiple simultaneous calls to execute(), measuring the total time and memory from the first starts until the last completes.
 *  
 * @author owen
 *
 */
class MeasuringSemaphoreExecutor {
	private int cnt = 0;
	private long subExecTime = 0; 
	private long subMem = 0;
	
	public <T> T execute(Operation<T> exec, String name, String storeDisplayName, LogErrorReporter logErrorReporter) throws ExecuteException {
		cnt++;

		StopWatch sw = new StopWatch();
		sw.start();
		
		long memoryBefore = MemoryUtilities.calcMemoryUsage();
	
		T obj = exec.doOperation();
		long timeTaken = Math.max( sw.getTime() - subExecTime, 0 );

		long memoryAfter = MemoryUtilities.calcMemoryUsage();
		long memoryAdded = memoryAfter - memoryBefore;
		
		
		long memoryUsed = Math.max( memoryAdded - subMem, 0);
		
		logErrorReporter.getLogReporter().log( String.format("Execution Time \t(%6dms, %7dkb)\t%s\t%s", timeTaken, memoryUsed/1000, name, storeDisplayName ) );
		cnt--;
		
		if (cnt==0) {
			subExecTime=0;
			subMem = 0;
		} else {
			subExecTime = timeTaken;
			subMem = memoryAdded;
		}
		
		return obj;
	}
}