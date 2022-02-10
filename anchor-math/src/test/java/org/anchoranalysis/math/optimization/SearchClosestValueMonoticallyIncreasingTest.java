/*-
 * #%L
 * anchor-math
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.math.optimization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.anchoranalysis.math.optimization.SearchClosestValueMonoticallyIncreasing.ValueFunction;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link SearchClosestValueMonoticallyIncreasing}.
 *
 * <p>The cube function is used as an example for testing, as it is monotonically increasing, and
 * increases quickly.
 *
 * @author Owen Feehan
 */
class SearchClosestValueMonoticallyIncreasingTest {

    /**
     * The expected optimal input that matches target, if all bounds on the input are sufficiently
     * permissive.
     */
    private static final int EXPECTED_OPTIMAL_INPUT = 15;

    /** The target to use for all searches. */
    private static final double TARGET = Math.pow(EXPECTED_OPTIMAL_INPUT, 3.0);

    /** The function to use in all searches. */
    private static final ValueFunction FUNCTION = value -> Math.pow(value, 3.0);

    /** The search without an upper bound. */
    private static final SearchClosestValueMonoticallyIncreasing SEARCH_LOWER_BOUND_ONLY =
            new SearchClosestValueMonoticallyIncreasing(TARGET, FUNCTION);

    /** The maximum permitted input value if an upper bound is imposed. */
    private static final int UPPER_BOUND = 11;

    /** Insists the input value is never greater than the upper bound. */
    private static final SearchClosestValueMonoticallyIncreasing SEARCH_LOWER_AND_UPPER_BOUND =
            new SearchClosestValueMonoticallyIncreasing(
                    TARGET, FUNCTION, value -> value > UPPER_BOUND);

    /** When the minimum bound is sufficiently minimum. */
    @Test
    void testValidMinimumBound() {
        doTest(3, EXPECTED_OPTIMAL_INPUT, false);
    }

    /** When the minimum bound is too high. */
    @Test
    void testTooHighMinimumBound() {
        doTest(21, 21, false);
    }

    /** With a valid minimum bound, but constrained upper bound. */
    @Test
    void testUpperBound() {
        doTest(3, UPPER_BOUND, true);
    }

    private void doTest(int minimumBound, int expectedOptima, boolean imposeUpperBound) {
        SearchClosestValueMonoticallyIncreasing search =
                imposeUpperBound ? SEARCH_LOWER_AND_UPPER_BOUND : SEARCH_LOWER_BOUND_ONLY;
        assertEquals(expectedOptima, search.findOptimalInput(minimumBound));
    }
}
