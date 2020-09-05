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

import static java.lang.Math.toIntExact;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.LongUnaryOperator;
import lombok.Getter;
import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.math.statistics.VarianceCalculator;
import org.apache.commons.lang.ArrayUtils;

public final class Histogram {

    /** Consumers a bin and corresponding count. */
    @FunctionalInterface
    public interface BinConsumer {
        void accept(int bin, int count);
    }

    /** Minimum bin-value (by default 0) inclusive */
    private int minBin;

    /** Maximum bin-value inclusive */
    @Getter private int maxBin;

    private int[] counts;

    private long sumCount = 0;

    public Histogram(int maxValue) {
        this(0, maxValue);
    }

    public Histogram(int minValue, int maxValue) {
        counts = new int[maxValue - minValue + 1];
        sumCount = 0;
        this.maxBin = maxValue;
        this.minBin = minValue;
    }

    public Histogram duplicate() {
        Histogram out = new Histogram(minBin, maxBin);
        out.counts = ArrayUtils.clone(counts);
        out.sumCount = sumCount;
        return out;
    }

    public void reset() {
        sumCount = 0;
        for (int i = minBin; i <= maxBin; i++) {
            set(i, 0);
        }
    }

    public void zeroValue(int value) {
        int index = index(value);
        sumCount -= counts[index];
        counts[index] = 0;
    }

    public void transferValue(int sourceValue, int destinationValue) {
        int srcIndex = index(sourceValue);

        incrementCount(destinationValue, counts[srcIndex]);
        counts[srcIndex] = 0;
    }

    public void incrementValue(int value) {
        incrementCount(value, 1);
        sumCount++;
    }

    public void incrementValueBy(int value, int increase) {
        incrementCount(value, increase);
        sumCount += increase;
    }

    /**
     * long version of incrValBy
     *
     * @throws ArithmeticException if incresae cannot be converted to an int safely
     */
    public void incrementValueBy(int value, long increase) {
        incrementValueBy(value, toIntExact(increase));
    }

    public void removeBelowThreshold(int threshold) {
        for (int bin = minBin; bin < threshold; bin++) {
            zeroValue(bin);
        }
        // Now chop off the unneeded values and set a new minimum
        chopBefore(index(threshold));
        this.minBin = threshold;
    }

    public boolean isEmpty() {
        return sumCount == 0;
    }

    public int getCount(int value) {
        return counts[index(value)];
    }

    /** The number of items in the histogram {@code (maxBin - minBin + 1)} */
    public int size() {
        return counts.length;
    }

    public void addHistogram(Histogram other) throws OperationFailedException {
        if (this.getMaxBin() != other.getMaxBin()) {
            throw new OperationFailedException(
                    "Cannot add histograms with different max-bin-values");
        }
        if (this.minBin != other.minBin) {
            throw new OperationFailedException(
                    "Cannot add histograms with different min-bin-values");
        }

        for (int bin = minBin; bin <= getMaxBin(); bin++) {
            int otherCount = other.getCount(bin);
            incrementCount(bin, otherCount);
            sumCount += otherCount;
        }
    }

    public double mean() throws OperationFailedException {

        checkAtLeastOneItemExists();

        long sum = 0;

        for (int bin = minBin; bin <= maxBin; bin++) {
            sum += getAsLong(bin) * bin;
        }

        return ((double) sum) / sumCount;
    }

    public double meanGreaterEqualTo(int value) throws OperationFailedException {
        checkAtLeastOneItemExists();

        long sum = 0;
        long count = 0;

        int startMin = Math.max(value, minBin);

        for (int bin = startMin; bin <= maxBin; bin++) {
            long num = getAsLong(bin);
            sum += bin * num;
            count += num;
        }

        return ((double) sum) / count; // NOSONAR
    }

    public double meanNonZero() throws OperationFailedException {
        checkAtLeastOneItemExists();

        long sum = 0;

        for (int bin = minBin; bin <= maxBin; bin++) {
            sum += bin * getAsLong(bin);
        }

        return ((double) sum) / (sumCount - getCount(0));
    }

    public long sumNonZero() {
        return calculateSum() - getCount(0);
    }

    public void scaleBy(double factor) {

        int sum = 0;

        for (int bin = minBin; bin <= maxBin; bin++) {

            int index = index(bin);

            int valNew = (int) Math.round(factor * counts[index]);
            sum += valNew;
            counts[index] = valNew;
        }

        sumCount = sum;
    }

    public int quantile(double quantile) throws OperationFailedException {
        checkAtLeastOneItemExists();

        double pos = quantile * sumCount;

        long sum = 0;
        for (int bin = minBin; bin <= maxBin; bin++) {
            sum += getCount(bin);

            if (sum > pos) {
                return bin;
            }
        }
        return calculateMaximum();
    }

