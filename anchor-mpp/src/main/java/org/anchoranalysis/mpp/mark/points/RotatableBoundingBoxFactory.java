package org.anchoranalysis.mpp.mark.points;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.unchecked.IntToFloatFunction;
import org.anchoranalysis.spatial.orientation.Orientation2D;
import org.anchoranalysis.spatial.orientation.RotationMatrix;
import org.anchoranalysis.spatial.point.Point2d;
import org.anchoranalysis.spatial.point.Point2f;
import org.anchoranalysis.spatial.point.Point2i;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.PointConverter;

/**
 * Creates instances of {@link RotatableBoundingBox}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RotatableBoundingBoxFactory {

    /**
     * Builds a bounding-box from the 5-element floating-point vector returned from the EAST
     * algorithm.
     *
     * <p>The bounding-boxes are encoded in RBOX format, see the original EAST paper Zhou et al.
     * 2017.
     *
     * <p>A C++ example of calling EAST via OpenCV can be found on <a
     * href="https://github.com/opencv/opencv/blob/master/samples/dnn/text_detection.cpp">GitHub</a>.
     *
     * <p>The vector is ordered as follows:
     *
     * <ul>
     *   <li>Position 0: distance to top-boundary of box (y-min).
     *   <li>Position 1: distance to right-boundary of box (x-max).
     *   <li>Position 2: distance to bottom-boundary of box (y-max).
     *   <li>Position 3: distance to left-boundary of box (x-min).
     *   <li>Position 4: clockwise angle to rotate around the offset.
     * </ul>
     *
     * @param extractElement extracts an element from the vector, corresponding to indices 0-5
     *     inclusive.
     * @param anchorPoint the anchor point for the object, about which rotation occurs, and the
     *     corners of the bounding box are expressed relative to.
     * @return a mark encapsulating a rotatable bounding-box.
     */
    public static RotatableBoundingBox create(
            IntToFloatFunction extractElement, Point2i anchorPoint) {

        Point2f startUnrotated =
                new Point2f(
                        extractElement.applyAsFloat(3), // distance to left-boundary of box (x-min)
                        extractElement.applyAsFloat(0) // distance to top-boundary of box (y-min)
                        );

        Point2f endUnrotated =
                new Point2f(
                        extractElement.applyAsFloat(1), // distance to right-boundary of box (x-max)
                        extractElement.applyAsFloat(2) // distance to bottom-boundary of box (y-max)
                        );

        // Clockwise angle to rotate around the offset
        float angle = extractElement.applyAsFloat(4);

        return RotatableBoundingBoxFactory.createFromPoints(
                startUnrotated, endUnrotated, angle, anchorPoint);
    }

    /**
     * Creates a {@link RotatableBoundingBox}, rotated at a particular angle.
     *
     * @param startUnrotated the minimum corner point of the box, unrotated, relative to {@code
     *     midpointScaled}.
     * @param endUnrotated the maximum corner point of the box, unrotated, relative to {@code
     *     midpointScaled}.
     * @param angle the angle to rotate at.
     * @param anchorPoint the anchor point for the object, about which rotation occurs, and the
     *     corners of the bounding box are expressed relative to.
     * @return the created {@link RotatableBoundingBox}.
     */
    private static RotatableBoundingBox createFromPoints(
            Point2f startUnrotated, Point2f endUnrotated, float angle, Point2i anchorPoint) {

        // Make it counter-clockwise by multiplying by -1
        Orientation2D orientation = new Orientation2D(-1.0 * angle);

        Point3d endRotated = rotatedPointWithOffset(endUnrotated, orientation, anchorPoint);

        float width = startUnrotated.x() + endUnrotated.x();
        float height = startUnrotated.y() + endUnrotated.y();

        Point3d midpoint = midPointFor(width, height, angle, endRotated);

        return createMarkFor(midpoint, width, height, orientation);
    }

    private static Point3d rotatedPointWithOffset(
            Point2f unrotated, Orientation2D orientation, Point2i anchorPoint) {

        RotationMatrix rotMatrix = orientation.deriveRotationMatrix();

        Point3d rotated = rotMatrix.rotatedPoint(convert3d(unrotated));
        rotated.add(PointConverter.doubleFromInt(anchorPoint));
        return rotated;
    }

    private static RotatableBoundingBox createMarkFor(
            Point3d midpoint, float width, float height, Orientation2D orientation) {
        RotatableBoundingBox mark = new RotatableBoundingBox();
        mark.setPosition(midpoint);
        mark.update(
                new Point2d(-width / 2, -height / 2),
                new Point2d(width / 2, height / 2),
                orientation);
        return mark;
    }

    private static Point3d midPointFor(float width, float height, float angle, Point3d endRotated) {

        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);

        Point3d heightVector = new Point3d(-sinA * height, -cosA * height, 0);
        Point3d widthVector = new Point3d(-cosA * width, sinA * width, 0);

        // Average point between the two
        Point3d avg = meanPoint(heightVector, widthVector);
        avg.add(endRotated);
        return avg;
    }

    private static Point3d meanPoint(Point3d point1, Point3d point2) {
        return new Point3d((point1.x() + point2.x()) / 2, (point1.y() + point2.y()) / 2, 0);
    }

    public static Point3d convert3d(Point2f p) {
        return new Point3d(p.x(), p.y(), 0);
    }
}
