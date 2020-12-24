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

package org.anchoranalysis.mpp.mark;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.DoubleUnaryOperator;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OptionalOperationUnsupportedException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.dimensions.SpatialUnits.UnitSuffix;
import org.anchoranalysis.image.core.dimensions.UnitConverter;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.overlay.OverlayProperties;
import org.anchoranalysis.overlay.identifier.Identifiable;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

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
    protected Mark(Mark source) {
        // We do not deep copy
        this.id = source.id;
    }

    // It is permissible to mutate the point during calculation
    public abstract byte isPointInside(Point3d point);

    public abstract Mark duplicate();

    public abstract int numberRegions();

    public abstract String getName();

    /** An alternative "quick" metric for overlap for a mark */
    public Optional<QuickOverlapCalculation> quickOverlap() {
        return Optional.empty();
    }

    public abstract double volume(int regionID);

    /** String representation of the mark */
    @Override
    public abstract String toString();

    /**
     * Scales the mark in X and Y dimensions.
     *
     * @param scaleFactor how much to scale by.
     * @throws OptionalOperationUnsupportedException if the type of mark used in the annotation does
     *     not supported scaling.
     */
    public abstract void scale(double scaleFactor) throws OptionalOperationUnsupportedException;

    public abstract int numberDimensions();

    // center point
    public abstract Point3d centerPoint();

    public abstract BoundingBox box(Dimensions dimensions, int regionID);

    public abstract BoundingBox boxAllRegions(Dimensions dimensions);

    protected byte evalPointInside(Point3i point) {
        return this.isPointInside(PointConverter.doubleFromInt(point));
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

    /**
     * Create an object-mask representation of the mark (i.e. in voxels in a bounding-box)
     *
     * @param dimensions
     * @param rm
     * @param bv
     * @return
     */
    public ObjectWithProperties deriveObject(
            Dimensions dimensions, RegionMembershipWithFlags rm, BinaryValuesByte bv) {

        BoundingBox box = this.box(dimensions, rm.getRegionID());

        // We make a new mask and populate it from out iterator
        ObjectWithProperties object = new ObjectWithProperties(box);

        byte maskOn = bv.getOnByte();

        ReadableTuple3i maxPos = box.calculateCornerMax();

        Point3i point = new Point3i();
        for (point.setZ(box.cornerMin().z()); point.z() <= maxPos.z(); point.incrementZ()) {

            int zLocal = point.z() - box.cornerMin().z();
            UnsignedByteBuffer maskSlice = object.sliceBufferLocal(zLocal);

            int count = 0;
            for (point.setY(box.cornerMin().y()); point.y() <= maxPos.y(); point.incrementY()) {
                for (point.setX(box.cornerMin().x()); point.x() <= maxPos.x(); point.incrementX()) {

                    byte membership = evalPointInside(point);

                    if (rm.isMemberFlag(membership)) {
                        maskSlice.putRaw(count, maskOn);
                    }
                    count++;
                }
            }
        }
        return object;
    }

    // Calculates the mask of an object
    public ObjectWithProperties maskScaledXY(
            Dimensions bndScene,
            RegionMembershipWithFlags rm,
            BinaryValuesByte bvOut,
            double scaleFactor) {

        BoundingBox box = box(bndScene, rm.getRegionID()).scale(new ScaleFactor(scaleFactor));

        // We make a new mask and populate it from out iterator
        ObjectWithProperties object = new ObjectWithProperties(box);

        byte maskOn = bvOut.getOnByte();

        ReadableTuple3i maxPos = box.calculateCornerMax();

        Point3i point = new Point3i();
        Point3d pointScaled = new Point3d();
        for (point.setZ(box.cornerMin().z()); point.z() <= maxPos.z(); point.incrementZ()) {

            int zLocal = point.z() - box.cornerMin().z();
            UnsignedByteBuffer maskSlice = object.sliceBufferLocal(zLocal);

            // Z coordinates are the same as we only scale in XY
            pointScaled.setZ(point.z());

            int count = 0;
            for (point.setY(box.cornerMin().y()); point.y() <= maxPos.y(); point.incrementY()) {
                for (point.setX(box.cornerMin().x()); point.x() <= maxPos.x(); point.incrementX()) {

                    pointScaled.setX(((double) point.x()) / scaleFactor);
                    pointScaled.setY(((double) point.y()) / scaleFactor);

                    byte membership = isPointInside(pointScaled);

                    if (rm.isMemberFlag(membership)) {
                        maskSlice.putRaw(count, maskOn);
                    }
                    count++;
                }
            }
        }
        return object;
    }

    public String identifier() {
        return String.format("id=%10d", id);
    }

    @Override
    public int getIdentifier() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OverlayProperties generateProperties(Optional<Resolution> resolution) {

        OverlayProperties nvc = new OverlayProperties();
        nvc.add("Type", getName());
        nvc.add("ID", Integer.toString(getIdentifier()));
        if (resolution.isPresent()) {
            addPropertiesForRegions(nvc, resolution.get().unitConvert());
        }
        return nvc;
    }

    private void addPropertiesForRegions(OverlayProperties nvc, UnitConverter unitConverter) {
        for (int region = 0; region < numberRegions(); region++) {
            double vol = volume(region);

            String name = numberDimensions() == 3 ? "Volume" : "Area";

            UnitSuffix unit =
                    numberDimensions() == 3 ? UnitSuffix.CUBIC_MICRO : UnitSuffix.SQUARE_MICRO;

            DoubleUnaryOperator conversionFunc =
                    numberDimensions() == 3
                            ? unitConverter::toPhysicalVolume
                            : unitConverter::toPhysicalArea;

            nvc.addWithUnits(
                    String.format("%s [geom] %d", name, region), vol, conversionFunc, unit);
        }
    }
}
