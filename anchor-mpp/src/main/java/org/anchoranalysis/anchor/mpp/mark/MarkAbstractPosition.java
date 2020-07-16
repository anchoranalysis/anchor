/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.overlay.OverlayProperties;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

public abstract class MarkAbstractPosition extends Mark implements Serializable {

    /** */
    private static final long serialVersionUID = -6976277985708631268L;

    // START mark state
    @Getter @Setter private Point3d pos;
    // END mark state

    // Constructor
    public MarkAbstractPosition() {
        super();
        this.pos = new Point3d();
    }

    // Copy constructor
    public MarkAbstractPosition(MarkAbstractPosition src) {
        super(src);
        this.pos = new Point3d(src.pos);
    }

    public String strPos() {
        return String.format(
                "[%6.1f,%6.1f,%6.1f]", this.pos.getX(), this.pos.getY(), this.pos.getZ());
    }

    @Override
    public void scale(double multFactor) {
        scaleXYPoint(this.pos, multFactor);
    }

    public static void scaleXYPoint(Point3d point, double multFactor) {
        point.setX(point.getX() * multFactor);
        point.setY(point.getY() * multFactor);
    }

    @Override
    public Point3d centerPoint() {
        return getPos();
    }

    // Checks if two marks are equal by comparing all attributes
    @Override
    public boolean equalsDeep(Mark m) {

        if (!super.equalsDeep(m)) {
            return false;
        }

        if (!(m instanceof MarkAbstractPosition)) {
            return false;
        }

        MarkAbstractPosition trgt = (MarkAbstractPosition) m;
        return pos.equals(trgt.pos);
    }

    @Override
    public ObjectWithProperties calcMask(
            ImageDimensions bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bvOut) {

        ObjectWithProperties mask = super.calcMask(bndScene, rm, bvOut);
        mask.setProperty("midpointInt", calcRelativePoint(pos, mask.getBoundingBox().cornerMin()));
        return mask;
    }

    @Override
    public OverlayProperties generateProperties(ImageResolution sr) {
        OverlayProperties op = super.generateProperties(sr);

        int numDims = numDims();

        if (numDims >= 1) {
            op.addDoubleAsString("Pos X", pos.getX());
        }
        if (numDims >= 2) {
            op.addDoubleAsString("Pos Y", pos.getY());
        }
        if (numDims >= 3) {
            op.addDoubleAsString("Pos Z", pos.getZ());
        }
        return op;
    }

    /** Calculates a relative-point from pointGlobal to pointBase */
    private static Point3i calcRelativePoint(Point3d pointGlobal, ReadableTuple3i pointBase) {
        Point3i pointOut = PointConverter.intFromDouble(pointGlobal);
        pointOut.subtract(pointBase);
        return pointOut;
    }
}
