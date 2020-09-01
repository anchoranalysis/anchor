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

import java.nio.Buffer;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsPredicate;

/**
 * Provides method to read/copy/duplicate portions of voxels
 *
 * @author Owen Feehan
 */
public interface VoxelsExtracter<T extends Buffer> {

    /**
     * Gets the value of one particular value
     *
     * <p>Note that this provides <b>very slow access</b>, compared to iterating through slice
     * buffers, so <b>use sparingly</b>.
     *
     * @param point coordinates
     * @return the value of a voxel (converted into an {@code int})
     */
    int voxel(ReadableTuple3i point);

    /**
     * Creates a new {@link Voxels} with only particular slice
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param sliceIndex index of slice in z-dimension
     * @return a channel containing the slice (reusing the existing voxel buffers)
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
     * <p>If {@code reuseIfPossible} is FALSE, it is guaranteed that a new voxels will always be
     * created.
     *
     * @param box a bounding-box indicating the regions desired (not be larger than the extent)
     * @param reuseIfPossible if TRUE the existing box will be reused if possible,, otherwise a new
     *     box is always created.
     * @return voxels corresponding to the requested region, either newly-created or reused
     */
    Voxels<T> region(BoundingBox box, boolean reuseIfPossible);

    /**
     * Copies a bounding-box area to another {@link Voxels}
     *
     * <p>{@code from} and {@code destinationBox} must have identically-sized extents.
     *
     * @param from box to copy from (relative to the current voxels)
     * @param voxelsDestination where to copy into
     * @param destinationBox box to copy into (relative to {@code voxelsDestination})
     */
    void boxCopyTo(BoundingBox from, Voxels<T> voxelsDestination, BoundingBox destinationBox);

    /**
     * Copies an area corresponding to an object-mask to another {@link Voxels}
     *
     * <p>Only copies voxels if part of an object, otherwise voxels in the destination-buffer are
     * not changed.
     *
     * <p>{@code from}'s bounding-box and {@code destinationBox} must have identically-sized
     * extents.
     *
     * @param from only copies voxels which correspond to an ON voxels in the object-mask
     * @param voxelsDestination where to copy into
     * @param destinationBox box to copy into (relative to {@code voxelsDestination})
     */
    void objectCopyTo(ObjectMask from, Voxels<T> voxelsDestination, BoundingBox destinationBox);

    /**
     * Creates a new voxels that are a resized version of the current voxels (only in X and Y
     * dimensions), interpolating as needed.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param sizeX new size in X dimension
     * @param sizeY new size in Y dimension
     * @param interpolator means to interpolate voxels as they are resized.
     * @return newly created voxels of specified size containing interpolated voxels from the
     *     current voxels.
     */
    Voxels<T> resizedXY(int sizeX, int sizeY, Interpolator interpolator);

    /**
     * A <i>maximum</i> intensity projection of all slices
     *
     * @return voxels with newly-created buffers containing projection (identical in XY dimensions
     *     but with a single slice)
     */
    Voxels<T> projectMax();

    /**
     * A <i>mean</i> intensity projection of all slices
     *
     * @return voxels with newly-created buffers containing projection (identical in XY dimensions
     *     but with a single slice)
     */
    Voxels<T> projectMean();

    /**
     * Operations on whether particular voxels are equal to a particular value
     *
     * @param equalToValue
     * @return a newly instantiated object to perform queries to this voxels object as described
     *     above
     */
    VoxelsPredicate voxelsEqualTo(int equalToValue);

    /**
     * Operations on whether particular voxels are greater than a treshold (but not equal to)
     *
     * @param threshold voxel-values greater than this threshold are included
     * @return a newly instantiated object to perform queries to this voxels object as described
     *     above
     */
    VoxelsPredicate voxelsGreaterThan(int threshold);

    /**
     * Finds the maximum-value of any voxel and rounds up (ceiling) to nearest integer
     *
     * @return the maximum-value
     */
    int voxelWithMaxIntensity();
}
