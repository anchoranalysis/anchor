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

package org.anchoranalysis.image.core.outline.traverser.distance;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.outline.traverser.path.ContiguousVoxelPath;
import org.anchoranalysis.spatial.point.Point3i;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DistanceToContiguousPath {

    /** The maximum-distance of a point to the closest point on the path */
    public static DistanceIndex maxDistanceToClosestPoint(ContiguousVoxelPath path, Point3i point) {

        int indexMin = -1;
        int distanceMin = Integer.MAX_VALUE;

        // Finds the minimum distance to any of the paths
        for (int i = 0; i < path.size(); i++) {

            Point3i point2 = path.get(i);

            int distance = point.distanceMax(point2);
            if (distance < distanceMin) {
                distanceMin = distance;
                indexMin = i;
            }
        }

        return new DistanceIndex(distanceMin, indexMin);
    }

    public static int maxDistanceToHeadTail(ContiguousVoxelPath path, Point3i point) {
        return Math.min(point.distanceMax(path.head()), point.distanceMax(path.tail()));
    }
}
