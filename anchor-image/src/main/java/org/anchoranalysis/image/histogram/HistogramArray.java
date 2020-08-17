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
import java.util.function.LongUnaryOperator;
import lombok.Getter;
import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.relation.RelationToValue;
import org.apache.commons.lang.ArrayUtils;

public class HistogramArray implements Histogram {

    private int[] arr;

    /** Minimum bin-value (by default 0) inclusive */
    @Getter private int minBin;

    /** Maximum bin-value inclusive */
    @Getter private int maxBin;

    private long countTotal = 0;

    public HistogramArray(int maxBinVal) {
        this(0, maxBinVal);
    }

    public HistogramArray(int minBinVal, int maxBinVal) {
        arr = new int[maxBinVal - minBinVal + 1];
        countTotal = 0;
        this.maxBin = maxBinVal;
        this.minBin = minBinVal;
    }

    @Override
    public HistogramArray duplicate() {
        HistogramArray out = new HistogramArray(minBin, maxBin);
        out.arr = ArrayUtils.clone(arr);
        out.countTotal = countTotal;
        return out;
    }

    public void reset() {
        countTotal = 0;
        for (int i = minBin; i <= maxBin; i++) {
            arrSet(i, 0);
        }
    }

    public void zeroValue(int val) {
        int indx = index(val);
        countTotal -= arr[indx];
        arr[indx] = 0;
    }

    public void transferValue(int srcVal, int destVal) {
        int srcIndx = index(srcVal);

        arrIncr(destVal, arr[srcIndx]);
        arr[srcIndx] = 0;
    }

    public void incrementValue(int val) {
        arrIncr(val, 1);
        countTotal++;
    }

    public void incrValueBy(int val, int increase) {
        arrIncr(val, increase);
        countTotal += increase;
    }

    /**
     * long version of incrValBy
     *
     * @throws ArithmeticException if incresae cannot be converted to an int safely
     */
    public void incrValueBy(int val, long increase) {
        incrValueBy(val, toIntExact(increase));
    }

    @Override
    public void removeBelowThreshold(int threshold) {
        for (int i = getMinBin(); i < threshold; i++) {
            zeroValue(i);
        }
        // Now chop off the unneeded values and set a new minimum
        chopArrBefore(index(threshold));
        this.minBin = threshold;
    }

    public boolean isEmpty() {
        return countTotal == 0;
    }

    public int getCount(int val) {
        return arrGet(val);
    }

    /** The number of items in the histogram (maxBinVal - minBinVal + 1) */
    public int size() {
        return arr.length;
    }

    public void addHistogram(Histogram other) throws OperationFailedException {
        if (this.getMaxBin() != other.getMaxBin()) {
            throw new OperationFailedException(
                    "Cannot add histograms with different max-bin-values");
        }
        if (this.getMinBin() != other.getMinBin()) {
            throw new OperationFailedException(
                    "Cannot add histograms with different min-bin-values");
        }

        for (int i = getMinBin(); i <= getMaxBin(); i++) {
            int otherCnt = other.getCount(i);
            arrIncr(i, otherCnt);
            countTotal += otherCnt;
        }
    }

    public double mean() throws OperationFailedException {

        checkAtLeastOneItemExists();

        long sum = 0;

        for (int i = minBin; i <= maxBin; i++) {
            sum += arrGetLong(i) * i;
        }

        return ((double) sum) / countTotal;
    }

    public double meanGreaterEqualTo(int val) throws OperationFailedException {
        checkAtLeastOneItemExists();

        long sum = 0;
        long count = 0;

        int startMin = Math.max(val, minBin);

        for (int i = startMin; i <= maxBin; i++) {
            long num = arrGetLong(i);
            sum += i * num;
            count += num;
        }

        return ((double) sum) / count; // NOSONAR
    }

    public double meanNonZero() throws OperationFailedException {
        checkAtLeastOneItemExists();

        long sum = 0;

        for (int i = minBin; i <= maxBin; i++) {
            sum += i * arrGetLong(i);
        }

        return ((double) sum) / (countTotal - getCount(0));
    }

