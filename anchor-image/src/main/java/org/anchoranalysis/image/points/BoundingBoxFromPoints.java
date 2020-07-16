/* (C)2020 */
package org.anchoranalysis.image.points;

import java.util.List;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;

/**
 * Creates a bounding-box from one or more points
 *
 * @author Owen Feehan
 */
public class BoundingBoxFromPoints {

    private BoundingBoxFromPoints() {}

    /**
     * Create from a list of points
     *
     * @param points the list
     * @return a bounding-box minimally spanning all points
     * @throws OperationFailedException if there are zero points
     */
    public static BoundingBox forList(List<Point3i> points) throws OperationFailedException {
        if (points.isEmpty()) {
            throw new OperationFailedException("Points list must contain at least one item");
        }

        PointRange range = new PointRange();
        points.forEach(range::add);

        return range.deriveBoundingBox();
    }

    /**
     * Creates a bounding-box for two unordered points.
     *
     * <p>By unordered, it means that no one point must have a value higher than another
     *
     * @param point1 first-point (arbitrary order)
     * @param point2 second-point (arbitrary order)
     * @return a bounding-box minally spanning the two points
     */
    public static BoundingBox forTwoPoints(Point3d point1, Point3d point2) {
        Point3d min = calcMin(point1, point2);
        Point3d max = calcMax(point1, point2);
        return new BoundingBox(min, max);
    }

    private static Point3d calcMin(Point3d point1, Point3d point2) {
        Point3d point = new Point3d();
        point.setX(Math.min(point1.getX(), point2.getX()));
        point.setY(Math.min(point1.getY(), point2.getY()));
        point.setZ(Math.min(point1.getZ(), point2.getZ()));
        return point;
    }

    private static Point3d calcMax(Point3d point1, Point3d point2) {
        Point3d point = new Point3d();
        point.setX(Math.max(point1.getX(), point2.getX()));
        point.setY(Math.max(point1.getY(), point2.getY()));
        point.setZ(Math.max(point1.getZ(), point2.getZ()));
        return point;
    }
}
