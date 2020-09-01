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

import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.Mark;

public class PolygonCurve extends PointListBase {

    /** */
    private static final long serialVersionUID = -2043844259526872933L;

    private static final byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
    private static final byte FLAG_SUBMARK_INSIDE =
            RegionMembershipUtilities.flagForRegion(GlobalRegionIdentifiers.SUBMARK_INSIDE);

    // Applied the same in all 3 dimensions, maybe we need to change this
    private double distanceThreshold = 0.7;

    private transient DistanceCalculatorToLine distanceCalculator = new DistanceCalculatorToLine();

    @Override
    public byte isPointInside(Point3d point) {

        if (distanceToPolygonLocal(point) < distanceThreshold) {
            return FLAG_SUBMARK_INSIDE;
        }
        return FLAG_SUBMARK_NONE;
    }

    private double distanceToPolygonSegmentLocal(
            Point3d point, Point3d pointFirst, Point3d pointSecond) {

        if (point.x() < (pointFirst.x() - distanceThreshold)) {
            return Double.POSITIVE_INFINITY;
        }

        if (point.y() < (pointFirst.y() - distanceThreshold)) {
            return Double.POSITIVE_INFINITY;
        }

        if (point.z() < (pointFirst.z() - distanceThreshold)) {
            return Double.POSITIVE_INFINITY;
        }

        if (point.x() > (pointSecond.x() + distanceThreshold)) {
            return Double.POSITIVE_INFINITY;
        }

        if (point.y() > (pointSecond.y() + distanceThreshold)) {
            return Double.POSITIVE_INFINITY;
        }

        if (point.z() > (pointSecond.z() + distanceThreshold)) {
            return Double.POSITIVE_INFINITY;
        }

        distanceCalculator.setPoints(pointFirst, pointSecond);

        return distanceCalculator.distanceToLine(point);
    }

    // Distance to polygon - only local (i.e. assumes that we only care about returning small values
    // in circumstances
    //  very close to the line segment in question, otherwise we don't care
    private double distanceToPolygonLocal(Point3d point) {

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
    public void scale(double multFactor) throws OptionalOperationUnsupportedException {
        throw new OptionalOperationUnsupportedException("Not supported");
    }

    @Override
    public int numDims() {
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