    public long sumNonZero() {
        return calculateSum() - getCount(0);
    }

    public void scaleBy(double factor) {

        int sum = 0;

        for (int i = minBin; i <= maxBin; i++) {

            int indx = index(i);

            int valNew = (int) Math.round(factor * arr[indx]);
            sum += valNew;
            arr[indx] = valNew;
        }

        countTotal = sum;
    }

    @Override
    public int quantile(double quantile) throws OperationFailedException {
        checkAtLeastOneItemExists();

        double pos = quantile * countTotal;

        long sum = 0;
        for (int i = minBin; i <= maxBin; i++) {
            sum += arrGet(i);

            if (sum > pos) {
                return i;
            }
        }
        return calculateMaximum();
    }

    @Override
    public int quantileAboveZero(double quantile) throws OperationFailedException {
        checkAtLeastOneItemExists();

        long cntMinusZero = countTotal - getCount(0);

        double pos = quantile * cntMinusZero;

        int startMin = Math.max(1, minBin);

        long sum = 0;
        for (int i = startMin; i <= maxBin; i++) {
            sum += arrGet(i);

            if (sum > pos) {
                return i;
            }
        }
        return calculateMaximum();
    }

    public boolean hasAboveZero() {

        int startMin = Math.max(1, minBin);

        for (int i = startMin; i <= maxBin; i++) {
            if (arrGet(i) > 0) {
                return true;
            }
        }
        return false;
    }

    public double percentGreaterEqualTo(int intensity) {

        int startMin = Math.max(intensity, minBin);

        long sum = 0;
        for (int i = startMin; i <= maxBin; i++) {
            sum += arrGet(i);
        }

        return ((double) sum) / countTotal;
    }

    @Override
    public int calculateMode() throws OperationFailedException {
        checkAtLeastOneItemExists();
        return calculateMode(0);
    }

