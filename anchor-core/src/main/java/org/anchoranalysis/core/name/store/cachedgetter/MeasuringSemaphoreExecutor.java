/*-
 * #%L
 * anchor-core
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
/* (C)2020 */
package org.anchoranalysis.core.name.store.cachedgetter;

import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.memory.MemoryUtilities;
import org.apache.commons.lang.time.StopWatch;

/**
 * Allows for multiple simultaneous calls to execute(), measuring the total time and memory from the
 * first starts until the last completes.
 *
 * @author Owen Feehan
 * @param E exception throw if operation fails
 */
class MeasuringSemaphoreExecutor<E extends Exception> {
    private int cnt = 0;
    private long subExecTime = 0;
    private long subMem = 0;

    public <T> T execute(Operation<T, E> exec, String name, String storeDisplayName, Logger logger)
            throws E {
        cnt++;

        StopWatch sw = new StopWatch();
        sw.start();

        long memoryBefore = MemoryUtilities.calcMemoryUsage();

        T obj = exec.doOperation();
        long timeTaken = Math.max(sw.getTime() - subExecTime, 0);

        long memoryAfter = MemoryUtilities.calcMemoryUsage();
        long memoryAdded = memoryAfter - memoryBefore;

        long memoryUsed = Math.max(memoryAdded - subMem, 0);

        logger.messageLogger()
                .log(
                        String.format(
                                "Execution Time \t(%6dms, %7dkb)\t%s\t%s",
                                timeTaken, memoryUsed / 1000, name, storeDisplayName));
        cnt--;

        if (cnt == 0) {
            subExecTime = 0;
            subMem = 0;
        } else {
            subExecTime = timeTaken;
            subMem = memoryAdded;
        }

        return obj;
    }
}
