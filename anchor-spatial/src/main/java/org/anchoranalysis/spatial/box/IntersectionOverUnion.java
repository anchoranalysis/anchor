/*-
 * #%L
 * anchor-plugin-opencv
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
package org.anchoranalysis.spatial.box;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Calculates the <a
 * href="https://www.pyimagesearch.com/2016/11/07/intersection-over-union-iou-for-object-detection/">intersection-over-union</a>
 * score for a pair of bounding-boxes.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IntersectionOverUnion {

    /**
     * Calculates the intersection-over-union score a pair of bounding-boxes.
     *
     * @param box1 the first box.
     * @param box2 the second box.
     * @return the area of intersection divided by the area of union, with 0 if there is no
     *     intersection or union.
     */
    public static double forBoxes(BoundingBox box1, BoundingBox box2) {
        Optional<BoundingBox> intersection = box1.intersection().with(box2);
        if (!intersection.isPresent()) {
            // If there's no intersection then the score is 0
            return 0.0;
        }

        long intersectionArea = intersection.get().extent().calculateVolume();

        // The total area is equal to the sum of both minus the intersection (which is otherwise
        // counted twice)
        long total =
                box2.extent().calculateVolume()
                        + box1.extent().calculateVolume()
                        - intersectionArea;

        return ((double) intersectionArea) / total;
    }
}
