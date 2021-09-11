/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.math.histogram;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * Further statistics that can be derived from a histogram in addition to those existing as direct methods of {@link Histogram}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistogramStatistics {

    /**
     * Calculates the <b>coefficient-of-variation</b> of a distribution of values, represented by their histogram.
     * 
     * <p>This is the ratio of the standard-deviation to the mean.
     * 
     * <p>See <a href="https://en.wikipedia.org/wiki/Coefficient_of_variation">coefficient-of-variation on Wikipedia</a>.
     * 
     * @param histogram the histogram to calculate from.
     * @return the coefficient-of-variation.
     * @throws OperationFailedException if the statistic is undefined, for example with zero mean.
     */
    public static double coefficientOfVariation(Histogram histogram)
            throws OperationFailedException {
        double mean = histogram.mean();

        if (mean == 0) {
            throw new OperationFailedException(
                    "The mean is 0 so the coefficient-of-variation is undefined");
        }

        return histogram.standardDeviation() / mean;
    }

    /**
     * Calculates the <b>skewness</b> of a distribution of values, represented by their histogram.
     * 
     * <p>This is the third standardized moment.
     * 
     * <p>See <a href="https://en.wikipedia.org/wiki/Skewness">skewness on Wikipedia</a>.
     * 
     * @param histogram the histogram to calculate from.
     * @return the skewness.
     * @throws OperationFailedException if the statistic is undefined, for example with zero variance.
     */
    public static double skewness(Histogram histogram) throws OperationFailedException {

        long count = histogram.getTotalCount();
        double mean = histogram.mean();
        double stdDev = histogram.standardDeviation();

        // Calculated using formula in https://en.wikipedia.org/wiki/Skewness
        long firstTerm = histogram.calculateSumCubes() / count;
        double secondTerm = -3.0 * mean * stdDev * stdDev;
        double thirdTerm = mean * mean * mean;

        double denominator = stdDev * stdDev * stdDev;

        return (firstTerm + secondTerm + thirdTerm) / denominator;
    }
    
    /**
     * Calculates the <b>kurtosis</b> of a distribution of values, represented by their histogram.
     * 
     * <p>This is fourth standardized moment.
     * 
     * <p>See <a href="https://en.wikipedia.org/wiki/Kurtosis">kurtosis on Wikipedia</a>.
     * 
     * @param histogram the histogram to calculate from.
     * @return the kurtosis.
     * @throws OperationFailedException if the statistic is undefined, for example with zero variance.
     */
    public static double kurtosis(Histogram histogram) throws OperationFailedException {

        // Kurtosis is calculated as in
        // http://www.macroption.com/kurtosis-formula/
        double histogramMean = histogram.mean();

        double fourthMomentAboutMean = histogram.mean(4.0, histogramMean);

        double varianceSquared = Math.pow(histogram.variance(), 2.0);

        if (varianceSquared == 0) {
            // We don't return infinity, but rather the maximum value allowed
            throw new OperationFailedException("Kurtosis is undefined as there is 0 variance");
        }

        return fourthMomentAboutMean / varianceSquared;
    }
}
