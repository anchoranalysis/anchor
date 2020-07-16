/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.DoubleUnaryOperator;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.overlay.OverlayProperties;
import org.anchoranalysis.anchor.overlay.id.Identifiable;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.scale.ScaleFactor;

@NoArgsConstructor
public abstract class Mark implements Serializable, Identifiable {

    /** */
    private static final long serialVersionUID = 3272456193681334471L;

    // START mark state
    private int id = -1;
    // END mark state

    /**
     * Copy constructor
     *
     * @param source source to copy from
     */
    public Mark(Mark source) {
        // We do not deep copy
        this.id = source.id;
    }

    // It is permissible to mutate the point during calculation
    public abstract byte evalPointInside(Point3d pt);

    public abstract Mark duplicate();

    public abstract int numRegions();

    public abstract String getName();

    /** An alternative "quick" metric for overlap for a mark */
    public Optional<QuickOverlapCalculation> quickOverlap() {
        return Optional.empty();
    }

    public abstract double volume(int regionID);

    // String representation of mark
    @Override
    public abstract String toString();

    public abstract void scale(double multFactor) throws OptionalOperationUnsupportedException;

    public abstract int numDims();

    // center point
    public abstract Point3d centerPoint();

    public abstract BoundingBox bbox(ImageDimensions bndScene, int regionID);

    public abstract BoundingBox bboxAllRegions(ImageDimensions bndScene);

    protected byte evalPointInside(Point3i pt) {
        return this.evalPointInside(PointConverter.doubleFromInt(pt));
    }

    public boolean equalsID(Object obj) {

        if (obj instanceof Mark) {
            Mark mark = (Mark) obj;
            return this.id == mark.id;
        }

        return false;
    }

    // Checks if two marks are equal by comparing all attributes
    public boolean equalsDeep(Mark m) {
        // ID check
        return equalsID(m);
    }

    // Calculates the mask of an object
    public ObjectWithProperties calcMask(
            ImageDimensions bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bv) {

        BoundingBox bbox = this.bbox(bndScene, rm.getRegionID());

        // We make a new mask and populate it from out iterator
        ObjectWithProperties mask = new ObjectWithProperties(bbox);

        assert (mask.getVoxelBox().extent().getZ() > 0);

        byte maskOn = bv.getOnByte();

        ReadableTuple3i maxPos = bbox.calcCornerMax();

        Point3i point = new Point3i();
        for (point.setZ(bbox.cornerMin().getZ());
                point.getZ() <= maxPos.getZ();
                point.incrementZ()) {

            int zLocal = point.getZ() - bbox.cornerMin().getZ();
            ByteBuffer maskSlice = mask.getVoxelBox().getPixelsForPlane(zLocal).buffer();

            int cnt = 0;
            for (point.setY(bbox.cornerMin().getY());
                    point.getY() <= maxPos.getY();
                    point.incrementY()) {
                for (point.setX(bbox.cornerMin().getX());
                        point.getX() <= maxPos.getX();
                        point.incrementX()) {

                    byte membership = evalPointInside(point);

                    if (rm.isMemberFlag(membership)) {
                        maskSlice.put(cnt, maskOn);
                    }
                    cnt++;
                }
            }
        }

        assert (mask.getVoxelBox().extent().getZ() > 0);

        return mask;
    }

    // Calculates the mask of an object
    public ObjectWithProperties calcMaskScaledXY(
            ImageDimensions bndScene,
            RegionMembershipWithFlags rm,
            BinaryValuesByte bvOut,
            double scaleFactor) {

        BoundingBox bbox = bbox(bndScene, rm.getRegionID()).scale(new ScaleFactor(scaleFactor));

        // We make a new mask and populate it from out iterator
        ObjectWithProperties mask = new ObjectWithProperties(bbox);

        assert (mask.getVoxelBox().extent().getZ() > 0);

        byte maskOn = bvOut.getOnByte();

        ReadableTuple3i maxPos = bbox.calcCornerMax();

        Point3i point = new Point3i();
        Point3d pointScaled = new Point3d();
        for (point.setZ(bbox.cornerMin().getZ());
                point.getZ() <= maxPos.getZ();
                point.incrementZ()) {

            int zLocal = point.getZ() - bbox.cornerMin().getZ();
            ByteBuffer maskSlice = mask.getVoxelBox().getPixelsForPlane(zLocal).buffer();

            // Z coordinates are the same as we only scale in XY
            pointScaled.setZ(point.getZ());

            int cnt = 0;
            for (point.setY(bbox.cornerMin().getY());
                    point.getY() <= maxPos.getY();
                    point.incrementY()) {
                for (point.setX(bbox.cornerMin().getX());
                        point.getX() <= maxPos.getX();
                        point.incrementX()) {

                    pointScaled.setX(((double) point.getX()) / scaleFactor);
                    pointScaled.setY(((double) point.getY()) / scaleFactor);

                    byte membership = evalPointInside(pointScaled);

                    if (rm.isMemberFlag(membership)) {
                        maskSlice.put(cnt, maskOn);
                    }
                    cnt++;
                }
            }
        }

        assert (mask.getVoxelBox().extent().getZ() > 0);

        return mask;
    }

    public String strId() {
        return String.format("id=%10d", id);
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OverlayProperties generateProperties(ImageResolution res) {

        OverlayProperties nvc = new OverlayProperties();
        nvc.add("Type", getName());
        nvc.add("ID", Integer.toString(getId()));
        if (res == null) {
            return nvc;
        }

        addPropertiesForRegions(nvc, res);
        return nvc;
    }

    private void addPropertiesForRegions(OverlayProperties nvc, ImageResolution res) {
        for (int r = 0; r < numRegions(); r++) {
            double vol = volume(r);

            String name = numDims() == 3 ? "Volume" : "Area";

            UnitSuffix unit = numDims() == 3 ? UnitSuffix.CUBIC_MICRO : UnitSuffix.SQUARE_MICRO;

            DoubleUnaryOperator conversionFunc =
                    numDims() == 3 ? res::convertVolume : res::convertArea;

            nvc.addWithUnits(String.format("%s [geom] %d", name, r), vol, conversionFunc, unit);
        }
    }
}
