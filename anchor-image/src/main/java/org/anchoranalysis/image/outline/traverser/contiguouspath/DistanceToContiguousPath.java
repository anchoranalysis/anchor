/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.contiguouspath;

import org.anchoranalysis.core.geometry.Point3i;

public class DistanceToContiguousPath {

    private DistanceToContiguousPath() {}

    /** The maximum-distance of a point to the closest point on the path */
    public static DistanceIndex maxDistanceToClosestPoint(ContiguousPixelPath path, Point3i point) {

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

    public static int maxDistanceToHeadTail(ContiguousPixelPath path, Point3i point) {
        return Math.min(point.distanceMax(path.head()), point.distanceMax(path.tail()));
    }
}
