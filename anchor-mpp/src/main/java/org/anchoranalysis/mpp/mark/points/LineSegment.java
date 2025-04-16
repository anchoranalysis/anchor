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

import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.points.BoundingBoxFromPoints;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Represents a line segment in 3D space as a Mark.
 */
@NoArgsConstructor
public class LineSegment extends Mark {

    private static final long serialVersionUID = 6436383113190855927L;

    private static final byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
    private static final byte FLAG_SUBMARK_INSIDE =
            RegionMembershipUtilities.flagForRegion(GlobalRegionIdentifiers.SUBMARK_INSIDE);

    /** The maximum distance from the line for a point to be considered inside. */
    private double distanceToLineForInside = 0.5;

    /** Calculator for distances to the line segment. */
    private transient DistanceCalculatorToLine distanceCalcToLine = new DistanceCalculatorToLine();

    /**
     * Constructs a LineSegment with given start and end points.
     *
     * @param startPoint the start point of the line segment
     * @param endPoint the end point of the line segment
     */
    public LineSegment(Point3i startPoint, Point3i endPoint) {
        setPoints(startPoint, endPoint);
    }

    // This isn't very efficient for lines, as we can analytically determine
    //   which pixels are inside
    // We assume this is only ever called for points within the bounding box, otherwise
    //  we need to do a check and reject all others
    @Override
    public byte isPointInside(Point3i point) {

        // TODO This should be half the distance from one corner of a pixel/voxel to another
        // And it thus depends on the number of dimensions
        // In future we calculate this in a better way

        if (distanceCalcToLine.distanceToLine(point) < distanceToLineForInside) {
            return FLAG_SUBMARK_INSIDE;
        }

        return FLAG_SUBMARK_NONE;
    }

    @Override
    public BoundingBox box(Dimensions dimensions, int regionID) {
        return BoundingBoxFromPoints.fromTwoPoints(
                distanceCalcToLine.getStartPoint(), distanceCalcToLine.getEndPoint());
    }

    @Override
    public Mark duplicate() {
        LineSegment out = new LineSegment();
        out.setPoints(distanceCalcToLine.getStartPoint(), distanceCalcToLine.getEndPoint());
        return out;
    }

    @Override
    public double volume(int regionID) {
        // The Line Length
        return distanceCalcToLine.getStartPoint().distance(distanceCalcToLine.getEndPoint());
    }

    @Override
    public String toString() {
        return String.format(
                "%s-%s",
                distanceCalcToLine.getStartPoint().toString(),
                distanceCalcToLine.getEndPoint().toString());
    }

    @Override
    public int numberDimensions() {
        return 3;
    }

    @Override
    public String getName() {
        return "markLineSegment";
    }

    @Override
    public void scale(ScaleFactor scaleFactor) {
        scaleFactor.scale(distanceCalcToLine.getStartPoint());
        scaleFactor.scale(distanceCalcToLine.getEndPoint());
    }

    @Override
    public Point3d centerPoint() {
        Point3d point = new Point3d(distanceCalcToLine.getStartPoint());
        point.add(distanceCalcToLine.getEndPoint());
        point.scale(0.5);
        return point;
    }

    /**
     * Sets the start and end points of the line segment.
     *
     * @param startPoint the start point
     * @param endPoint the end point
     */
    public void setPoints(Point3i startPoint, Point3i endPoint) {
        setPoints(PointConverter.doubleFromInt(startPoint), PointConverter.doubleFromInt(endPoint));
    }

    /**
     * Sets the start and end points of the line segment.
     *
     * @param startPoint the start point
     * @param endPoint the end point
     */
    public void setPoints(Point3d startPoint, Point3d endPoint) {
        distanceCalcToLine.setPoints(startPoint, endPoint);
    }

    /**
     * Gets the start point of the line segment.
     *
     * @return the start point
     */
    public Point3d getStartPoint() {
        return distanceCalcToLine.getStartPoint();
    }

    /**
     * Gets the end point of the line segment.
     *
     * @return the end point
     */
    public Point3d getEndPoint() {
        return distanceCalcToLine.getEndPoint();
    }

    /**
     * Gets the direction vector of the line segment.
     *
     * @return the direction vector
     */
    public Point3d getDirectionVector() {
        return distanceCalcToLine.getDirectionVector();
    }

    @Override
    public int numberRegions() {
        return 1;
    }

    @Override
    public BoundingBox boxAllRegions(Dimensions dimensions) {
        return box(dimensions, GlobalRegionIdentifiers.SUBMARK_INSIDE);
    }
}