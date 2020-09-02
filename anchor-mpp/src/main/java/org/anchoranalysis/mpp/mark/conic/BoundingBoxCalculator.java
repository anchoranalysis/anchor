/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.mark.conic;

import static org.anchoranalysis.mpp.mark.conic.TensorUtilities.*;

import cern.colt.matrix.DoubleMatrix1D;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.mpp.points.PointClipper;

/** Functions to calculate a bounding-box for a point surrounded by some form of radii */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoundingBoxCalculator {

    /**
     * Calculates a bounding box for a point with a scalar radius in all dimensions
     *
     * @param pos center-point
     * @param radius size of scalar radius
     * @param do3D 3 dimensions (XYZ) iff true, otherwise 2 dimensions (XZ)
     * @param bndScene bounds on the scene, used to clip the bounding-box
     * @return a newly created bounding-box
     */
    public static BoundingBox boxFromBounds(
            Point3d pos, double radius, boolean do3D, Dimensions bndScene) {
        DoubleMatrix1D radiusBBoxMatrix = threeElementMatrix(radius, radius, radius);
        return boxFromBounds(pos, radiusBBoxMatrix, do3D, bndScene);
    }

    /**
     * Calculates a bounding box for a point with varying radii in each dimension (that have already
     * been resolved into a matrix)
     *
     * @param center center-point
     * @param radiiMatrix a matrix with resolved-radii for each dimension
     * @param do3D 3 dimensions (XYZ) iff true, otherwise 2 dimensions (XZ)
     * @param dimensions image-bounds, used to clip the bounding-box
     * @return a newly created bounding-box
     */
    public static BoundingBox boxFromBounds(
            Point3d center, DoubleMatrix1D radiiMatrix, boolean do3D, Dimensions dimensions) {
        Point3i minPt = subTwoPointsClip(center, radiiMatrix, do3D, dimensions);
        Point3i maxPt = addTwoPointsClip(center, radiiMatrix, do3D, dimensions);

        assert maxPt.x() >= minPt.x();
        assert maxPt.y() >= minPt.y();
        assert maxPt.z() >= minPt.z();

        return new BoundingBox(minPt, maxPt);
    }

    private static Point3i subTwoPointsClip(
            Point3d point1, DoubleMatrix1D point2, boolean do3D, Dimensions dimensions) {
        Point3i point = subTwoPoints(point1, point2, do3D);
        return PointClipper.clip(point, dimensions);
    }

    private static Point3i addTwoPointsClip(
            Point3d point1, DoubleMatrix1D point2, boolean do3D, Dimensions dimensions) {
        Point3i point = addTwoPoints(point1, point2, do3D);
        return PointClipper.clip(point, dimensions);
    }

    /**
     * Creates a new point that is the subtraction of one point from another (a Point3d minus a
     * DoubleMatrix1D)
     */
    private static Point3i subTwoPoints(Point3d point1, DoubleMatrix1D point2, boolean do3D) {
        Point3i out = new Point3i();
        out.setX(floorDiff(point1.x(), point2.get(0)));
        out.setY(floorDiff(point1.y(), point2.get(1)));
        out.setZ(do3D ? floorDiff(point1.z(), point2.get(2)) : 0);
        return out;
    }

    /**
     * Creates a new point that is the sum of two existing point (one a Point3d, and one a
     * DoubleMatrix1D)
     */
    private static Point3i addTwoPoints(Point3d point1, DoubleMatrix1D point2, boolean do3D) {
        Point3i out = new Point3i();
        out.setX(ceilSum(point1.x(), point2.get(0)));
        out.setY(ceilSum(point1.y(), point2.get(1)));
        out.setZ(do3D ? ceilSum(point1.z(), point2.get(2)) : 0);
        return out;
    }

    private static int floorDiff(double x, double y) {
        return (int) Math.floor(x - y);
    }

    private static int ceilSum(double x, double y) {
        return (int) Math.ceil(x + y);
    }
}
