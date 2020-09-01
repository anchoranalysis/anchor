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
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.DoubleUnaryOperator;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.overlay.OverlayProperties;
import org.anchoranalysis.overlay.id.Identifiable;

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
    public abstract byte isPointInside(Point3d point);

    public abstract Mark duplicate();

    public abstract int numberRegions();

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
            ByteBuffer maskSlice = object.sliceBufferLocal(zLocal);

            int cnt = 0;
            for (point.setY(box.cornerMin().y()); point.y() <= maxPos.y(); point.incrementY()) {
                for (point.setX(box.cornerMin().x()); point.x() <= maxPos.x(); point.incrementX()) {

                    byte membership = evalPointInside(point);

                    if (rm.isMemberFlag(membership)) {
                        maskSlice.put(cnt, maskOn);
                    }
                    cnt++;
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
            ByteBuffer maskSlice = object.sliceBufferLocal(zLocal);

            // Z coordinates are the same as we only scale in XY
            pointScaled.setZ(point.z());

            int cnt = 0;
            for (point.setY(box.cornerMin().y()); point.y() <= maxPos.y(); point.incrementY()) {
                for (point.setX(box.cornerMin().x()); point.x() <= maxPos.x(); point.incrementX()) {

                    pointScaled.setX(((double) point.x()) / scaleFactor);
                    pointScaled.setY(((double) point.y()) / scaleFactor);

                    byte membership = isPointInside(pointScaled);

                    if (rm.isMemberFlag(membership)) {
                        maskSlice.put(cnt, maskOn);
                    }
                    cnt++;
                }
            }
        }
        return object;
    }

    public String identifier() {
        return String.format("id=%10d", id);
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OverlayProperties generateProperties(Resolution res) {

        OverlayProperties nvc = new OverlayProperties();
        nvc.add("Type", getName());
        nvc.add("ID", Integer.toString(getId()));
        if (res == null) {
            return nvc;
        }

        addPropertiesForRegions(nvc, res);
        return nvc;
    }

    private void addPropertiesForRegions(OverlayProperties nvc, Resolution res) {
        for (int r = 0; r < numberRegions(); r++) {
            double vol = volume(r);

            String name = numDims() == 3 ? "Volume" : "Area";

            UnitSuffix unit = numDims() == 3 ? UnitSuffix.CUBIC_MICRO : UnitSuffix.SQUARE_MICRO;

            DoubleUnaryOperator conversionFunc =
                    numDims() == 3 ? res::convertVolume : res::convertArea;

            nvc.addWithUnits(String.format("%s [geom] %d", name, r), vol, conversionFunc, unit);
        }
    }
}
