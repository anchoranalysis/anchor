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
package org.anchoranalysis.image.voxel.extracter;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.extracter.predicate.VoxelsPredicate;
import org.anchoranalysis.image.voxel.iterator.MinMaxRange;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.resizer.VoxelsResizer;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent; // NOSONAR
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Provides methods to read/copy/duplicate regions of voxels.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
public interface VoxelsExtracter<T> {

    /**
     * Gets the value of one particular voxel.
     *
     * <p>Note that this provides <b>very slow access</b>, compared to iterating through slice
     * buffers, so <b>use sparingly</b>.
     *
     * <p>The <i>Z</i>-coordinate is assumed to be 0.
     *
     * @param x coordinate of voxel in <i>X</i>-dimension.
     * @param y coordinate of voxel in <i>Y</i>-dimension.
     * @return the value of a voxel (converted into an {@code int}).
     */
    default int voxel(int x, int y) {
        return voxel(x, y, 0);
    }

    /**
     * Gets the value of one particular voxel.
     *
     * <p>Note that this provides <b>very slow access</b>, compared to iterating through slice
     * buffers, so <b>use sparingly</b>.
     *
     * @param x coordinate of voxel in <i>X</i>-dimension.
     * @param y coordinate of voxel in <i>Y</i>-dimension.
     * @param z coordinate of voxel in <i>Z</i>-dimension.
     * @return the value of a voxel (converted into an {@code int}).
     */
    default int voxel(int x, int y, int z) {
        return voxel(new Point3i(x, y, z));
    }

    /**
     * Gets the value of one particular voxel.
     *
     * <p>Note that this provides <b>very slow access</b>, compared to iterating through slice
     * buffers, so <b>use sparingly</b>.
     *
     * @param point coordinates.
     * @return the value of a voxel (converted into an {@code int}).
     */
    int voxel(ReadableTuple3i point);

    /**
     * Creates a new {@link Voxels} with only particular slice.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param sliceIndex index of slice in z-dimension.
     * @return a channel containing the slice (reusing the existing voxel buffers).
     */
    Voxels<T> slice(int sliceIndex);

    /**
     * A (sub-)region of the voxels.
     *
     * <p>The region may some smaller portion of the voxels, or the voxels in their entirety.
     *
     * <p>It should <b>never</b> be larger than the voxels.
     *
     * <p>Depending on policy, an the existing box will be reused if possible (if the region
     * requested is equal to the box as a whole), useful to avoid unnecessary new memory allocation.
     *
     * <p>If {@code reuseIfPossible} is false, it is guaranteed that a new voxels will always be
     * created.
     *
     * @param box a bounding-box indicating the regions desired (not be larger than the extent)
     * @param reuseIfPossible if true the existing box will be reused if possible,, otherwise a new
     *     box is always created.
     * @return voxels corresponding to the requested region, either newly-created or reused
     */
    Voxels<T> region(BoundingBox box, boolean reuseIfPossible);

    /**
     * Copies a bounding-box area to another {@link Voxels}.
     *
     * <p>{@code from} and {@code destinationBox} must have identically-sized {@link Extent}s.
     *
     * @param from box to copy from (relative to the current voxels).
     * @param voxelsDestination where to copy into.
     * @param destinationBox box to copy into (relative to {@code voxelsDestination}).
     */
    void boxCopyTo(BoundingBox from, Voxels<T> voxelsDestination, BoundingBox destinationBox);

    /**
     * Copies an area corresponding to an object-mask to another {@link Voxels}.
     *
     * <p>Only copies voxels if part of an object, otherwise voxels in the destination-buffer are
     * not changed.
     *
     * <p>{@code from}'s bounding-box and {@code destinationBox} must have identically-sized {@link
     * Extent}s.
     *
     * @param from only copies voxels which correspond to an <i>on</i> voxels in the object-mask.
     * @param voxelsDestination where to copy into.
     * @param destinationBox box to copy into (relative to {@code voxelsDestination}).
     */
    void objectCopyTo(ObjectMask from, Voxels<T> voxelsDestination, BoundingBox destinationBox);

    /**
     * Creates a new voxels that are a resized version of the current voxels (only in X and Y
     * dimensions), interpolating as needed.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param sizeX new size in X dimension.
     * @param sizeY new size in Y dimension.
     * @param resizer an interpolator for resizing voxels.
     * @return newly created voxels of specified size containing interpolated voxels from the
     *     current voxels.
     */
    Voxels<T> resizedXY(int sizeX, int sizeY, VoxelsResizer resizer);

    /**
     * A <i>maximum</i> intensity projection of all slices
     *
     * @return voxels with newly-created buffers containing projection (identical in XY dimensions
     *     but with a single slice).
     */
    Voxels<T> projectMax();

    /**
     * A <i>mean</i> intensity projection of all slices.
     *
     * @return voxels with newly-created buffers containing projection (identical in XY dimensions
     *     but with a single slice).
     */
    Voxels<T> projectMean();

    /**
     * Operations on whether particular voxels are equal to a particular value.
     *
     * @param equalToValue
     * @return a newly instantiated object to perform queries on voxels who fulfill the above
     *     condition.
     */
    VoxelsPredicate voxelsEqualTo(int equalToValue);

    /**
     * Operations on whether particular voxels are greater than a threshold (but not equal to).
     *
     * @param threshold voxel-values greater than this threshold are included.
     * @return a newly instantiated object to perform queries on voxels who fulfill the above
     *     condition.
     */
    VoxelsPredicate voxelsGreaterThan(int threshold);

    /**
     * Finds the minimum-value of any voxel and rounding down (floor) to the nearest long.
     *
     * <p>The computational cost of the operation is {@code O(n)} in the number of voxels. The
     * result is not cached.
     *
     * @return the minimum-value.
     */
    long voxelWithMinIntensity();

    /**
     * Finds the maximum-value of any voxel and rounding up (ceiling) to the nearest long.
     *
     * <p>The computational cost of the operation is {@code O(n)} in the number of voxels. The
     * result is not cached.
     *
     * @return the maximum-value.
     */
    long voxelWithMaxIntensity();

    /**
     * Finds the minimum-value <b>and</b> maximum of any voxel.
     *
     * <p>The minimum is rounded down (floor) to the nearest long.
     *
     * <p>The maximum is rounded up (ceil) to the nearest long.
     *
     * <p>This is efficient than calling {@link #voxelWithMinIntensity()} and {@link
     * #voxelWithMaxIntensity()} separately.
     *
     * <p>The computational cost of the operation is {@code O(n)} in the number of voxels. The
     * result is not cached.
     *
     * @return the minimum- and maximum values.
     */
    MinMaxRange voxelsWithMinMaxIntensity();
}
