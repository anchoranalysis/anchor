/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic;

import static org.anchoranalysis.anchor.mpp.mark.conic.TensorUtilities.*;

import cern.colt.matrix.DoubleMatrix1D;
import org.anchoranalysis.anchor.mpp.points.PointClipper;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;

/** Functions to calculate a bounding-box for a point surrounded by some form of radii */
public class BoundingBoxCalculator {

    private BoundingBoxCalculator() {}

    /**
     * Calculates a bounding box for a point with a scalar radius in all dimensions
     *
     * @param pos center-point
     * @param radius size of scalar radius
     * @param do3D 3 dimensions (XYZ) iff TRUE, otherwise 2 dimensions (XZ)
     * @param bndScene bounds on the scene, used to clip the bounding-box
     * @return
     */
    public static BoundingBox bboxFromBounds(
            Point3d pos, double radius, boolean do3D, ImageDimensions bndScene) {
        DoubleMatrix1D radiusBBoxMatrix = threeElementMatrix(radius, radius, radius);
        return bboxFromBounds(pos, radiusBBoxMatrix, do3D, bndScene);
    }

    /**
     * Calculates a bounding box for a point with varying radii in each dimension (that have already
     * been resolved into a matrix)
     *
     * @param pos center-point
     * @param radiiMatrix a matrix with resolved-radii for each dimension
     * @param do3D 3 dimensions (XYZ) iff TRUE, otherwise 2 dimensions (XZ)
     * @param bndScene bounds on the scene, used to clip the bounding-box
     * @return
     */
    public static BoundingBox bboxFromBounds(
            Point3d pos, DoubleMatrix1D radiiMatrix, boolean do3D, ImageDimensions bndScene) {
        Point3i minPt = subTwoPointsClip(pos, radiiMatrix, do3D, bndScene);
        Point3i maxPt = addTwoPointsClip(pos, radiiMatrix, do3D, bndScene);

        assert maxPt.getX() >= minPt.getX();
        assert maxPt.getY() >= minPt.getY();
        assert maxPt.getZ() >= minPt.getZ();

        return new BoundingBox(minPt, maxPt);
    }

    private static Point3i subTwoPointsClip(
            Point3d point1, DoubleMatrix1D point2, boolean do3D, ImageDimensions sd) {
        Point3i point = subTwoPoints(point1, point2, do3D);
        return PointClipper.clip(point, sd);
    }

    private static Point3i addTwoPointsClip(
            Point3d point1, DoubleMatrix1D point2, boolean do3D, ImageDimensions sd) {
        Point3i point = addTwoPoints(point1, point2, do3D);
        return PointClipper.clip(point, sd);
    }

    /**
     * Creates a new point that is the subtraction of one point from another (a Point3d minus a
     * DoubleMatrix1D)
     */
    private static Point3i subTwoPoints(Point3d point1, DoubleMatrix1D point2, boolean do3D) {
        Point3i out = new Point3i();
        out.setX(floorDiff(point1.getX(), point2.get(0)));
        out.setY(floorDiff(point1.getY(), point2.get(1)));
        out.setZ(do3D ? floorDiff(point1.getZ(), point2.get(2)) : 0);
        return out;
    }

    /**
     * Creates a new point that is the sum of two existing point (one a Point3d, and one a
     * DoubleMatrix1D)
     */
    private static Point3i addTwoPoints(Point3d point1, DoubleMatrix1D point2, boolean do3D) {
        Point3i out = new Point3i();
        out.setX(ceilSum(point1.getX(), point2.get(0)));
        out.setY(ceilSum(point1.getY(), point2.get(1)));
        out.setZ(do3D ? ceilSum(point1.getZ(), point2.get(2)) : 0);
        return out;
    }

    private static int floorDiff(double x, double y) {
        return (int) Math.floor(x - y);
    }

    private static int ceilSum(double x, double y) {
        return (int) Math.ceil(x + y);
    }
}
