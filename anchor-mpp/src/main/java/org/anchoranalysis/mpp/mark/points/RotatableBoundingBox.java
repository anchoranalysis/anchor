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

package org.anchoranalysis.mpp.mark.points;

import static org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities.flagForNoRegion;
import static org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities.flagForRegion;
import static org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers.SUBMARK_INSIDE;
import java.util.List;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.orientation.Orientation2D;
import org.anchoranalysis.image.points.BoundingBoxFromPoints;
import org.anchoranalysis.math.rotation.RotationMatrix;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkWithPosition;

/**
 * A two-dimensional bounding-box rotated at arbitrary angle in XY plane around a point.
 *
 * <p>Axis-aligned bounding boxes are also supported by fixing orientation.
 *
 * @author Owen Feehan
 */
public class RotatableBoundingBox extends MarkWithPosition {

    /** */
    private static final long serialVersionUID = 1L;

    private static final byte FLAG_SUBMARK_NONE = flagForNoRegion();
    private static final byte FLAG_SUBMARK_REGION0 = flagForRegion(SUBMARK_INSIDE);

    // START mark state

    // Note that internally three-dimensional points are used instead of two-dimensional as it
    //  fits more nicely with the rotation matrices (at the cost of some extra computation).

    /** Add to orientation to get the left-point and bottom-point (without rotation) */
    private Point3d distanceToLeftBottom;

    /** Add to orientation to get the right-point and top-point (without rotation) */
    private Point3d distanceToRightTop;

    private Orientation2D orientation;
    // END mark state

    // START internal objects
    private RotationMatrix rotMatrix;
    private RotationMatrix rotMatrixInv; // Inversion of rotMatrix
    // END internal objects

    public RotatableBoundingBox() {
        this.update(new Point2d(0, 0), new Point2d(0, 0), new Orientation2D());
    }

    @Override
    public byte isPointInside(Point3d point) {

        // See if after rotating a point back, it lies with on our box
        Point3d pointRelative = Point3d.immutableSubtract(point, getPos());

        Point3d pointRot = rotMatrixInv.rotatedPoint(pointRelative);

        if (pointRot.x() < distanceToLeftBottom.x() || pointRot.x() >= distanceToRightTop.x()) {
            return FLAG_SUBMARK_NONE;
        }

        if (pointRot.y() < distanceToLeftBottom.y() || pointRot.y() >= distanceToRightTop.y()) {
            return FLAG_SUBMARK_NONE;
        }

        return FLAG_SUBMARK_REGION0;
    }

    public void update(
            Point2d distanceToLeftBottom, Point2d distanceToRightTop, Orientation2D orientation) {

        update(convert3d(distanceToLeftBottom), convert3d(distanceToRightTop), orientation);
    }

    /** Internal version with Point3d */
    private void update(
            Point3d distanceToLeftBottom, Point3d distanceToRightTop, Orientation2D orientation) {
        this.distanceToLeftBottom = distanceToLeftBottom;
        this.distanceToRightTop = distanceToRightTop;
        this.orientation = orientation;

        this.rotMatrix = orientation.createRotationMatrix();
        this.rotMatrixInv = rotMatrix.transpose();
    }

    @Override
    public BoundingBox boxAllRegions(Dimensions dimensions) {

        Point3d[] points =
                new Point3d[] {
                    cornerPoint(false, false),
                    cornerPoint(true, false),
                    cornerPoint(false, true),
                    cornerPoint(true, true)
                };

        try {
            BoundingBox box = BoundingBoxFromPoints.forList(rotateAddPos(points));
            return box.clipTo(dimensions.extent());
        } catch (OperationFailedException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    @Override
    public BoundingBox box(Dimensions dimensions, int regionID) {
        return boxAllRegions(dimensions);
    }

    @Override
    public double volume(int regionID) {
        // The volume is invariant to rotation
        double width = distanceToRightTop.x() - distanceToLeftBottom.x();
        double height = distanceToRightTop.y() - distanceToLeftBottom.y();
        return width * height;
    }

    @Override
    public Mark duplicate() {
        RotatableBoundingBox out = new RotatableBoundingBox();
        out.update(distanceToLeftBottom, distanceToRightTop, orientation.duplicate());
        return out;
    }

    @Override
    public int numberRegions() {
        return 1;
    }

    @Override
    public String getName() {
        return "rotatableBoundingBox";
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int numDims() {
        return 2;
    }

    private static Point3d convert3d(Point2d point) {
        return new Point3d(point.x(), point.y(), 0);
    }

    private Point3d cornerPoint(boolean x, boolean y) {
        return new Point3d(
                x ? distanceToLeftBottom.x() : distanceToRightTop.x(),
                y ? distanceToLeftBottom.y() : distanceToRightTop.y(),
                0);
    }

    private List<Point3i> rotateAddPos(Point3d[] points) {
        return FunctionalList.mapToList(
                points, point -> PointConverter.intFromDoubleFloor(rotateAddPos(point)));
    }

    /** Rotates a position and adds the current position afterwards */
    private Point3d rotateAddPos(Point3d point) {
        Point3d out = rotMatrix.rotatedPoint(point);
        out.add(getPos());
        return out;
    }
}
