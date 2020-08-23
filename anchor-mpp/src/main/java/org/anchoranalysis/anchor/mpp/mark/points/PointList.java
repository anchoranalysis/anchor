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

package org.anchoranalysis.anchor.mpp.mark.points;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Dimensions;

public class PointList extends PointListBase {

    /** */
    private static final long serialVersionUID = 1718294470056379145L;

    private static final byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
    private static final byte FLAG_SUBMARK_INSIDE =
            RegionMembershipUtilities.flagForRegion(GlobalRegionIdentifiers.SUBMARK_INSIDE);

    private Set<Point3d> set; // A set that makes it quick to check if a point is on the list

    public PointList() {
        super();
    }

    public PointList(Stream<Point3d> stream) {
        super(stream);
    }

    @Override
    public byte isPointInside(Point3d pointIsInside) {

        // FOR NOW WE IGNORE THE SHELL RADIUS
        if (PointInSetQuery.anyCrnrInSet(pointIsInside, set)) {
            return FLAG_SUBMARK_INSIDE;
        } else {
            return FLAG_SUBMARK_NONE;
        }
    }

    @Override
    public void updateAfterPointsChange() {
        super.updateAfterPointsChange();

        this.set = new HashSet<>(getPoints());
    }

    @Override
    public Mark duplicate() {
        PointList out = new PointList();
        doDuplicate(out);
        return out;
    }

    @Override
    public double volume(int regionID) {
        return getPoints().size();
    }

    @Override
    public String toString() {
        return PointList.class.getSimpleName() + "_" + this.hashCode();
    }

    @Override
    public void scale(double multFactor) throws OptionalOperationUnsupportedException {

        for (int i = 0; i < getPoints().size(); i++) {

            Point3d point = getPoints().get(i);
            point.scale(multFactor);
        }
    }

    @Override
    public int numDims() {
        return 2;
    }

    @Override
    public String getName() {
        return PointList.class.getSimpleName();
    }

    @Override
    public Point3d centerPoint() {
        // We take the mean of the BBOX as it's not really well defined. We probably should take the
        // COG.
        return box().midpoint();
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