    public int calculateMode(int startVal) throws OperationFailedException {
        checkAtLeastOneItemExists();

        int maxIndex = -1;
        int maxValue = -1;

        for (int i = startVal; i <= maxBin; i++) {
            int val = arrGet(i);
            if (val > maxValue) {
                maxValue = val;
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public int calculateMaximum() throws OperationFailedException {
        checkAtLeastOneItemExists();

        for (int i = maxBin; i >= minBin; i--) {
            if (arrGet(i) > 0) {
                return i;
            }
        }

        throw new AnchorImpossibleSituationException();
    }

    public int calculateMinimum() throws OperationFailedException {
        checkAtLeastOneItemExists();

        for (int i = minBin; i <= maxBin; i++) {
            if (arrGet(i) > 0) {
                return i;
            }
        }

        throw new AnchorImpossibleSituationException();
    }

    @Override
    public long calculateSum() {
        return calculateSumHelper(i -> i);
    }

    @Override
    public long calculateSumSquares() {
        return calculateSumHelper(i -> i * i);
    }

    @Override
    public long calculateSumCubes() {
        return calculateSumHelper(i -> i * i * i);
    }

    public int calculateCountNonZero() {

        int num = 0;

        for (int i = minBin; i <= maxBin; i++) {
            if (arrGet(i) > 0) {
                num++;
            }
        }

        return num;
    }

    @Override
    public double standardDeviation() throws OperationFailedException {
        checkAtLeastOneItemExists();
        return Math.sqrt(variance());
    }

    @Override
    public long countThreshold(RelationToThreshold relationToThreshold) {

        RelationToValue relation = relationToThreshold.relation();
        double threshold = relationToThreshold.threshold();

        long sum = 0;

        for (int i = minBin; i <= maxBin; i++) {

            if (relation.isRelationToValueTrue(i, threshold)) {
                sum += arrGetLong(i);
            }
        }

        return sum;
    }

    // Thresholds (generates a new histogram, existing object is unchanged)
    @Override
    public Histogram threshold(RelationToThreshold relationToThreshold) {

        RelationToValue relation = relationToThreshold.relation();
        double threshold = relationToThreshold.threshold();

        HistogramArray out = new HistogramArray(maxBin);
        out.countTotal = 0;
        for (int i = minBin; i <= maxBin; i++) {

            if (relation.isRelationToValueTrue(i, threshold)) {
                int s = arrGet(i);
                out.arrSet(i, s);
                out.countTotal += s;
            }
        }

        return out;
    }

    // Doesn't show zero values
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int t = minBin; t <= maxBin; t++) {
            int cnt = arrGet(t);
            if (cnt != 0) {
                sb.append(String.format("%d: %d%n", t, cnt));
            }
        }
        return sb.toString();
    }

    // Includes zero values
    @Override
    public String csvString() {
        StringBuilder sb = new StringBuilder();
        for (int t = minBin; t <= maxBin; t++) {
            sb.append(String.format("%d, %d%n", t, arrGet(t)));
        }
        return sb.toString();
    }

    @Override
    public long getTotalCount() {
        return countTotal;
    }

    public Histogram extractValuesFromRight(long numberValues) {

        Histogram out = new HistogramArray(getMaxBin());

        long remaining = numberValues;

        // We keep taking pixels from the histogram until we have reached our quota
        for (int b = getMaxBin(); b >= getMinBin(); b--) {

            int bVal = getCount(b);

            // Skip if there's nothing there
            if (bVal != 0) {

                remaining = extractBin(out, b, bVal, remaining);

                if (remaining == 0) {
                    break;
                }
            }
        }
        return out;
    }

    public Histogram extractValuesFromLeft(long numberValues) {

        Histogram out = new HistogramArray(getMaxBin());

        long remaining = numberValues;

        // We keep taking pixels from the histogram until we have reached our quota
        for (int b = getMinBin(); b <= getMaxBin(); b++) {

            int bVal = getCount(b);

            // Skip if there's nothing there
            if (bVal != 0) {
                remaining = extractBin(out, b, bVal, remaining);

                if (remaining == 0) {
                    break;
                }
            }
        }
        return out;
    }

    @Override
    public double mean(double power) throws OperationFailedException {
        checkAtLeastOneItemExists();
        return mean(power, 0.0);
    }

    @Override
    public double mean(double power, double subtractVal) throws OperationFailedException {
        checkAtLeastOneItemExists();

        double sum = 0;

        for (int i = minBin; i <= maxBin; i++) {
            double iSub = (i - subtractVal);
            sum += arrGetLong(i) * Math.pow(iSub, power);
        }

        return sum / countTotal;
    }

    private long calculateSumHelper(LongUnaryOperator func) {

        long sum = 0;

        for (int i = minBin; i <= maxBin; i++) {
            long add = arrGetLong(i) * func.applyAsLong((long) i);
            sum += add;
        }

        return sum;
    }

    // The index in the array the value is stored at
    private int index(int val) {
        return val - minBin;
    }

    // Sets a count for a value
    private void arrSet(int val, int cntToSet) {
        arr[index(val)] = cntToSet;
    }

    // Sets a count for a value
    private void arrIncr(int val, int incrCnt) {
        arr[index(val)] += incrCnt;
    }

    private int arrGet(int val) {
        return arr[index(val)];
    }

    private long arrGetLong(int val) {
        return (long) arrGet(val);
    }

    private void chopArrBefore(int indx) {
        arr = Arrays.copyOfRange(arr, indx, arr.length);
    }

    private void checkAtLeastOneItemExists() throws OperationFailedException {
        if (isEmpty()) {
            throw new OperationFailedException(
                    "There are no items in the histogram so this operation cannot occur");
        }
    }

    /**
     * Places a particular bin in a destination histogram.
     * <p>
     * Either the whole bin is transferred or only some of the bin so that {@code remaining >= 0).
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
            destination.incrValueBy(bin, countForBin);
            return remaining - countForBin;
        } else {
            // Otherwise partially transfer the bin
            destination.incrValueBy(bin, remaining);
            return 0;
        }
    }
}