    public int quantileAboveZero(double quantile) throws OperationFailedException {
        checkAtLeastOneItemExists();

        long countMinusZero = sumCount - getCount(0);

        double pos = quantile * countMinusZero;

        int startMin = Math.max(1, minBin);

        long sum = 0;
        for (int bin = startMin; bin <= maxBin; bin++) {
            sum += getCount(bin);

            if (sum > pos) {
                return bin;
            }
        }
        return calculateMaximum();
    }

    public boolean hasAboveZero() {

        int startMin = Math.max(1, minBin);

        for (int bin = startMin; bin <= maxBin; bin++) {
            if (getCount(bin) > 0) {
                return true;
            }
        }
        return false;
    }

    public double percentGreaterEqualTo(int binThreshold) {

        int startMin = Math.max(binThreshold, minBin);

        long sum = 0;
        for (int bin = startMin; bin <= maxBin; bin++) {
            sum += getCount(bin);
        }

        return ((double) sum) / sumCount;
    }

    public int calculateMode() throws OperationFailedException {
        checkAtLeastOneItemExists();
        return calculateMode(0);
    }

    /**
     * Calculates the mode of the histogram values i.e. the most frequently occurring item
     *
     * <p>Should only be called on a histogram with at least one item
     */
    public int calculateMode(int startValue) throws OperationFailedException {
        checkAtLeastOneItemExists();

        int maxIndex = -1;
        int maxValue = -1;

        for (int bin = startValue; bin <= maxBin; bin++) {
            int val = getCount(bin);
            if (val > maxValue) {
                maxValue = val;
                maxIndex = bin;
            }
        }

        return maxIndex;
    }

    /**
     * Calculates the <i>maximum</i> of the histogram values i.e. the highest bin with a non-zero
     * count
     *
     * <p>Should only be called on a histogram with at least one item
     */
    public int calculateMaximum() throws OperationFailedException {
        checkAtLeastOneItemExists();

        for (int bin = maxBin; bin >= minBin; bin--) {
            if (getCount(bin) > 0) {
                return bin;
            }
        }

        throw new AnchorImpossibleSituationException();
    }

    /**
     * Calculates the <i>minimum</i> of the histogram values i.e. the lowest bin with a non-zero
     * count
     *
     * <p>Should only be called on a histogram with at least one item
     */
    public int calculateMinimum() throws OperationFailedException {
        checkAtLeastOneItemExists();

        for (int bin = minBin; bin <= maxBin; bin++) {
            if (getCount(bin) > 0) {
                return bin;
            }
        }

        throw new AnchorImpossibleSituationException();
    }

    public long calculateSum() {
        return calculateSumHelper(i -> i);
    }

    public long calculateSumSquares() {
        return calculateSumHelper(i -> i * i);
    }

    public long calculateSumCubes() {
        return calculateSumHelper(i -> i * i * i);
    }

    public int calculateCountNonZero() {

        int num = 0;

        for (int bin = minBin; bin <= maxBin; bin++) {
            if (getCount(bin) > 0) {
                num++;
            }
        }

        return num;
    }

    public double standardDeviation() throws OperationFailedException {
        checkAtLeastOneItemExists();
        return Math.sqrt(variance());
    }

    public double variance() {
        return new VarianceCalculator(calculateSum(), calculateSumSquares(), getTotalCount())
                .variance();
    }

    public long countThreshold(RelationToThreshold relationToThreshold) {

        RelationToValue relation = relationToThreshold.relation();
        double threshold = relationToThreshold.threshold();

        long sum = 0;

        for (int bin = minBin; bin <= maxBin; bin++) {

            if (relation.isRelationToValueTrue(bin, threshold)) {
                sum += getAsLong(bin);
            }
        }

        return sum;
    }

    // Thresholds (generates a new histogram, existing object is unchanged)

    public Histogram threshold(RelationToThreshold relationToThreshold) {

        RelationToValue relation = relationToThreshold.relation();
        double threshold = relationToThreshold.threshold();

        Histogram out = new Histogram(maxBin);
        out.sumCount = 0;
        for (int bin = minBin; bin <= maxBin; bin++) {

            if (relation.isRelationToValueTrue(bin, threshold)) {
                int s = getCount(bin);
                out.set(bin, s);
                out.sumCount += s;
            }
        }

        return out;
    }

    // Doesn't show zero values
    public String toString() {
        return concatenateForEachBin(
                bin -> {
                    int count = getCount(bin);
                    if (count != 0) {
                        return String.format("%d: %d%n", bin, count);
                    } else {
                        return "";
                    }
                });
    }

    // Includes zero values

