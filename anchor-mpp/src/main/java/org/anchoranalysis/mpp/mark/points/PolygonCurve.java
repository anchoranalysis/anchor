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

import org.anchoranalysis.core.exception.CheckedUnsupportedOperationException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/** Represents a polygon curve in 3D space as a Mark. */
public class PolygonCurve extends PointListBase {

    private static final long serialVersionUID = -2043844259526872933L;

    private static final byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
    private static final byte FLAG_SUBMARK_INSIDE =
            RegionMembershipUtilities.flagForRegion(GlobalRegionIdentifiers.SUBMARK_INSIDE);

    /**
     * The distance threshold applied in all 3 dimensions. TODO: Consider changing this to be
     * dimension-specific.
     */
    private static final double DISTANCE_THRESHOLD = 0.7;

    /** Calculator for distances to line segments. */
    private transient DistanceCalculatorToLine distanceCalculator = new DistanceCalculatorToLine();

    @Override
    public byte isPointInside(Point3i point) {

        if (distanceToPolygonLocal(point) < DISTANCE_THRESHOLD) {
            return FLAG_SUBMARK_INSIDE;
        }
        return FLAG_SUBMARK_NONE;
    }

    /**
     * Calculates the distance from a point to a polygon segment.
     *
     * @param point the point to calculate the distance from
     * @param pointFirst the first point of the polygon segment
     * @param pointSecond the second point of the polygon segment
     * @return the distance from the point to the polygon segment, or Double.POSITIVE_INFINITY if
     *     the point is outside the threshold
     */
    private double distanceToPolygonSegmentLocal(
            Point3i point, Point3d pointFirst, Point3d pointSecond) {

        if (point.x() < (pointFirst.x() - DISTANCE_THRESHOLD)) {
            return Double.POSITIVE_INFINITY;
        }

        if (point.y() < (pointFirst.y() - DISTANCE_THRESHOLD)) {
            return Double.POSITIVE_INFINITY;
        }

        if (point.z() < (pointFirst.z() - DISTANCE_THRESHOLD)) {
            return Double.POSITIVE_INFINITY;
        }

        if (point.x() > (pointSecond.x() + DISTANCE_THRESHOLD)) {
            return Double.POSITIVE_INFINITY;
        }

        if (point.y() > (pointSecond.y() + DISTANCE_THRESHOLD)) {
            return Double.POSITIVE_INFINITY;
        }

        if (point.z() > (pointSecond.z() + DISTANCE_THRESHOLD)) {
            return Double.POSITIVE_INFINITY;
        }

        distanceCalculator.setPoints(pointFirst, pointSecond);

        return distanceCalculator.distanceToLine(point);
    }

    /**
     * Calculates the distance from a point to the polygon curve.
     *
     * @param point the point to calculate the distance from
     * @return the minimum distance from the point to any segment of the polygon curve
     */
    private double distanceToPolygonLocal(Point3i point) {

        // If a point is inside the bounding box of two points +- the distanceThreshold, we
        // calculate the distance to
        // it.  And we take the minimum overall

        double min = Double.POSITIVE_INFINITY;

        int numPtsMinus1 = getPoints().size() - 1;
        for (int i = 0; i < numPtsMinus1; i++) {
            Point3d pointFirst = getPoints().get(i);
            Point3d pointSecond = getPoints().get(i + 1);

            double distance = distanceToPolygonSegmentLocal(point, pointFirst, pointSecond);
            min = Math.min(min, distance);
        }

        return min;
    }

    @Override
    public Mark duplicate() {
        PolygonCurve out = new PolygonCurve();
        doDuplicate(out);
        return out;
    }

    @Override
    public double volume(int regionID) {
        // What does mean really?
        return getPoints().size();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void scale(ScaleFactor scaleFactor) throws CheckedUnsupportedOperationException {
        throw new CheckedUnsupportedOperationException();
    }

    @Override
    public int numberDimensions() {
        return 2;
    }

    @Override
    public Point3d centerPoint() {
        // We take the mean of the BBOX as it's not really well defined. We probably should take the
        // COG.
        return box().midpoint();
    }

    @Override
    public String getName() {
        return "markPolygonCurve";
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
