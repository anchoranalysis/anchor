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

import java.util.Arrays;
import org.anchoranalysis.core.exception.CheckedUnsupportedOperationException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.points.BoundingBoxFromPoints;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkWithPosition;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.orientation.Orientation;
import org.anchoranalysis.spatial.orientation.Orientation2D;
import org.anchoranalysis.spatial.orientation.RotationMatrix;
import org.anchoranalysis.spatial.point.Point2d;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * A two-dimensional bounding-box rotated at arbitrary angle in XY plane around a point.
 *
 * <p>Axis-aligned bounding boxes are also supported by fixing orientation.
 *
 * @author Owen Feehan
 */
public class RotatableBoundingBox extends MarkWithPosition {

    private static final long serialVersionUID = 1L;

    private static final byte FLAG_SUBMARK_NONE = flagForNoRegion();
    private static final byte FLAG_SUBMARK_REGION0 = flagForRegion(SUBMARK_INSIDE);

    /** Add to orientation to get the left-point and bottom-point (without rotation) */
    private Point3d distanceToLeftBottom;

    /** Add to orientation to get the right-point and top-point (without rotation) */
    private Point3d distanceToRightTop;

    /** An orientation in the 2D plane to rotate the bounding box by. */
    private Orientation orientation;

    private RotationMatrix rotMatrix;
    private RotationMatrix rotMatrixInv; // Inversion of rotMatrix

    /**
     * Repeatedly reused when evaluating points. We instantiate it here, to avoid unnecessary heap
     * allocation.
     */
    private Point3d pointRelative = new Point3d();

    /** Creates a new RotatableBoundingBox with default values. */
    public RotatableBoundingBox() {
        this.update(new Point2d(0, 0), new Point2d(0, 0), new Orientation2D());
    }

    @Override
    public byte isPointInside(Point3i point) {
        pointRelative.setX(point.x() - getPosition().x());
        pointRelative.setY(point.y() - getPosition().y());
        pointRelative.setZ(point.z() - getPosition().z());

        // See if after rotating a point back, it lies with on our box
        rotMatrixInv.rotatePointInplace(pointRelative);

        if (pointRelative.x() < distanceToLeftBottom.x()
                || pointRelative.x() >= distanceToRightTop.x()) {
            return FLAG_SUBMARK_NONE;
        }

        if (pointRelative.y() < distanceToLeftBottom.y()
                || pointRelative.y() >= distanceToRightTop.y()) {
            return FLAG_SUBMARK_NONE;
        }

        return FLAG_SUBMARK_REGION0;
    }

    /**
     * Updates the bounding box with new dimensions and orientation.
     *
     * @param distanceToLeftBottom the distance to the left-bottom corner
     * @param distanceToRightTop the distance to the right-top corner
     * @param orientation the orientation of the bounding box
     */
    public void update(
            Point2d distanceToLeftBottom, Point2d distanceToRightTop, Orientation orientation) {
        update(convert3d(distanceToLeftBottom), convert3d(distanceToRightTop), orientation);
    }

    /** Internal version of update with Point3d. */
    private void update(
            Point3d distanceToLeftBottom, Point3d distanceToRightTop, Orientation orientation) {
        this.distanceToLeftBottom = distanceToLeftBottom;
        this.distanceToRightTop = distanceToRightTop;
        this.orientation = orientation;

        this.rotMatrix = orientation.getRotationMatrix();
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
            BoundingBox box =
                    BoundingBoxFromPoints.fromStream(Arrays.stream(points).map(this::rotateAddPos));
            return box.clampTo(dimensions.extent());
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
        out.update(distanceToLeftBottom, distanceToRightTop, orientation);
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
    public int numberDimensions() {
        return 2;
    }

    /** Converts a 2D point to a 3D point with z=0. */
    private static Point3d convert3d(Point2d point) {
        return new Point3d(point.x(), point.y(), 0);
    }

    /**
     * Creates a corner point of the bounding box.
     *
     * @param x true for right, false for left
     * @param y true for top, false for bottom
     * @return the corner point
     */
    private Point3d cornerPoint(boolean x, boolean y) {
        return new Point3d(
                x ? distanceToLeftBottom.x() : distanceToRightTop.x(),
                y ? distanceToLeftBottom.y() : distanceToRightTop.y(),
                0);
    }

    /** Rotates a position and adds the current position afterwards. */
    private Point3i rotateAddPos(Point3d point) {
        rotMatrix.rotatePointInplace(point);
        point.add(getPosition());
        return PointConverter.intFromDoubleFloor(point);
    }

    @Override
    public void scale(ScaleFactor scaleFactor) throws CheckedUnsupportedOperationException {
        super.scale(scaleFactor);
        scaleFactor.scale(distanceToLeftBottom);
        scaleFactor.scale(distanceToRightTop);
    }
}
