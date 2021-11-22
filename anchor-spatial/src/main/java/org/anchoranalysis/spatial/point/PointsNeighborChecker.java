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

package org.anchoranalysis.spatial.point;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Checks if two or more points fulfill certain <a
 * href="https://en.wikipedia.org/wiki/Neighborhood_operation">neighbor relations</a> to each other.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointsNeighborChecker {

    /**
     * Are all points in a list neighboring the next point in the list?
     *
     * @param list the list of points to check.
     * @return true when all points have a <i>big neighbor</i> relationship to the subsequent point.
     *     false when any point fails this condition.
     */
    public static boolean areAllPointsInBigNeighborhood(List<Point3i> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {

                Point3i first = list.get(i - 1);
                Point3i second = list.get(i);

                if (!arePointsNeighbors(first, second)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Do two points have a <i>big neighbor</i> relationship to each other?
     *
     * @param point1 the first point to check.
     * @param point2 the second point to check.
     * @return true iff the big neighbor relationship exists between the points.
     */
    public static boolean arePointsNeighbors(Point3i point1, Point3i point2) {
        if (point1.equals(point2)) {
            return false;
        }
        if (distanceSingleDim(point1.x(), point2.x())) {
            return false;
        }
        if (distanceSingleDim(point1.y(), point2.y())) {
            return false;
        }
        return (!distanceSingleDim(point1.z(), point2.z()));
    }

    private static boolean distanceSingleDim(int x, int y) {
        return Math.abs(x - y) > 1;
    }
}
