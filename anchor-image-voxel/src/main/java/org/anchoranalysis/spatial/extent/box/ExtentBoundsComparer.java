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

package org.anchoranalysis.spatial.extent.box;

import java.util.Optional;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Helper routines for calculating the union/intersection of two bounding-boxes along a particular
 * axis.
 */
@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class ExtentBoundsComparer {

    private final int min;
    private final int max;

    public int extent() {
        return max - min + 1;
    }

    public static ExtentBoundsComparer createMax(
            ReadableTuple3i min1,
            ReadableTuple3i min2,
            ReadableTuple3i max1,
            ReadableTuple3i max2,
            ToIntFunction<ReadableTuple3i> extract) {
        return calculate(
                        extract.applyAsInt(min1),
                        extract.applyAsInt(min2),
                        extract.applyAsInt(max1),
                        extract.applyAsInt(max2),
                        Math::min,
                        Math::max)
                .orElseThrow(AnchorImpossibleSituationException::new);
    }

    public static Optional<ExtentBoundsComparer> createMin(
            ReadableTuple3i min1,
            ReadableTuple3i min2,
            ReadableTuple3i max1,
            ReadableTuple3i max2,
            ToIntFunction<ReadableTuple3i> extract) {
        return calculate(
                extract.applyAsInt(min1),
                extract.applyAsInt(min2),
                extract.applyAsInt(max1),
                extract.applyAsInt(max2),
                Math::max,
                Math::min);
    }

    private static Optional<ExtentBoundsComparer> calculate(
            int min1,
            int min2,
            int max1,
            int max2,
            IntBinaryOperator minOperator,
            IntBinaryOperator maxOperator) {
        int minNew = minOperator.applyAsInt(min1, min2);
        int maxNew = maxOperator.applyAsInt(max1, max2);
        if (minNew <= maxNew) {
            return Optional.of(new ExtentBoundsComparer(minNew, maxNew));
        } else {
            return Optional.empty();
        }
    }
}
