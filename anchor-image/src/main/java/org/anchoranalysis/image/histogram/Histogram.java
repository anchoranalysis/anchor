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

import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.math.statistics.VarianceCalculator;

public interface Histogram {

    Histogram duplicate();

    void reset();

    void zeroValue(int val);

    void transferValue(int srcVal, int destVal);

    void incrementValue(int val);

    void incrValueBy(int val, int increase);

    void incrValueBy(int val, long increase);

    boolean isEmpty();

    int getCount(int val);

    int size();

    void addHistogram(Histogram other) throws OperationFailedException;

    double mean() throws OperationFailedException;

    /**
     * calculates the mean after raising each histogram value to a power i.e. mean of
     * histogramVal^power
     */
    double mean(double power) throws OperationFailedException;

    /** calculates the mean of (histogramVal - subtractVal)^power */
    double mean(double power, double subtractVal) throws OperationFailedException;

    double meanGreaterEqualTo(int val) throws OperationFailedException;

    double meanNonZero() throws OperationFailedException;

    long sumNonZero();

    void scaleBy(double factor);

    int quantile(double quantile) throws OperationFailedException;

    int quantileAboveZero(double quantile) throws OperationFailedException;

    boolean hasAboveZero();

    double percentGreaterEqualTo(int intensity);

    default int calculateMode() throws OperationFailedException {
        return calculateMode(0);
    }

    // Should only be called on a histogram with at least one item
    int calculateMode(int startIndex) throws OperationFailedException;

    // Should only be called on a histogram with at least one item
    int calculateMaximum() throws OperationFailedException;

    // Should only be called on a histogram with at least one item
    int calculateMinimum() throws OperationFailedException;

    long calculateSum();

    long calculateSumSquares();

    long calculateSumCubes();

    void removeBelowThreshold(int threshold);

    int calculateCountNonZero();

    double standardDeviation() throws OperationFailedException;

    default double variance() {
        return new VarianceCalculator(calculateSum(), calculateSumSquares(), getTotalCount()).variance();
    }

    long countThreshold(RelationToThreshold relationToThreshold);

    // Thresholds (generates a new histogram, existing object is unchanged)
    Histogram threshold(RelationToThreshold relationToThreshold);

    String toString();

    default String csvString() {
        StringBuilder sb = new StringBuilder();
        for (int t = 0; t <= getMaxBin(); t++) {
            sb.append(String.format("%d, %d%n", t, getCount(t)));
        }
        return sb.toString();
    }

    int getMaxBin();

    int getMinBin();

    long getTotalCount();

    Histogram extractValuesFromRight(long numberValues);

    Histogram extractValuesFromLeft(long numberValues);
}
