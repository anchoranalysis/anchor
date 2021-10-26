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

package org.anchoranalysis.image.voxel.interpolator;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.spatial.box.Extent;
import com.google.common.base.Preconditions;

/**
 * Copies voxels while performing interpolation.
 * 
 * @author Owen Feehan
 *
 */
public interface Interpolator {

    /**
     * Copies voxels slice-by-slice from {@code source} to {@code destination} performing necessary interpolation.
     * 
     * <p>Note that interpolation only occurs in the XY plane, and the number of Z-slices should be identical for
     * both {@code source} and {@code destination}.
     * 
     * @param source the voxels to copy from.
     * @param destination the voxels to copy interpolated-values into, which may differ in size in the XY dimensions.
     */
    default void interpolate(
            VoxelsWrapper source, VoxelsWrapper destination) {

        Extent extentSource = source.any().extent();
        Extent extentTarget = destination.any().extent();
        
        Preconditions.checkArgument(extentSource.z()==extentTarget.z());

        TransferViaSpecificType<?> transfer = InterpolateHelper.createTransfer(source, destination);

        for (int z = 0; z < extentSource.z(); z++) {

            transfer.assignSlice(z);
            if (extentSource.x() == extentTarget.x() && extentSource.y() == extentTarget.y()) {
                transfer.transferCopyTo(z);
            } else {
                if (extentSource.x() != 1 && extentSource.y() != 1) {
                    // We only bother to interpolate when we have more than a single pixel in both
                    // directions
                    // And in this case, some of the interpolation algorithms would crash.
                    transfer.transferTo(z, this);
                } else {
                    transfer.transferTo(z, InterpolatorFactory.getInstance().noInterpolation());
                }
            }
        }
        Preconditions.checkArgument(destination.slice(0).capacity() == extentTarget.areaXY());
    }
    
    /**
     * Interpolates from {@code voxelsSource} to {@code voxelsDestination} for unsigned 8-bit
     * buffers.
     *
     * <p>Both buffers must be 2-dimensional, not 3-dimensional.
     *
     * @param voxelsSource voxels to interpolate from.
     * @param voxelsDestination voxels to write the interpolated values into.
     * @param extentSource extent corresponding to {@code voxelsSource}.
     * @param extentDestination extent corresponding to {@code extentDestination}.
     * @return the destination buffer (either as passed, or a new one that was created).
     */
    VoxelBuffer<UnsignedByteBuffer> interpolateByte(
            VoxelBuffer<UnsignedByteBuffer> voxelsSource,
            VoxelBuffer<UnsignedByteBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination);

    /**
     * Interpolates from {@code voxelsSource} to {@code voxelsDestination} for unsigned 16-bit
     * buffers.
     *
     * <p>Both buffers must be 2-dimensional, not 3-dimensional.
     *
     * @param voxelsSource voxels to interpolate from.
     * @param voxelsDestination voxels to write the interpolated values into.
     * @param extentSource extent corresponding to {@code voxelsSource}.
     * @param extentDestination extent corresponding to {@code extentDestination}.
     * @return the destination buffer (either as passed, or a new one that was created).
     */
    VoxelBuffer<UnsignedShortBuffer> interpolateShort(
            VoxelBuffer<UnsignedShortBuffer> voxelsSource,
            VoxelBuffer<UnsignedShortBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination);

    /**
     * Interpolates from {@code voxelsSource} to {@code voxelsDestination} for float buffers.
     *
     * <p>Both buffers must be 2-dimensional, not 3-dimensional.
     *
     * @param voxelsSource voxels to interpolate from.
     * @param voxelsDestination voxels to write the interpolated values into.
     * @param extentSource extent corresponding to {@code voxelsSource}.
     * @param extentDestination extent corresponding to {@code extentDestination}.
     * @return the destination buffer (either as passed, or a new one that was created).
     */
    VoxelBuffer<FloatBuffer> interpolateFloat(
            VoxelBuffer<FloatBuffer> voxelsSource,
            VoxelBuffer<FloatBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination);

    /**
     * Returns true if it's possible for values to be created after interpolation that aren't found
     * in the input-image.
     *
     * @return true if values can be created in the destination buffer that were not found in the source buffer.
     */
    boolean canValueRangeChange();
}
