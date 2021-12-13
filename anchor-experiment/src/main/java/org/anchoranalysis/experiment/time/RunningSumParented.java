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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.math.arithmetic.RunningSum;

/**
 * Wraps a {@link RunningSum} with an additional variable, indicating the total number of parent
 * operations.
 *
 * <p>{@link #hashCode()} and {@link #equals(Object)} delegate to the underlying {@link RunningSum}.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class RunningSumParented {

    // START REQUIRED ARGUMENTS
    /** The number of parent operations. */
    @Getter private final int numberParentOperations;
    // END REQUIRED ARGUMENTS

    @Getter private RunningSum runningSum = new RunningSum();

    public int hashCode() {
        return runningSum.hashCode();
    }

    public boolean equals(Object obj) {
        return runningSum.equals(obj);
    }
}
