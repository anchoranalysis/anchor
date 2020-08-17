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

package org.anchoranalysis.anchor.mpp.mark;

import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.points.BoundingBoxFromPoints;

@NoArgsConstructor
public class MarkLineSegment extends Mark {

    /** */
    private static final long serialVersionUID = 6436383113190855927L;

    private static final byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
    private static final byte FLAG_SUBMARK_INSIDE =
            RegionMembershipUtilities.flagForRegion(GlobalRegionIdentifiers.SUBMARK_INSIDE);

    // START mark state
    private double distanceToLineForInside = 0.5;
    // END mark state

    private transient DistanceCalculatorToLine distanceCalcToLine = new DistanceCalculatorToLine();

    public MarkLineSegment(Point3i startPoint, Point3i endPoint) {
        this();
        setPoints(startPoint, endPoint);
    }

    // This isn't very efficient for lines, as we can analytically determine
    //   which pixels are inside
    // We assume this is only ever called for points within the bounding box, otherwise
    //  we need to do a check and reject all others
    @Override
    public byte isPointInside(Point3d point) {

        // TODO
        // This should be half the distance from one corner of a pixel/voxel to another
        // And it thus depends on the number of dimensions
        // In future we calculate this in a better way

        if (distanceCalcToLine.distanceToLine(point) < distanceToLineForInside) {
            return FLAG_SUBMARK_INSIDE;
        }

        return FLAG_SUBMARK_NONE;
    }

    @Override
    public BoundingBox box(ImageDimensions dimensions, int regionID) {
        return BoundingBoxFromPoints.forTwoPoints(
                distanceCalcToLine.getStartPoint(), distanceCalcToLine.getEndPoint());
    }

    @Override
    public Mark duplicate() {
        MarkLineSegment out = new MarkLineSegment();
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
    public int numDims() {
        return 3;
    }

    @Override
    public String getName() {
        return "markLineSegment";
    }

    @Override
    public void scale(double multFactor) {
        MarkAbstractPosition.scaleXYPoint(distanceCalcToLine.getStartPoint(), multFactor);
        MarkAbstractPosition.scaleXYPoint(distanceCalcToLine.getEndPoint(), multFactor);
    }

    @Override
    public Point3d centerPoint() {
        Point3d point = new Point3d(distanceCalcToLine.getStartPoint());
        point.add(distanceCalcToLine.getEndPoint());
        point.scale(0.5);
        return point;
    }

    public void setPoints(Point3i startPoint, Point3i endPoint) {
        setPoints(PointConverter.doubleFromInt(startPoint), PointConverter.doubleFromInt(endPoint));
    }

    public void setPoints(Point3d startPoint, Point3d endPoint) {
        distanceCalcToLine.setPoints(startPoint, endPoint);
    }

    public Point3d getStartPoint() {
        return distanceCalcToLine.getStartPoint();
    }

    public Point3d getEndPoint() {
        return distanceCalcToLine.getEndPoint();
    }

    public Point3d getDirectionVector() {
        return distanceCalcToLine.getDirectionVector();
    }

    @Override
    public int numberRegions() {
        return 1;
    }

    @Override
    public BoundingBox boxAllRegions(ImageDimensions dimensions) {
        return box(dimensions, GlobalRegionIdentifiers.SUBMARK_INSIDE);
    }
}
