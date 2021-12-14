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

import static java.lang.Math.toIntExact;

import java.util.Arrays;
import java.util.function.DoublePredicate;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.LongUnaryOperator;
import lombok.Getter;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.math.statistics.VarianceCalculatorLong;
import org.apache.commons.lang.ArrayUtils;

/**
 * A histogram of integer values.
 *
 * <p>The bin-size is always 1, so each bin corresponds to a discrete integer.
 *
 * <p>See <a href="https://en.wikipedia.org/wiki/Histogram">histogram on Wikipedia</a>.
 *
 * <p>This can be used to record a discrete probability distribution, and is typically used in the
 * Anchor software to record the distribution of image voxel intensity values.
 *
 * <p>Note that this is dense implementation and memory is allocated to store all values from {@code
 * minValue} to {@code maxValue} (inclusive). This can be a lot of memory for e.g. unsigned-short
 * value types. However, it allows for a maximally efficient incrementing through voxels in an
 * image, without intermediate structures.
 *
 * @author Owen Feehan
 */
public final class Histogram {

    /** Consumes a bin and corresponding count. */
    @FunctionalInterface
    public interface BinConsumer {

        /**
         * Accepts a particular bin and corresponding count.
         *
         * @param bin the bin.
         * @param count the corresponding count.
         */
        void accept(int bin, int count);
    }

    /** Minimum possible value in the histogram (inclusive). */
    private int minValue;

    /** Maximum possible value in the histogram (inclusive). */
    @Getter private int maxValue;

    private int[] counts;

    private long sumCount = 0;

    /**
     * Constructs with a maximum value, and assuming a minimum value of 0.
     *
     * @param maxValue maximum possible value in the histogram (inclusive).
     */
    public Histogram(int maxValue) {
        this(0, maxValue);
    }

