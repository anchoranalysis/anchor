package org.anchoranalysis.core.name.store.cachedgetter;

import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.memory.MemoryUtilities;
import org.apache.commons.lang.time.StopWatch;


/**
 * Allows for multiple simultaneous calls to execute(), measuring the total time and memory from the first starts until the last completes.
 *  
 * @author owen
 * @param E exception throw if operation fails
 */
class MeasuringSemaphoreExecutor<E extends Throwable> {
	private int cnt = 0;
	private long subExecTime = 0; 
	private long subMem = 0;
	
	public <T> T execute(Operation<T,E> exec, String name, String storeDisplayName, LogErrorReporter logErrorReporter) throws E {
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
