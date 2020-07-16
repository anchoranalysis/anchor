/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.points;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkAbstractPointList;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;

public class MarkPointList extends MarkAbstractPointList {

    /** */
    private static final long serialVersionUID = 1718294470056379145L;

    private static final byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
    private static final byte FLAG_SUBMARK_INSIDE =
            RegionMembershipUtilities.flagForRegion(GlobalRegionIdentifiers.SUBMARK_INSIDE);

    private Set<Point3d> set; // A set that makes it quick to check if a point is on the list

    public MarkPointList() {
        super();
    }

    public MarkPointList(Stream<Point3d> stream) {
        super(stream);
    }

    @Override
    public byte evalPointInside(Point3d pointIsInside) {

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
        MarkPointList out = new MarkPointList();
        doDuplicate(out);
        return out;
    }

    @Override
    public double volume(int regionID) {
        return getPoints().size();
    }

    @Override
    public String toString() {
        return MarkPointList.class.getSimpleName() + "_" + this.hashCode();
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
        return MarkPointList.class.getSimpleName();
    }

    @Override
    public Point3d centerPoint() {
        // We take the mean of the BBOX as it's not really well defined. We probably should take the
        // COG.
        return bbox().midpoint();
    }

    @Override
    public int numRegions() {
        return 1;
    }

    @Override
    public BoundingBox bboxAllRegions(ImageDimensions bndScene) {
        return bbox(bndScene, GlobalRegionIdentifiers.SUBMARK_INSIDE);
    }
}
