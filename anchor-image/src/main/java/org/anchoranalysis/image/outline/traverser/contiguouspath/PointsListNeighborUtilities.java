/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.contiguouspath;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointsListNeighborUtilities {

    /** Are all points in a list neighboring the next point in the list? */
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

    /** Makes sure no successive neighbors are equal */
    public static boolean areNeighborsDistinct(List<Point3i> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {

                Point3i first = list.get(i - 1);
                Point3i second = list.get(i);

                if (first.equals(second)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean arePointsNeighbors(Point3i point1, Point3i point2) {
        if (point1.equals(point2)) {
            return false;
        }
        if (distanceSingleDim(point1.getX(), point2.getX())) {
            return false;
        }
        if (distanceSingleDim(point1.getY(), point2.getY())) {
            return false;
        }
        return (!distanceSingleDim(point1.getZ(), point2.getZ()));
    }

    private static boolean distanceSingleDim(int x, int y) {
        return Math.abs(x - y) > 1;
    }
}
