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
