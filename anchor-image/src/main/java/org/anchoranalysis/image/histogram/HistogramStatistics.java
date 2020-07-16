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

package org.anchoranalysis.image.histogram;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Further statistics that can be derived from a histogram in addition to those callable directly
 * from the {@link Histogram} class
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistogramStatistics {

    public static double coefficientOfVariation(Histogram hist) throws OperationFailedException {
        double mean = hist.mean();

        if (mean == 0) {
            throw new OperationFailedException(
                    "The mean is 0 so the coefficient-of-variation is undefined");
        }

        return hist.stdDev() / mean;
    }

    public static double kurtosis(Histogram hist) throws OperationFailedException {

        // Kurtosis is calculated as in
        // http://www.macroption.com/kurtosis-formula/
        double histMean = hist.mean();

        double fourthMomentAboutMean = hist.mean(4.0, histMean);

        double varSquared = Math.pow(hist.variance(), 2.0);

        if (varSquared == 0) {
            // We don't return infinity, but rather the maximum value allowed
            throw new OperationFailedException("Kurtosis is undefined as there is 0 variance");
        }

        return fourthMomentAboutMean / varSquared;
    }

    public static double skewness(Histogram hist) throws OperationFailedException {

        long count = hist.getTotalCount();
        double mean = hist.mean();
        double sd = hist.stdDev();

        // Calculated using formula in https://en.wikipedia.org/wiki/Skewness
        long firstTerm = hist.calcSumCubes() / count;
        double secondTerm = -3.0 * mean * sd * sd;
        double thirdTerm = mean * mean * mean;

        double dem = sd * sd * sd;

        return (((double) firstTerm) + secondTerm + thirdTerm) / dem;
    }
}
