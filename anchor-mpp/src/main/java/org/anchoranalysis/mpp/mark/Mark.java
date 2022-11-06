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
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CheckedUnsupportedOperationException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

@NoArgsConstructor
public abstract class Mark implements Serializable {

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
    public abstract byte isPointInside(Point3i point);

    public abstract Mark duplicate();

    public abstract int numberRegions();

    public abstract String getName();

    /** An alternative "quick" metric for overlap for a {@link Mark}. */
    public Optional<QuickOverlapCalculation> quickOverlap() {
        return Optional.empty();
    }

    public abstract double volume(int regionID);

    /** String representation of the {@link Mark}. */
    @Override
    public abstract String toString();

    /**
     * Scales the mark in X and Y dimensions.
     *
     * @param scaleFactor how much to scale by.
     * @throws CheckedUnsupportedOperationException if the type of mark used in the annotation does
     *     not supported scaling.
     */
    public abstract void scale(ScaleFactor scaleFactor) throws CheckedUnsupportedOperationException;

    public abstract int numberDimensions();

    // center point
    public abstract Point3d centerPoint();

    public abstract BoundingBox box(Dimensions dimensions, int regionID);

    public abstract BoundingBox boxAllRegions(Dimensions dimensions);

    public boolean equalsID(Object obj) {

        if (obj instanceof Mark) {
            Mark mark = (Mark) obj;
            return this.id == mark.id;
        }

        return false;
    }

    // Checks if two marks are equal by comparing all attributes
    public boolean equalsDeep(Mark mark) {
        // ID check
        return equalsID(mark);
    }

    /**
     * Create a {@link ObjectMask} representation of the {@link Mark}.
     *
     * <p>i.e. the {@link Mark} is converted into voxels within a bounding-box.
     *
     * <p>The {@link ObjectMask} is forced to entirely be contained within {@code dimensions}.
     *
     * @param dimensions the size of the image in which the {@link Mark} resides.
     * @param region which region(s) of the {@link Mark} to voxelize.
     * @param binaryValues how to encode on and off voxels in the created {@link
     *     ObjectWithProperties}.
     * @return the created {@link ObjectMask} with associated properties.
     */
    public ObjectMask deriveObject(
            Dimensions dimensions,
            RegionMembershipWithFlags region,
            BinaryValuesByte binaryValues) {

        BoundingBox box = this.box(dimensions, region.getRegionID());

        // We make a new mask and populate it from out iterator
        ObjectMask object = new ObjectMask(box);

        byte maskOn = binaryValues.getOn();

        ReadableTuple3i maxPos = box.calculateCornerMaxInclusive();

        Point3i point = new Point3i();
        for (point.setZ(box.cornerMin().z()); point.z() <= maxPos.z(); point.incrementZ()) {

            int zLocal = point.z() - box.cornerMin().z();
            UnsignedByteBuffer maskSlice = object.sliceBufferLocal(zLocal);

            int count = 0;
            for (point.setY(box.cornerMin().y()); point.y() <= maxPos.y(); point.incrementY()) {
                for (point.setX(box.cornerMin().x()); point.x() <= maxPos.x(); point.incrementX()) {

                    byte membership = isPointInside(point);

                    if (region.isMemberFlag(membership)) {
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

    public int getIdentifier() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