    public String csvString() {
        return concatenateForEachBin(bin -> String.format("%d, %d%n", bin, getCount(bin)));
    }

    public long getTotalCount() {
        return sumCount;
    }

    public Histogram extractValuesFromRight(long numberValues) {

        Histogram out = new Histogram(maxBin);

        long remaining = numberValues;

        // We keep taking pixels from the histogram until we have reached our quota
        for (int bin = getMaxBin(); bin >= minBin; bin--) {

            int count = getCount(bin);

            // Skip if there's nothing there
            if (count != 0) {

                remaining = extractBin(out, bin, count, remaining);

                if (remaining == 0) {
                    break;
                }
            }
        }
        return out;
    }

    public Histogram extractValuesFromLeft(long numberValues) {

        Histogram out = new Histogram(maxBin);

        long remaining = numberValues;

        // We keep taking pixels from the histogram until we have reached our quota
        for (int bin = minBin; bin <= getMaxBin(); bin++) {

            int count = getCount(bin);

            // Skip if there's nothing there
            if (count != 0) {
                remaining = extractBin(out, bin, count, remaining);

                if (remaining == 0) {
                    break;
                }
            }
        }
        return out;
    }

    /**
     * calculates the mean after raising each histogram value to a power i.e. mean of {@code
     * histogramVal^power}
     */
    public double mean(double power) throws OperationFailedException {
        checkAtLeastOneItemExists();
        return mean(power, 0.0);
    }

    /** calculates the mean of {@code (histogramVal - subtractVal)^power} */
    public double mean(double power, double subtractVal) throws OperationFailedException {
        checkAtLeastOneItemExists();

        double sum = 0;

        for (int bin = minBin; bin <= maxBin; bin++) {
            double binSubtracted = (bin - subtractVal);
            sum += getAsLong(bin) * Math.pow(binSubtracted, power);
        }

        return sum / sumCount;
    }

    /**
     * Calls {@code consumer} for every bin-value, <i>increasing</i> from min to max.
     *
     * @param consumer called for every bin
     */
    public void iterateBins(BinConsumer consumer) {
        for (int bin = minBin; bin <= maxBin; bin++) {
            consumer.accept(bin, getCount(bin));
        }
    }

    /**
     * Calls {@code consumer} for every bin-value until a limit, <i>increasing</i> from min to limit
     * (inclusive).
     *
     * @param limit the maximum-bin to consume
     * @param consumer called for every bin
     */
    public void iterateBinsUntil(int limit, BinConsumer consumer) {
        for (int bin = minBin; bin <= limit; bin++) {
            consumer.accept(bin, getCount(bin));
        }
    }

    private long calculateSumHelper(LongUnaryOperator func) {

        long sum = 0;

        for (int bin = minBin; bin <= maxBin; bin++) {
            long add = getAsLong(bin) * func.applyAsLong((long) bin);
            sum += add;
        }

        return sum;
    }

    // The index in the array the value is stored at
    private int index(int value) {
        return value - minBin;
    }

    // Sets a count for a value
    private void set(int value, int cntToSet) {
        counts[index(value)] = cntToSet;
    }

    // Sets a count for a value
    private void incrementCount(int value, int incrementBy) {
        counts[index(value)] += incrementBy;
    }

    private long getAsLong(int value) {
        return (long) getCount(value);
    }

    private void chopBefore(int index) {
        counts = Arrays.copyOfRange(counts, index, counts.length);
    }

    private void checkAtLeastOneItemExists() throws OperationFailedException {
        if (isEmpty()) {
            throw new OperationFailedException(
                    "There are no items in the histogram so this operation cannot occur");
        }
    }

    /**
     * Places a particular bin in a destination histogram.
     *
     * <p>Either the whole bin is transferred or only some of the bin so that {@code remaining >=
     * 0}.
     *
     * @param destination the destination histogram
     * @param bin the bin-value
     * @param countForBin the count
     * @param remaining the count remaining that can still be transferred
     * @return an updated value for remaining after subtracting the transferred count
     */
    private static long extractBin(
            Histogram destination, int bin, int countForBin, long remaining) {
        // If there's more or just enough remaining than we have, we transfer the entire bin
        if (remaining >= countForBin) {
            destination.incrementValueBy(bin, countForBin);
            return remaining - countForBin;
        } else {
            // Otherwise partially transfer the bin
            destination.incrementValueBy(bin, remaining);
            return 0;
        }
    }

    /**
     * Builds a string that is a concatenation of strings generated for each bin in the histogram
     */
    private String concatenateForEachBin(IntFunction<String> stringForBin) {
        StringBuilder builder = new StringBuilder();
        for (int bin = minBin; bin <= maxBin; bin++) {
            builder.append(stringForBin.apply(bin));
        }
        return builder.toString();
    }
}
