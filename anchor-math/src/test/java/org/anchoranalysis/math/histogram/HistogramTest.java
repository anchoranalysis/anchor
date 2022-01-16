/*-
 * #%L
 * anchor-math
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.math.arithmetic.Counter;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Histogram}.
 *
 * @author Owen Feehan
 */
class HistogramTest {

    private static final double TOLERANCE = 0.1;

    private Histogram histogram = createHistogram();

    @Test
    void testMean() throws OperationFailedException {
        assertEquals(170.0 / 16, histogram.mean());
    }

    @Test
    void testMeanPower() throws OperationFailedException {
        assertEquals(3.243, histogram.mean(0.5), TOLERANCE);
    }

    @Test
    void testMeanPowerSubtract() throws OperationFailedException {
        assertEquals(2.911, histogram.mean(0.5, 2), TOLERANCE);
    }

    @Test
    void testCalculateSum() {
        assertEquals(170, histogram.calculateSum());
    }

    @Test
    void testCalculateMinimum() throws OperationFailedException {
        assertEquals(5, histogram.calculateMinimum());
    }

    @Test
    void testCalculateMaximum() throws OperationFailedException {
        assertEquals(12, histogram.calculateMaximum());
    }

    @Test
    void testCalculateStandardDeviation() throws OperationFailedException {
        assertEquals(1.996, histogram.standardDeviation(), TOLERANCE);
    }

    @Test
    void testCalculateMode() throws OperationFailedException {
        assertEquals(12, histogram.calculateMode());
    }

    @Test
    void testCalculateSumCubes() throws OperationFailedException {
        assertEquals(21050, histogram.calculateSumCubes());
    }

    @Test
    void testCalculateSumSquares() throws OperationFailedException {
        assertEquals(1870, histogram.calculateSumSquares());
    }

    @Test
    void testCalculateCountMatching() throws OperationFailedException {
        assertEquals(15, histogram.countMatching(value -> value > 8));
    }

    @Test
    void testCalculateRemoveLargerValues() throws OperationFailedException {
        assertEquals(62, histogram.cropRemoveLargerValues(7).calculateSum());
    }

    @Test
    void testCalculateRemoveSmallerValues() throws OperationFailedException {
        assertEquals(36, histogram.cropRemoveSmallerValues(3).calculateSum());
    }

    @Test
    void testZeroValue() throws OperationFailedException {
        histogram.zeroValue(5);
        assertEquals(165, histogram.calculateSum());
    }

    @Test
    void testTransferCount() throws OperationFailedException {
        histogram.transferCount(5, 12);
        assertEquals(177, histogram.calculateSum());
    }

    @Test
    void testQuantile() throws OperationFailedException {
        assertEquals(9, histogram.quantile(0.3));
    }

    @Test
    void testSize() throws OperationFailedException {
        assertEquals(18, histogram.size());
    }

    @Test
    void testIterateValues() throws OperationFailedException {
        Counter counter = new Counter();
        histogram.iterateValues((bin, count) -> counter.incrementBy(bin * count));
        assertEquals(170, counter.getCount());
    }

    private static Histogram createHistogram() {
        Histogram histogram = new Histogram(3, 20);
        histogram.incrementValueBy(12, 10);
        histogram.incrementValueBy(5, 1);
        histogram.incrementValueBy(9, 5);
        return histogram;
    }
}
