package org.anchoranalysis.math.statistics;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Calculates a Z-Score.
 *
 * <p>See <a href="https://en.wikipedia.org/wiki/Standard_score">Standard score on Wikipedia</a>.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ZScoreCalculator {

    /**
     * Calculates a Z-Score for a particular value and parameterization.
     *
     * @param value the value to convert.
     * @param mean the mean parameter of the population.
     * @param scale the scale parameter of the population, usually standard-deviation.
     * @return {@code value} converted to a Z-Score.
     */
    public static double calculateZScore(double value, double mean, double scale) {
        return (value - mean) / scale;
    }
}
