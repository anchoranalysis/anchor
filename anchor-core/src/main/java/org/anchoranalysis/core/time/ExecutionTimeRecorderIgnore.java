/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.core.time;

import java.util.stream.Stream;

/**
 * An implementation of {@link ExecutionTimeRecorder} that is a simple placeholder that does
 * nothing.
 *
 * @author Owen Feehan
 */
public class ExecutionTimeRecorderIgnore extends ExecutionTimeRecorder {

    private static ExecutionTimeRecorder instance = null;

    /**
     * Singleton instance.
     *
     * @return the instance.
     */
    public static ExecutionTimeRecorder instance() {
        if (instance == null) {
            instance = new ExecutionTimeRecorderIgnore();
        }
        return instance;
    }

    @Override
    public void recordExecutionTime(String operationIdentifier, long millis) {
        // NOTHING TO DO
    }

    @Override
    public RecordedExecutionTimes recordedTimes() {
        return new RecordedExecutionTimes(Stream.empty());
    }

    @Override
    public boolean isOperationAlreadyRecorded(String operationIdentifier) {
        return false;
    }

    @Override
    public long measureTime(boolean start, String... operationIdentifiers) {
        return System.currentTimeMillis();
    }
}
