/*-
 * #%L
 * anchor-core
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
/* (C)2020 */
package org.anchoranalysis.core.random;

import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import java.util.List;

/**
 * Generates random numbers in accordance to particular distributions.
 *
 * @author Owen Feehan
 */
public interface RandomNumberGenerator {

    /** Samples from a uniformly-distributed range between 0.0 (exclusive) and 1.0 (exclusive) */
    double sampleDoubleZeroAndOne();

    /** Generates a Poisson random variable with particular parameterization */
    Poisson generatePoisson(double param);

    /** Generates a Gaussian (Normal) random variable with particular parameterization */
    Normal generateNormal(double mean, double standardDeviation);

    /** Samples uniformly an item from a list */
    default <T> T sampleFromList(List<T> list) {
        return list.get(sampleIntFromRange(list.size()));
    }

    /**
     * Generates a uniformly random discrete number within a range from 0 (inclusive) to {@code
     * endExclusive}
     */
    default int sampleIntFromRange(int endExclusive) {
        return (int) (sampleDoubleZeroAndOne() * endExclusive);
    }

    /**
     * Generates a uniformly random discrete number within a range from 0 (inclusive) to {@code
     * endExclusive}
     */
    default long sampleLongFromRange(long endExclusive) {
        return (long) (sampleDoubleZeroAndOne() * endExclusive);
    }

    /**
     * Generates a uniformly random floating-point number within a range from 0 (exclusive) to
     * {@code endExclusive}
     */
    default double sampleDoubleFromRange(double endExclusive) {
        return sampleDoubleZeroAndOne() * endExclusive;
    }

    /** Samples from a uniformly random discrete number within a range */
    default int sampleIntFromRange(int startInclusive, int endExclusive) {
        return sampleIntFromRange(endExclusive - startInclusive) + startInclusive;
    }

    /** Generates a uniformly random floating-point number within a range */
    default double sampleDoubleFromRange(double startExclusive, double endExclusive) {
        return sampleDoubleFromRange(endExclusive - startExclusive) + startExclusive;
    }

    /**
     * Generates a uniformly random floating-point number centered around 0 ranging from
     * [-halfWidth,+halfWidth] (both exclusive)
     *
     * @param halfWidth half the total width of the distribution (i.e. the width of one side)
     * @return the sampled-number
     */
    default double sampleDoubleFromZeroCenteredRange(double halfWidth) {
        return sampleDoubleFromRange(halfWidth * 2) - halfWidth;
    }
}
