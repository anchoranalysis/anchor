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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.anchoranalysis.core.exception.CheckedUnsupportedOperationException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * A a list of 3D points.
 *
 * <p>Internally a set data-structure (as opposed to list) is used to store the points.
 */
public class PointList extends PointListBase {

    private static final long serialVersionUID = 1718294470056379145L;

    private static final byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
    private static final byte FLAG_SUBMARK_INSIDE =
            RegionMembershipUtilities.flagForRegion(GlobalRegionIdentifiers.SUBMARK_INSIDE);

    /** A set that makes it quick to check if a point is on the list. */
    private Set<Point3d> set;

    /** Constructs an empty PointList. */
    public PointList() {
        super();
    }

    /**
     * Constructs a PointList from a stream of Point3d objects.
     *
     * @param stream the stream of Point3d objects
     */
    public PointList(Stream<Point3d> stream) {
        super(stream);
    }

    @Override
    public byte isPointInside(Point3i pointIsInside) {

        // FOR NOW WE IGNORE THE SHELL RADIUS
        if (set.contains(PointConverter.doubleFromInt(pointIsInside))) {
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

    /**
     * Scales the points in the list by a given scale factor.
     *
     * @param scaleFactor the scale factor to apply
     * @throws CheckedUnsupportedOperationException if scaling is not supported
     */
    @Override
    public void scale(ScaleFactor scaleFactor) throws CheckedUnsupportedOperationException {
        for (int i = 0; i < getPoints().size(); i++) {
            Point3d point = getPoints().get(i);
            scaleFactor.scale(point);
        }
    }

    @Override
    public int numberDimensions() {
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
