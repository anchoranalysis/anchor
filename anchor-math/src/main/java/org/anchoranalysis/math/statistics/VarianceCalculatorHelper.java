package org.anchoranalysis.math.statistics;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class VarianceCalculatorHelper {

    /**
     * Calculate the variance, given a sum, running sum and count.
     *
     * @param sum the sum of all values.
     * @param sumSquares the sum of squares of all values.
     * @param count how many separate values exist.
     * @return the variance.
     */
    public static double calculateVariance(double sum, double sumSquares, long count) {
        Preconditions.checkArgument(sumSquares >= 0);
        Preconditions.checkArgument(sum >= 0);
        Preconditions.checkArgument(count >= 0);

        // Formula for variance
        // https://en.wikipedia.org/wiki/Variance
        // https://www.sciencebuddies.org/science-fair-projects/science-fair/variance-and-standard-deviation
        double second = Math.pow(sum, 2.0) / count;
        double val = (sumSquares - second) / count;

        assert (val >= 0);
        return val;
    }
}
