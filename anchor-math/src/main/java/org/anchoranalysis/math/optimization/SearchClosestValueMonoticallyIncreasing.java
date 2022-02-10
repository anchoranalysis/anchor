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

import java.util.function.IntPredicate;
import lombok.RequiredArgsConstructor;

/**
 * Given a <a
 * href="https://opencurriculum.org/5512/monotonically-increasing-and-decreasing-functions-an-algebraic-approach/">monotonically
 * increasing</a> function of an integer, determine the input value that provides an output value as
 * close to {@code target} as possible.
 *
 * <p>The input-values must be integers, and have a minimum bound.
 *
 * <p>It begins at the minimum-bound, and in a similar manner to <a
 * href="https://en.wikipedia.org/wiki/Exponential_search">exponential search</a>, successively
 * doubles, until a lower and upper bound are found.
 *
 * <p>It then proceeds with a <a
 * href="https://en.wikipedia.org/wiki/Binary_search_algorithm">binary-search</a>-style recursive
 * evaluation to find the integer value that produces the exact closest value to {@code target}.
 *
 * <p>An upper bound on the input-values may also be optionally imposed via {@code boundUpper}.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class SearchClosestValueMonoticallyIncreasing {

    /**
     * The function that calculates the output value for a particular input {@code int}.
     *
     * <p>It must be <a
     * href="https://opencurriculum.org/5512/monotonically-increasing-and-decreasing-functions-an-algebraic-approach/">monotonically
     * increasing</a>.
     */
    @FunctionalInterface
    public interface ValueFunction {

        /**
         * Calculate the output value, for a particular input-value.
         *
         * @param input the input integer.
         * @return the calculated output value.
         */
        public double calculate(int input);
    }

    /**
     * The target value to try and be closest to when evaluating {@code function} on differing
     * inputs.
     */
    private final double target;

    /** Calculates an output value for a particular input integer. */
    private final ValueFunction function;

    /**
     * An optional upper bound, which must be false for all values less than a particular threshold,
     * and true all values over it.
     *
     * <p>This allows an additional constraint to be placed on the exploration of values.
     *
     * <p>It is expressed as a predicate rather than as a constant, as the threshold may not be
     * known exactly, and this allows the search function to explore it, as it also seeks the
     * closest value to {@code target}.
     *
     * <p>It is <b>guaranteed</b> that this predicate will only be evaluated after a call to {@code
     * function} with the same value - and this call was the most-recent call. This may help
     * optimize implementation code of {@code function} and {@code boundUpper}.
     */
    private final IntPredicate boundUpper;

    /**
     * Create without an upper bound.
     *
     * @param target the target value to try and be closest to when evaluating {@code function} on
     *     differing inputs.
     * @param function calculates an output value for a particular input integer.
     */
    public SearchClosestValueMonoticallyIncreasing(double target, ValueFunction function) {
        this.target = target;
        this.function = function;

        // Disables the upper bound.
        this.boundUpper = value -> false;
    }

    /**
     * Finds the input value that produces a calculated-value that is closest to {@code target}.
     *
     * @param boundLower a lower bound on the input range, inclusive.
     * @return the input-value that is optimal to produce the closest value to {@code target} when
     *     evaluated using {@code calculateValue}.
     */
    public int findOptimalInput(int boundLower) {
        int boundPrevious = boundLower;
        int bound = boundLower * 2;
        double height = function.calculate(bound);
        while (height < target && !boundUpper.test(bound)) {
            boundPrevious = bound;
            bound *= 2;
            height = function.calculate(bound);
        }
        // The best input-value lies between boundPrevious and bound, and can be found with binary
        // search in this range.
        return binarySearch(boundPrevious, bound);
    }

    /**
     * A binary-search style recursive algorithm to successively narrow the range of values until
     * the closest is found.
     *
     * @param the minimum input value of the the range to search (inclusive).
     * @param the maximum input value of the the range to search (inclusive).
     */
    private int binarySearch(int lower, int upper) {
        if (lower == upper || lower == (upper - 1)) {
            return lower;
        }

        // Find the mean betweeo the two
        int mean = (upper + lower) / 2;
        double outputMean = function.calculate(mean);

        if (outputMean > target || boundUpper.test(mean)) {
            return binarySearch(lower, mean);
        } else {
            return binarySearch(mean, upper);
        }
    }
}
