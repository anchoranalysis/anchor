package org.anchoranalysis.image.object;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.iterator.IterateVoxels;

/**
 * Creates object-masks that are circles in different ways
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CircleObjectFixture {

    /**
     * Creates several successive circles whose center is incrementally shifted
     *
     * @param numberCircles how many circles to create
     * @param startCenter the center of the first circle
     * @param radius the radius for all circles
     * @param centerShift shifts the center by this amount for each successive circle
     * @param radiusShift increases the radius by this amount for each successive circle
     * @return a collection of circular-objects
     */
    public static ObjectCollection successiveCircles(
            int numberCircles,
            Point2d startCenter,
            int radius,
            Point2d centerShift,
            double radiusShift) {

        ObjectCollection out = new ObjectCollection();

        Point2d currentCenter = startCenter;
        double currentRadius = radius;

        for (int i = 0; i < numberCircles; i++) {
            out.add(circleAt(currentCenter, (int) currentRadius));

            currentCenter.add(centerShift);
            currentRadius += radiusShift;
        }

        return out;
    }

    /**
     * Creates a single circular mask at a particular center point and radius
     *
     * @param center center-point
     * @param radius the radius
     * @return a circular object-mask as above with a minimally fitting bounding-box around it
     */
    public static ObjectMask circleAt(Point2d center, int radius) {
        Point3d center3d = PointConverter.convertTo3d(center);

        BoundingBox box = boundingBoxForCircle(center3d, radius);

        double radiusSquared = Math.pow(radius, 2.0);

        ObjectMask object = new ObjectMask(box);
        VoxelsAssigner assigner = object.assignOn();
        IterateVoxels.callEachPoint(
                box, point -> center3d.distanceSquared(point) <= radiusSquared, assigner::toVoxel);
        return object;
    }

    private static BoundingBox boundingBoxForCircle(Point3d center3d, int radius) {

        Point3i centerCeil = PointConverter.intFromDoubleCeil(center3d);
        Point3i centerFloor = PointConverter.intFromDoubleFloor(center3d);
        Point3i radii = new Point3i(radius, radius, 0);

        Point3i cornerMin = Point3i.immutableSubtract(centerFloor, radii);
        Point3i cornerMax = Point3i.immutableAdd(centerCeil, radii);
        return new BoundingBox(cornerMin, cornerMax);
    }
}
