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
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Abstract base class representing a mark in 3D space.
 *
 * <p>A mark is a geometric shape with various properties and operations.
 */
@NoArgsConstructor
public abstract class Mark implements Serializable {

    private static final long serialVersionUID = 3272456193681334471L;

    private int id = -1;

    /**
     * Copy constructor.
     *
     * @param source source mark to copy from
     */
    protected Mark(Mark source) {
        this.id = source.id;
    }

    /**
     * Determines if a point is inside the mark.
     *
     * @param point the point to check
     * @return a byte representing the region membership of the point
     */
    public abstract byte isPointInside(Point3i point);

    /**
     * Creates a duplicate of this mark.
     *
     * @return a new Mark instance that is a copy of this one
     */
    public abstract Mark duplicate();

    /**
     * Returns the number of regions in this mark.
     *
     * @return the number of regions
     */
    public abstract int numberRegions();

    /**
     * Returns the name of this mark type.
     *
     * @return the name of the mark
     */
    public abstract String getName();

    /**
     * Provides an optional quick overlap calculation method.
     *
     * @return an Optional containing a QuickOverlapCalculation, or empty if not available
     */
    public Optional<QuickOverlapCalculation> quickOverlap() {
        return Optional.empty();
    }

    /**
     * Calculates the volume of a specific region of the mark.
     *
     * @param regionID the ID of the region
     * @return the volume of the region
     */
    public abstract double volume(int regionID);

    /**
     * Scales the mark in X and Y dimensions.
     *
     * @param scaleFactor how much to scale by
     * @throws CheckedUnsupportedOperationException if scaling is not supported for this mark type
     */
    public abstract void scale(ScaleFactor scaleFactor) throws CheckedUnsupportedOperationException;

    /**
     * Returns the number of dimensions of this mark.
     *
     * @return the number of dimensions
     */
    public abstract int numberDimensions();

    /**
     * Returns the center point of the mark.
     *
     * @return the center point as a Point3d
     */
    public abstract Point3d centerPoint();

    /**
     * Calculates the bounding box for a specific region of the mark.
     *
     * @param dimensions the dimensions of the space
     * @param regionID the ID of the region
     * @return the bounding box
     */
    public abstract BoundingBox box(Dimensions dimensions, int regionID);

    /**
     * Calculates the bounding box for all regions of the mark.
     *
     * @param dimensions the dimensions of the space
     * @return the bounding box
     */
    public abstract BoundingBox boxAllRegions(Dimensions dimensions);

    /**
     * Checks if this mark has the same ID as another object.
     *
     * @param obj the object to compare with
     * @return true if the IDs are the same, false otherwise
     */
    public boolean equalsID(Object obj) {
        if (obj instanceof Mark) {
            Mark mark = (Mark) obj;
            return this.id == mark.id;
        }
        return false;
    }

    /**
     * Checks if this mark is deeply equal to another mark.
     *
     * @param mark the mark to compare with
     * @return true if the marks are deeply equal, false otherwise
     */
    public boolean equalsDeep(Mark mark) {
        return equalsID(mark);
    }

    /**
     * Creates an ObjectMask representation of the mark.
     *
     * @param dimensions the dimensions of the space
     * @param region the region membership to consider
     * @param binaryValues the binary values to use for encoding
     * @return an ObjectMask representing the mark
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

    /**
     * Returns a string identifier for the mark.
     *
     * @return a string representation of the mark's ID
     */
    public String identifier() {
        return String.format("id=%10d", id);
    }

    /**
     * Gets the identifier of the mark.
     *
     * @return the mark's ID
     */
    public int getIdentifier() {
        return id;
    }

    /**
     * Sets the identifier of the mark.
     *
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
    }
}
