/*-
 * #%L
 * anchor-plugin-ij
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

import com.google.common.base.Preconditions;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import java.util.List;
import org.anchoranalysis.core.exception.OptionalOperationUnsupportedException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.scale.ScaleFactor;

public class Polygon extends PointListBase {

    /** */
    private static final long serialVersionUID = 1718294470056379145L;

    private static final byte FLAG_OUTSIDE = RegionMembershipUtilities.flagForNoRegion();
    private static final byte FLAG_SUBMARK_INSIDE =
            RegionMembershipUtilities.flagForRegion(GlobalRegionIdentifiers.SUBMARK_INSIDE);

    /** A region of interest in ImageJ representing a polygon */
    private PolygonRoi region;

    private double area;
    private Point3d centroid;

    @Override
    public byte isPointInside(Point3d point) {

        // FOR NOW WE IGNORE THE SHELL RADIUS
        if (containsPixel(point.x(), point.y()) || getPoints().contains(point)) {
            return FLAG_SUBMARK_INSIDE;
        }

        return FLAG_OUTSIDE;
    }

    private boolean containsPixel(double xDbl, double yDbl) {
        int x = (int) xDbl;
        int y = (int) yDbl;
        return contains(x, y) || contains(x, y + 1) || contains(x + 1, y) || contains(x + 1, y + 1);
    }

    private boolean contains(int x, int y) {
        return region.contains(x, y);
    }

    @Override
    public Mark duplicate() {
        Polygon out = new Polygon();
        doDuplicate(out);
        return out;
    }

    @Override
    public double volume(int regionID) {
        return area;
    }

    @Override
    public String toString() {
        return "polygon";
    }

    @Override
    public void scale(ScaleFactor scaleFactor) throws OptionalOperationUnsupportedException {
        throw new OptionalOperationUnsupportedException();
    }

    @Override
    public int numberDimensions() {
        return 2;
    }

    @Override
    public Point3d centerPoint() {
        return centroid;
    }

    @Override
    public String getName() {
        return "polygon";
    }

    @Override
    public void updateAfterPointsChange() {
        super.updateAfterPointsChange();
        Preconditions.checkArgument(!getPoints().isEmpty());

        this.area = area(getPoints());
        this.centroid = centroid(getPoints(), area);

        region = createRoi(getPoints());
    }

    private static PolygonRoi createRoi(List<Point3d> points) {

        float[] xArr = new float[points.size()];
        float[] yArr = new float[points.size()];

        int i = 0;
        for (Point3d point : points) {
            xArr[i] = (float) point.x();
            yArr[i] = (float) point.y();
            i++;
        }

        return new PolygonRoi(xArr, yArr, points.size(), Roi.POLYGON);
    }

    private static double area(List<Point3d> points) {
        // http://en.wikipedia.org/wiki/Polygon

        int numPoints = points.size();

        double sum = 0;

        for (int i = 0; i < numPoints; i++) {
            Point3d point = points.get(i);

            // We cycle around at the end
            Point3d pointNext = i == (numPoints - 1) ? points.get(0) : points.get(i + 1);

            sum += (point.x() * pointNext.y()) - (pointNext.x() * point.y());
        }
        return sum / 2;
    }

    private static Point3d centroid(List<Point3d> points, double area) {
        // http://en.wikipedia.org/wiki/Polygon
        // Shoelace formula

        int numPoints = points.size();

        Point3d centroid = new Point3d();

        for (int i = 0; i < numPoints; i++) {
            Point3d point = points.get(i);

            // We cycle around at the end
            Point3d pointNext = i == (numPoints - 1) ? points.get(0) : points.get(i + 1);

            double leftX = point.x() + pointNext.x();
            double leftY = point.y() + pointNext.y();
            double right = (point.x() * pointNext.y()) - (pointNext.x() * point.y());

            centroid.setX(centroid.x() + leftX * right);
            centroid.setY(centroid.y() + leftY * right);
        }

        centroid.scale(1 / (6 * area));

        return centroid;
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
