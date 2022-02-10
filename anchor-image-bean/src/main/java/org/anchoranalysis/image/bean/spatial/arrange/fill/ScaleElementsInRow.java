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

import com.github.davidmoten.guavamini.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Scales element in a particular row.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class ScaleElementsInRow {

    /**
     * Scales all elements in {@code row} so they have identical height, and exactly match the
     * desired row-width.
     *
     * <p>The note the order of elements in {@code row} is usually often during processing.
     *
     * @param row the elements in the row to scale.
     * @param targetWidth the desired target width for the entire row, i.e. desired sum of widths
     *     for each element.
     * @param aspectRatioSum the sum of aspect-ratios for all elements in {@code row}.
     */
    public static void scaleElementsToMatchRow(
            List<ExtentToArrange> row, int targetWidth, double aspectRatioSum) {
        int actualRowWidth = scaleElementsToApproximatelyMatch(row, targetWidth, aspectRatioSum);

        // As the above will always round, it's possible the final row-size will always be
        // smaller or greater than permitted.
        // To fix the gap, one pixel width is added/subtracted (as circumstances need) from a
        // certain
        // number of images, chosen by priority of the size of their rounding error, during prior
        // scaling.
        maybeAdjustElementWidth(row, targetWidth, actualRowWidth);
    }

    /**
     * Scales all elements in the row (preserving-aspect ratio approximately) so give a total width
     * similar to {@code targetWidth}.
     *
     * @param row the elements in the row to scale.
     * @param targetWidth the desired target width for the entire row, i.e. desired sum of widths
     *     for each element.
     * @param aspectRatioSum the sum of aspect-ratios for all elements in {@code row}.
     * @return the actual width for the entire row, after scaling.
     */
    private static int scaleElementsToApproximatelyMatch(
            List<ExtentToArrange> row, int targetWidth, double aspectRatioSum) {
        int actualRowWidth = 0;
        for (ExtentToArrange element : row) {
            actualRowWidth += element.scaleToMatchRow(aspectRatioSum, targetWidth);
        }
        return actualRowWidth;
    }

    /**
     * Adjusts the width of a certain number of elements, if necessary, so that the total width
     * exactly matches the target.
     *
     * @param row the row of elements, of which some elements may or may not be adjusted.
     * @param targetWidth the desired target width for the entire row, i.e. desired sum of widths
     *     for each element.
     * @param actualWidth the actual width for the entire row, i.e. existing sum of widths for each
     *     element.
     */
    private static void maybeAdjustElementWidth(
            List<ExtentToArrange> row, int targetWidth, int actualWidth) {
        int difference = targetWidth - actualWidth;
        assert Math.abs(difference) <= row.size();
        if (difference > 0) {
            // There's too little total width, so add 1 to certain elements' width.
            adjustElementWidth(row, difference, 1);
        } else if (difference < 0) {
            // There's too much actual total width, so subtract 1 from certain elements' width.
            adjustElementWidth(row, difference * -1, -1);
        }
    }

    /**
     * Adjusts the width of a certain number of elements in {@code row}.
     *
     * <p>Elements are chosen in priority of their rounding error, picking the elements with the
     * largest rounding-up errors to make smaller, and with the largest rounding-down errors to make
     * larger.
     *
     * <p>The note the order of elements in {@code row} is usually altered during processing.
     *
     * @param row the row of elements, of which some elements are adjusted.
     * @param numberElements how many elements to adjust. This must be {@code <= row.size()}.
     * @param adjustment how many pixels to add (if positive) or subtract (if negative) from the
     *     existing width.
     */
    private static void adjustElementWidth(
            List<ExtentToArrange> row, int numberElements, int adjustment) {
        Preconditions.checkArgument(numberElements <= row.size());

        // Sort the elements, in order of most desirable rounding-errors to correct.
        // The order depends on whether we are making a positive adjustment or negative adjustment,
        // to find respectively rounding-down or rounding-up errors.
        List<ExtentToArrange> sortedRow = new ArrayList<>(row);
        Collections.sort(
                sortedRow,
                Comparator.comparingDouble(
                        extent -> -1 * adjustment * extent.getScaleRoundingError()));

        Iterator<ExtentToArrange> iterator = sortedRow.iterator();
        while (numberElements > 0) {
            ExtentToArrange element = iterator.next();
            element.growWidth(adjustment);
            numberElements--;
        }
    }
}
