/* (C)2020 */
package org.anchoranalysis.image.histogram;

import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.math.statistics.VarianceCalculator;

public interface Histogram {

    Histogram duplicate();

    void reset();

    void zeroVal(int val);

    void transferVal(int srcVal, int destVal);

    void incrVal(int val);

    void incrValBy(int val, int increase);

    void incrValBy(int val, long increase);

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

    default int calcMode() throws OperationFailedException {
        return calcMode(0);
    }

    // Should only be called on a histogram with at least one item
    int calcMode(int startIndex) throws OperationFailedException;

    // Should only be called on a histogram with at least one item
    int calcMax() throws OperationFailedException;

    // Should only be called on a histogram with at least one item
    int calcMin() throws OperationFailedException;

    long calcSum();

    long calcSumSquares();

    long calcSumCubes();

    void removeBelowThreshold(int threshold);

    int calcNumNonZero();

    double stdDev() throws OperationFailedException;

    default double variance() {
        return new VarianceCalculator(calcSum(), calcSumSquares(), getTotalCount()).variance();
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

    Histogram extractPixelsFromRight(long numPixels);

    Histogram extractPixelsFromLeft(long numPixels);
}
