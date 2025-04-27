/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.voxel.object;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point2i;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Creates object-masks of a certain shape.
 *
 * <p>The object-masks are entirely filled-in (rectangular to fill bounding-box) or filled-in except
 * single-voxel corners in the X and Y dimensions.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ObjectMaskFixture {

    /** The default size of the created {@link ObjectMask}, if not otherwise specified. */
    public static final Extent DEFAULT_EXTENT = new Extent(40, 50, 15);

    private static final int VOXELS_REMOVED_CORNERS = 4;

    // START REQUIRED ARGUMENTS
    /** Whether to remove single-voxel pixels from corners or not? */
    private final boolean removeCorners;

    /** Width of object-mask. */
    @Getter private final Extent extent;

    // END REQUIRED ARGUMENTS

    /**
     * Creates with a default size of {@link #DEFAULT_EXTENT}.
     *
     * @param removeCorners whether to remove single-voxel pixels from corners or not?
     * @param do3D whether to include a Z axis or not?
     */
    public ObjectMaskFixture(boolean removeCorners, boolean do3D) {
        this(removeCorners, do3D ? DEFAULT_EXTENT : DEFAULT_EXTENT.flattenZ());
    }

    /**
     * Creates an object-mask whose bounding-box corner exists at the origin.
     *
     * @return a newly created object-mask (with a shape as described in the class comment) and with
     *     a bounding box starting at {@code corner}.
     */
    public ObjectMask filledMaskAtOrigin() {
        return filledMask(0, 0);
    }

    /**
     * Creates an object-mask whose bounding-box corner exists at a particular point.
     *
     * @param corner the corner (minimal corner in all dimensions).
     * @return a newly created object-mask (with a shape as described in the class comment) and with
     *     a bounding box starting at {@code corner}.
     */
    public ObjectMask filledMask(Point2i corner) {
        return filledMask(corner.x(), corner.y(), 0);
    }

    /**
     * Creates an object-mask whose bounding-box corner exists at a particular point.
     *
     * @param corner the corner (minimal corner in all dimensions).
     * @return a newly created object-mask (with a shape as described in the class comment) and with
     *     a bounding box starting at {@code corner}.
     */
    public ObjectMask filledMask(Point3i corner) {
        return filledMask(corner.x(), corner.y(), corner.z());
    }

    /**
     * Creates an object-mask whose bounding-box corner exists at a particular point.
     *
     * @param cornerX the corner in X dimension (minimal value of X).
     * @param cornerY the corner in Y dimension (minimal value of Y).
     * @return a newly created object-mask (with a shape as described in the class comment) and with
     *     a bounding box starting at {@code corner}.
     */
    public ObjectMask filledMask(int cornerX, int cornerY) {
        return filledMask(cornerX, cornerY, 0);
    }

    /**
     * Creates an object-mask whose bounding-box corner exists at a particular point.
     *
     * @param cornerX the corner in X dimension (minimal value of X).
     * @param cornerY the corner in Y dimension (minimal value of Y).
     * @param cornerZ the corner in Z dimension (minimal value of Z).
     * @return a newly created object-mask (with a shape as described in the class comment) and with
     *     a bounding box starting at {@code corner}.
     */
    public ObjectMask filledMask(int cornerX, int cornerY, int cornerZ) {
        Point3i corner = new Point3i(cornerX, cornerY, cornerZ);
        ObjectMask object = new ObjectMask(BoundingBox.createReuse(corner, extent));
        object.assignOn().toAll();
        if (removeCorners) {
            removeEachCorner(object);
        }
        return object;
    }

    /**
     * The expected number of voxels in the object-mask.
     *
     * @return the volume.
     */
    public int expectedVolume() {
        return (int) (extent.calculateVolume() - (removedPerSlice(removeCorners) * extent.z()));
    }

    /**
     * The total number of voxels on the surface of the object.
     *
     * @param useZ if true, z is also treated as a dimension in which a surface is indicated. If
     *     false, only surfaces in X and Y dimensions are considered.
     * @return the number of voxels that are expected to appear on the surface.
     */
    public int sizeSurface(boolean useZ) {
        checkNoRemoveCorners();

        // The top and bottom lines in x
        int typicalSlice = multiplyFirstTwoDifferently(extent.y(), extent.x(), 2);

        if (useZ) {
            return multiplyFirstTwoDifferently(extent.z(), extent.areaXY(), typicalSlice);
        } else {
            return typicalSlice * extent.z();
        }
    }

    /**
     * The total number of neighbors voxels.
     *
     * @param useZ if true, z is also treated as a dimension in which neighbors are considered. If
     *     false, only neighbors in X and Y dimensions are considered.
     * @return the number of voxels that are expected to appear on the surface.
     */
    public int numberNeighbors(boolean useZ) {
        checkNoRemoveCorners();

        // The top and bottom lines in x
        int neighbors2D = 2 * (extent.x() + extent.y()) * extent.z();

        if (useZ) {
            return neighbors2D + (2 * extent.areaXY());
        } else {
            return neighbors2D;
        }
    }

    private void checkNoRemoveCorners() {
        if (removeCorners) {
            throw new UnsupportedOperationException(
                    "removeCorners==true is not supported for this method");
        }
    }

    /**
     * A special <i>multiplication</i> that uses a different multiplicand for the first two values
     * (if they exist) and another multiplicand for the remainder.
     *
     * @param number a positive number to be multiplied.
     * @param multiplicandFirstTwo the multiplicand to be applied for the first two "times" of
     *     {@code number}.
     * @param multiplicandRest the multiplicand to be applied for any other "times" of {@code
     *     number}.
     * @return the total sum of all the times a multiplicand is applied.
     */
    private static int multiplyFirstTwoDifferently(
            int number, int multiplicandFirstTwo, int multiplicandRest) {
        Preconditions.checkArgument(number >= 0);
        if (number <= 2) {
            return multiplicandFirstTwo * number;
        } else {
            return (multiplicandFirstTwo * 2) + (number - 2) * multiplicandRest;
        }
    }

    private int removedPerSlice(boolean removeCorners) {
        return removeCorners ? VOXELS_REMOVED_CORNERS : 0;
    }

    private void removeEachCorner(ObjectMask object) {

        BinaryVoxels<UnsignedByteBuffer> binaryValues = object.binaryVoxels();

        Extent maskExtent = object.boundingBox().extent();
        int widthMinusOne = maskExtent.x() - 1;
        int heightMinusOne = maskExtent.y() - 1;

        for (int z = 0; z < maskExtent.z(); z++) {
            binaryValues.setOff(0, 0, z);
            binaryValues.setOff(widthMinusOne, 0, z);
            binaryValues.setOff(0, heightMinusOne, z);
            binaryValues.setOff(widthMinusOne, heightMinusOne, z);
        }
    }
}
