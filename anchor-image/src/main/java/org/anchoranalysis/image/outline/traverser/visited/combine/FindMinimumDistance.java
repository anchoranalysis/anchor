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

package org.anchoranalysis.image.outline.traverser.visited.combine;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.path.ContiguousVoxelPath;
import org.anchoranalysis.image.outline.traverser.path.DistanceIndex;
import org.anchoranalysis.image.outline.traverser.path.DistanceToContiguousPath;

/**
 * TODO naming is too complicated in functions with lots of min/max
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindMinimumDistance {

    /** The maximum-distance of a point to the closest contiguous-path */
    public static int minDistanceMaxToHeadTail(Point3i point, List<ContiguousVoxelPath> paths) {

        // Finds the minimum distance to any of the paths
        return paths.stream()
                .mapToInt(path -> DistanceToContiguousPath.maxDistanceToHeadTail(path, point))
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    /** The index of whichever of the ContiguousPixelPaths has the nearest point */
    static DistanceIndexTwice indexDistanceMaxToClosestPoint(
            Point3i point, List<ContiguousVoxelPath> paths, int avoidIndex) {

        // The -1 is arbitrary
        DistanceIndex minDistance = new DistanceIndex(Integer.MAX_VALUE, -1);
        int indexWithMin = -1;

        // Finds the minimum distance to any of the paths
        for (int i = 0; i < paths.size(); i++) {

            if (i == avoidIndex) {
                continue;
            }

            ContiguousVoxelPath path = paths.get(i);

            DistanceIndex distance =
                    DistanceToContiguousPath.maxDistanceToClosestPoint(path, point);
            if (distance.getDistance() < minDistance.getDistance()) {
                minDistance = distance;
                indexWithMin = i;
            }
        }

        return new DistanceIndexTwice(
                minDistance.getDistance(), indexWithMin, minDistance.getIndex());
    }
}