    /**
     * Constructs with a maximum value, and assuming a minimum value of 0.
     *
     * @param minValue minimum possible value in the histogram (inclusive).
     * @param maxValue maximum possible value in the histogram (inclusive).
     */
    public Histogram(int minValue, int maxValue) {
        counts = new int[maxValue - minValue + 1];
        sumCount = 0;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    /**
     * Creates a deep-copy of the current object.
     *
     * @return a deep-copy.
     */
    public Histogram duplicate() {
        Histogram out = new Histogram(minValue, maxValue);
        out.counts = ArrayUtils.clone(counts);
        out.sumCount = sumCount;
        return out;
    }

    /** Sets the count for all values to 0. */
    public void reset() {
        sumCount = 0;
        for (int i = minValue; i <= maxValue; i++) {
            set(i, 0);
        }
    }

    /**
     * Sets the count for a particular value to 0.
     *
     * @param value the value whose count is zeroed.
     */
    public void zeroValue(int value) {
        int index = index(value);
        sumCount -= counts[index];
        counts[index] = 0;
    }

    /**
     * Moves all count for a particular value and adds it to the count for another.
     *
     * @param valueFrom the value whose count is moved, after which it's count is set to zero.
     * @param valueTo the value to which the count for {@code valueFrom} is added.
     */
    public void transferCount(int valueFrom, int valueTo) {
        int indexFrom = index(valueFrom);

        incrementCount(valueTo, counts[indexFrom]);
        counts[indexFrom] = 0;
    }

    /**
     * Increments the count for a particular value by one.
     *
     * @param value the value whose count will be incremented by one.
     */
    public void incrementValue(int value) {
        incrementCount(value, 1);
        sumCount++;
    }

    /**
     * Increments the count for a particular value.
     *
     * @param value the value whose count will be incremented.
     * @param increase how much to increase the count by.
     */
    public void incrementValueBy(int value, int increase) {
        incrementCount(value, increase);
        sumCount += increase;
    }

    /**
     * Like {@link #incrementValueBy(int, int)} but accepts a {@code long} as the {@code increase}
     * argument.
     *
     * @param value the value whose count will be incremented.
     * @param increase how much to increase the count by.
     * @throws ArithmeticException if increase cannot be converted to an {@code int} safely.
     */
    public void incrementValueBy(int value, long increase) {
        incrementValueBy(value, toIntExact(increase));
    }

    /**
     * All values less than {@code threshold} are removed.
     *
     * @param threshold values greater or equal to this are kept in the histogram, lesser values are
     *     removed.
     */
    public void removeBelowThreshold(int threshold) {
        for (int bin = minValue; bin < threshold; bin++) {
            zeroValue(bin);
        }
        // Now chop off the unneeded values and set a new minimum
        chopBefore(index(threshold));
        this.minValue = threshold;
    }

    /**
     * If no value exists in the histogram with a count greater than zero.
     *
     * @return true iff the histogram has zero-count for all values.
     */
    public boolean isEmpty() {
        return sumCount == 0;
    }

    /**
     * The count corresponding to a particular value.
     *
     * @param value the value (the bin) to find a count for.
     * @return the corresponding count.
     */
    public int getCount(int value) {
        return counts[index(value)];
    }

    /**
     * The size of the range of values in the histogram.
     *
     * <p>This is equivalent to {@code (maxValue - minValue + 1)}.
     *
     * @return the number of values represented in the histogram.
     */
    public int size() {
        return counts.length;
    }

    /**
     * Adds the counts from another histogram to the current object.
     *
     * <p>Both histograms must have identical minimum and maximum values, and therefore represent
     * the same range of values.
     *
     * @param other the histogram to add.
     * @throws OperationFailedException if the histograms do have identical minimum and maximum
     *     values.
     */
    public void addHistogram(Histogram other) throws OperationFailedException {
        if (this.getMaxValue() != other.getMaxValue()) {
            throw new OperationFailedException(
                    "Cannot add histograms with different max-bin-values");
        }
        if (this.minValue != other.minValue) {
            throw new OperationFailedException(
                    "Cannot add histograms with different min-bin-values");
        }

        for (int bin = minValue; bin <= getMaxValue(); bin++) {
            int otherCount = other.getCount(bin);
            incrementCount(bin, otherCount);
            sumCount += otherCount;
        }
    }

    /**
     * Calculates the <b>mean</b> of the histogram values, considering their frequency.
     *
     * <p>Specifically, this is the mean of {@code value * countFor(value)} across all values.
     *
     * @return the mean.
     * @throws OperationFailedException if the histogram has no values.
     */
    public double mean() throws OperationFailedException {

        checkAtLeastOneItemExists();

        long sum = 0;

        for (int bin = minValue; bin <= maxValue; bin++) {
            sum += getCountAsLong(bin) * bin;
        }

        return ((double) sum) / sumCount;
    }

    /**
     * Calculates the corresponding value for a particular <b>quantile</b> in the distribution of
     * values in the histogram.
     *
     * <p>See <a href="https://en.wikipedia.org/wiki/Quantile">Quantile on wikipedia</a>.
     *
     * <p>A quantile of 0.3, would return the minimal value, greater or equal to at least 30% of the
     * count.
     *
     * @param quantile the quantile, in the interval {@code [0, 1]}.
     * @return the mean.
     * @throws OperationFailedException if the histogram has no values, or the quantile is outside
     *     acceptable bounds.
     */
    public int quantile(double quantile) throws OperationFailedException {
        checkAtLeastOneItemExists();

        if (quantile < 0 || quantile > 1) {
            throw new OperationFailedException(
                    String.format("The quantile must be >= 0 and <= 1 but is %f", quantile));
        }

        double threshold = quantile * sumCount;

        long sum = 0;
        for (int bin = minValue; bin <= maxValue; bin++) {
            sum += getCount(bin);

            if (sum > threshold) {
                return bin;
            }
        }
        return calculateMaximum();
    }

    /**
     * Whether at least one value, greater or equal to {@code startMin} has non-zero count?
     *
     * @param threshold only values greater or equal to {@code threshold} are considered. Use 0 for
     *     all values.
     * @return true iff at least one value in this range has a non-zero count, false if all values
     *     in the range are zero.
     */
    public boolean hasNonZeroCount(int threshold) {

        for (int bin = threshold; bin <= maxValue; bin++) {
            if (getCount(bin) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the <b>mode</b> of the histogram values.
     *
     * <p>The mode is the most frequently occurring item.
     *
     * @return the mode.
     * @throws OperationFailedException if the histogram has no values.
     */
    public int calculateMode() throws OperationFailedException {
        checkAtLeastOneItemExists();

        int maxIndex = -1;
        int maxCount = -1;

        for (int bin = minValue; bin <= maxValue; bin++) {
            int count = getCount(bin);
            if (count > maxCount) {
                maxCount = count;
                maxIndex = bin;
            }
        }

        return maxIndex;
    }

    /**
     * Calculates the <b>maximum value with non zero-count</b> among the histogram values.
     *
     * @return the maximal value with non-zero count.
     * @throws OperationFailedException if the histogram has no values.
     */
    public int calculateMaximum() throws OperationFailedException {
        checkAtLeastOneItemExists();

        for (int bin = maxValue; bin >= minValue; bin--) {
            if (getCount(bin) > 0) {
                return bin;
            }
        }

        throw new AnchorImpossibleSituationException();
    }

    /**
     * Calculates the <b>minimum value with non zero-count</b> among the histogram values.
     *
     * @return the minimal value with non-zero count.
     * @throws OperationFailedException if the histogram has no values.
     */
    public int calculateMinimum() throws OperationFailedException {
        checkAtLeastOneItemExists();

        for (int bin = minValue; bin <= maxValue; bin++) {
            if (getCount(bin) > 0) {
                return bin;
            }
        }

        throw new AnchorImpossibleSituationException();
    }

    /**
     * Calculates the <b>sum of all values</b> in the distribution considering their counts.
     *
     * <p>Specifically, the sum is {@code value * countFor(value)} across all values.
     *
     * @return the sum.
     */
    public long calculateSum() {
        return calculateSumHelper(value -> value);
    }

    /**
     * Calculates the <b>sum of the squares of all values</b> in the distribution considering their
     * counts.
     *
     * <p>Specifically, the sum is {@code value^2 * countFor(value)} across all values.
     *
     * @return the sum of squares.
     */
    public long calculateSumSquares() {
        return calculateSumHelper(value -> value * value);
    }

    /**
     * Calculates the <b>cube of the squares of all values</b> in the distribution considering their
     * counts.
     *
     * <p>Specifically, the sum is {@code value^3 * countFor(value)} across all values.
     *
     * @return the sum of cubes.
     */
    public long calculateSumCubes() {
        return calculateSumHelper(value -> value * value * value);
    }

    /**
     * Calculates the <b>standard-deviation</b> of the distribution represented by the histogram.
     *
     * @return the standard-deviation.
     * @throws OperationFailedException if the histogram has no values.
     */
    public double standardDeviation() throws OperationFailedException {
        checkAtLeastOneItemExists();
        return Math.sqrt(variance());
    }

    /**
     * Calculates the <b>variance</b> of the distribution represented by the histogram.
     *
     * @return the variance.
     * @throws OperationFailedException if the histogram has no values.
     */
    public double variance() throws OperationFailedException {
        checkAtLeastOneItemExists();
        return new VarianceCalculatorLong(calculateSum(), calculateSumSquares(), getTotalCount())
                .variance();
    }

    /**
     * Gets the total count of all values that match a predicate.
     *
     * @param predicate the predicate a value must match to be included in the count.
     * @return the sum of the counts corresponding to all values that match the predicate.
     */
    public long countMatching(IntPredicate predicate) {

        long sum = 0;

        for (int bin = minValue; bin <= maxValue; bin++) {

            if (predicate.test(bin)) {
                sum += getCountAsLong(bin);
            }
        }

        return sum;
    }

    /**
     * Generates a new histogram containing only values that match a predicate.
     *
     * <p>This is an <i>immutable operation</i>. The existing histogram's values are unchanged.
     *
     * @param predicate a condition that must hold on the value for it to be included in the created
     *     histogram.
     * @return a newly created {@link Histogram} containing values and corresponding counts from
     *     this object, but only if they fulfill the predicate.
     */
    public Histogram threshold(DoublePredicate predicate) {

        Histogram out = new Histogram(maxValue);
        out.sumCount = 0;
        for (int bin = minValue; bin <= maxValue; bin++) {

            if (predicate.test(bin)) {
                int count = getCount(bin);
                out.set(bin, count);
                out.sumCount += count;
            }
        }

        return out;
    }

    /** A string representation of what's in the histogram. */
    @Override
    public String toString() {
        return concatenateForEachBin(value -> String.format("%d: %d%n", value, getCount(value)));
    }

    /**
     * The total count across values in the histogram.
     *
     * <p>This is pre-calculated, so calling this operation occurs no computational expense.
     *
     * @return the total count.
     */
    public long getTotalCount() {
        return sumCount;
    }

    /**
     * Creates a {@link Histogram} reusing the bins in the current histogram, but with an upper
     * limit on the total count.
     *
     * <p>If more total count exists than {@code maxCount}, values are removed in <b>ascending
     * order</b>, until the count is under the limit.
     *
     * @param maxCount the maximum allowable total-count for the extracted histogram.
     * @return a newly created {@link Histogram} either a copy of the existing (if the total count
     *     is less than {@code maxCount} or cropped as per above rules.
     */
    public Histogram cropRemoveSmallerValues(long maxCount) {

        Histogram out = new Histogram(maxValue);

        long remaining = maxCount;

        // We keep taking pixels from the histogram until we have reached our quota
        for (int bin = getMaxValue(); bin >= minValue; bin--) {

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
     * Like {@link #cropRemoveSmallerValues(long)} but larger values are removed rather than smaller
     * values if the total count is too high.
     *
     * @param maxCount the maximum allowable total-count for the extracted histogram.
     * @return a newly created {@link Histogram} either a copy of the existing (if the total count
     *     is less than {@code maxCount} or cropped as per above rules.
     */
    public Histogram cropRemoveLargerValues(long maxCount) {

        Histogram out = new Histogram(maxValue);

        long remaining = maxCount;

        // We keep taking pixels from the histogram until we have reached our quota
        for (int bin = minValue; bin <= getMaxValue(); bin++) {

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
     * Calculates the mean of the values in the distribution, if each value is raised to a power.
     *
     * <p>Specifically, it calculates the mean of {@code countFor(value) * value^power} across all
     * values.
     *
     * @param power the power to raise each value to.
     * @return the calculated mean.
     * @throws OperationFailedException if the histogram has no values.
     */
    public double mean(double power) throws OperationFailedException {
        checkAtLeastOneItemExists();
        return mean(power, 0.0);
    }

    /**
     * Like {@link #mean(double)} but a value may be subtracted before raising to a power.
     *
     * <p>Specifically, it calculates the mean of {@code countFor(value) * (value -
     * subtractValue)^power} across all values.
     *
     * @param power the power to raise each value to (after subtraction).
     * @param subtractValue a value subtracted before raising to a power.
     * @return the calculated mean.
     * @throws OperationFailedException if the histogram has no values.
     */
    public double mean(double power, double subtractValue) throws OperationFailedException {
        checkAtLeastOneItemExists();

        double sum = 0;

        for (int bin = minValue; bin <= maxValue; bin++) {
            double binSubtracted = (bin - subtractValue);
            sum += getCountAsLong(bin) * Math.pow(binSubtracted, power);
        }

        return sum / sumCount;
    }

    /**
     * Calls {@code consumer} for every value, <i>increasing</i> from min to max.
     *
     * @param consumer called for every bin.
     */
    public void iterateValues(BinConsumer consumer) {
        for (int bin = minValue; bin <= maxValue; bin++) {
            consumer.accept(bin, getCount(bin));
        }
    }

    /**
     * Calls {@code consumer} for every value until a limit, <i>increasing</i> from min to {@code
     * limit}.
     *
     * @param limit the maximum-value to consume (inclusive).
     * @param consumer called for every bin.
     */
    public void iterateValuesUntil(int limit, BinConsumer consumer) {
        for (int bin = minValue; bin <= limit; bin++) {
            consumer.accept(bin, getCount(bin));
        }
    }

    /** Calculates the sum of {@code function(value) * count)} across all values. */
    private long calculateSumHelper(LongUnaryOperator function) {

        long sum = 0;

        for (int bin = minValue; bin <= maxValue; bin++) {
            long add = getCountAsLong(bin) * function.applyAsLong(bin);
            sum += add;
        }

        return sum;
    }

    // The index in the array the value is stored at
    private int index(int value) {
        return value - minValue;
    }

    /** Assigns a count for a particular value. */
    private void set(int value, int countToAssign) {
        counts[index(value)] = countToAssign;
    }

    /** Increments the count for a particular value. */
    private void incrementCount(int value, int incrementBy) {
        counts[index(value)] += incrementBy;
    }

    private long getCountAsLong(int value) {
        return getCount(value);
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
     * @param destination the destination histogram.
     * @param bin the bin-value.
     * @param countForBin the count.
     * @param remaining the count remaining that can still be transferred.
     * @return an updated value for remaining after subtracting the transferred count.
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
        for (int bin = minValue; bin <= maxValue; bin++) {
            builder.append(stringForBin.apply(bin));
        }
        return builder.toString();
    }
}
