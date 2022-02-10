/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.bean.spatial.arrange.fill;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Calculates the combined width for them ontage.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CombinedWidthCalculator {

    /**
     * Calculates the width to use for the combined-image.
     *
     * <p>This will be either from {@code width} or calculated using the {@code widthRatio}.
     *
     * <p>If both are specified, the maximum is taken.
     *
     * @param partitions how the images are arranged in the table.
     * @param width a specific width if one when assigned, or 0 if unassigned.
     * @param widthRatio a portion of natural row-width when assigned, or 0.0 if unassigned.
     * @return the calculated combined width to use for partitions.
     */
    public static int calculate(
            List<List<ExtentToArrange>> partitions, int width, double widthRatio) {
        if (width > 0) {
            if (widthRatio > 0.0) {
                return Math.max(width, calculateFromRatio(partitions, widthRatio));
            } else {
                return width;
            }
        } else {
            // Then widthRatio must be assigned
            return calculateFromRatio(partitions, widthRatio);
        }
    }

    /** Calculates a target-width from the {@code widthRatio}. */
    private static int calculateFromRatio(
            List<List<ExtentToArrange>> partitions, double widthRatio) {
        return (int) Math.round(widthRatio * averageRowWidth(partitions));
    }

    /** Calculates the average row-width across all partitions. */
    private static double averageRowWidth(List<List<ExtentToArrange>> partitions) {
        return partitions.stream()
                .mapToInt(CombinedWidthCalculator::widthOfRow)
                .average()
                .getAsDouble(); // NOSONAR
    }

    /** Calculates the width of a particular row in the partition. */
    private static int widthOfRow(List<ExtentToArrange> extents) {
        return extents.stream().mapToInt(ExtentToArrange::width).sum();
    }
}
