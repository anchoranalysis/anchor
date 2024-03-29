package org.anchoranalysis.core.system;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.MessageLogger;

/**
 * Logs a message about approximate memory (RAM) usage of the JVM.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoryUtilities {

    /**
     * Logs a message to {@code logger} describing current memory usage of the JVM.
     *
     * @param identifier a human-readable string identifying the timepoint in the execution of the
     *     program where memory is recorded.
     * @param logger the logger to record the message onto
     */
    public static void logMemoryUsage(String identifier, MessageLogger logger) {
        Runtime runtime = java.lang.Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long memory = totalMemory - freeMemory;

        logger.logFormatted(
                "Total used memory (%s): %d MB (out of %d MB)",
                identifier, memory / 1000000, totalMemory / 1000000);
    }
}
