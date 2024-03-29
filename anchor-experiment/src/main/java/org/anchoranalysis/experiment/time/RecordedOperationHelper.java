/*-
 * #%L
 * anchor-experiment
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
package org.anchoranalysis.experiment.time;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.time.RecordedExecutionTimes.RecordedOperation;
import org.anchoranalysis.math.arithmetic.RunningSum;

/**
 * Helps create {@link RecordedOperation} from a {@link RunningSum}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RecordedOperationHelper {

    /**
     * Creates the operation.
     *
     * @param operationIdentifier the identifier.
     * @param numberParentOperations the number of parent operations. Zero if there are no parents.
     * @param runningSum the running sum.
     * @return the operation.
     */
    public static RecordedOperation create(
            String operationIdentifier, RunningSum runningSum, int numberParentOperations) {
        return new RecordedOperation(
                operationIdentifier,
                numberParentOperations,
                runningSum.mean(),
                runningSum.getSum(),
                (int) runningSum.getCount());
    }
}
