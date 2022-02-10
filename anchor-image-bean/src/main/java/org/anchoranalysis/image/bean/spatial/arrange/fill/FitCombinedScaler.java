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

import java.util.Collection;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Scales the size of images to match the target image, preserving aspect-ratio.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FitCombinedScaler {

    /**
     * When {@code permitPartianFinalRow==true} this is the maximum permitted difference in
     * aspect-ratio in the final row, before which a reduction occurs.
     */
    private static final double MAXIMUM_PERMITTED_DIFFERENCE_ASPECT_RATIO_FINAL_ROW = 1.2;

    /**
     * Calculates the size of the combined image to use for the montage, and scales the associated
     * {@link Extent} each to match.
     *
     * <p>The state of each {@code ExtentToArrange} is changed to assign a new scaled size to match.
     *
     * @param partitions the {@link Extent}s partitioned into rows.
     * @param permitPartialFinalRow allows the final row to only be partially filled, so as to keep
     *     its height similar to other rows.
     * @param width the desired width of the combiend image.
     * @return the size of the combined image, containing all rows and columns.
     */
    public static Extent scaleToWidth(
            List<List<ExtentToArrange>> partitions, boolean permitPartialFinalRow, int width) {

        // Scale each image to match the row width, preserving-aspect ratio
        scaleImagesToMatchRow(partitions, width, permitPartialFinalRow);

        // Size of the combined image
        int height = sumExtractedInt(partitions, row -> row.get(0).getExtent().y());
        return new Extent(width, height, 1);
    }

    /**
     * Scales each {@link Extent} in each row, so that they match the row-width, preserving the
     * aspect-ratio.
     */
    private static void scaleImagesToMatchRow(
            List<List<ExtentToArrange>> partitions, int rowWidth, boolean permitPartialFinalRow) {
        // Tracks the sum of each row so far, so the average aspect-ratio can be be calculated
        double sumOfSums = 0.0;

        int lastIndex = partitions.size() - 1;
        for (int index = 0; index < partitions.size(); index++) {
            List<ExtentToArrange> row = partitions.get(index);

            double aspectRatioSum = sumExtractedDouble(row, ExtentToArrange::getAspectRatio);

            // Special treatment for final row
            if (permitPartialFinalRow && index == lastIndex && partitions.size() > 1) {
                // Adjust the row-width to be a fraction of its usual size, so as to give a
                // row-height similar to the others

                // It is not possible for index to be 0, as the lastIndex will never be 0 when the
                // partition-size must be >= 2
                rowWidth =
                        maybeAdjustTargetRowWidth(
                                aspectRatioSum, sumOfSums / index, rowWidth); // NOSONAR
            }

            sumOfSums += aspectRatioSum;

            // Scale the elements in the row so they have identical height, and exactly match the
            // target rowWidth
            ScaleElementsInRow.scaleElementsToMatchRow(row, rowWidth, aspectRatioSum);
        }
    }

    /**
     * Adjusts the row-width, if needed, in the final row, to make the row height more similar to
     * the other rows.
     */
    private static int maybeAdjustTargetRowWidth(
            double currentHeight, double meanHeight, int rowWidth) {
        if (currentHeight < (MAXIMUM_PERMITTED_DIFFERENCE_ASPECT_RATIO_FINAL_ROW * meanHeight)) {
            return (int) (rowWidth * (currentHeight / meanHeight));
        } else {
            return rowWidth;
        }
    }

    /** Sums an {@code int} value extracted from each element. */
    private static <T> int sumExtractedInt(
            Collection<T> collection, ToIntFunction<T> extractValue) {
        return collection.stream().mapToInt(extractValue::applyAsInt).sum();
    }

    /** Sums an {@code double} value extracted from each element. */
    private static <T> double sumExtractedDouble(
            Collection<T> collection, ToDoubleFunction<T> extractValue) {
        return collection.stream().mapToDouble(extractValue::applyAsDouble).sum();
    }
}
