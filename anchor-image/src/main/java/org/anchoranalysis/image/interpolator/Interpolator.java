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

package org.anchoranalysis.image.interpolator;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

public interface Interpolator {

    /**
     * Interpolates from {@code voxelsSource} to {@code voxelsDestination} for unsigned 8-bit
     * buffers.
     *
     * <p>Both buffers must be 2-dimensional, not 3-dimensional.
     *
     * @param voxelsSource voxels to interpolate from
     * @param voxelsDestination voxels to write the interpolated values into
     * @param extentSource extent corresponding to {@code voxelsSource}
     * @param extentDestination extent corresponding to {@code extentDestination}
     * @return the destination buffer (either as passed, or a new one that was created)
     */
    VoxelBuffer<ByteBuffer> interpolateByte(
            VoxelBuffer<ByteBuffer> voxelsSource,
            VoxelBuffer<ByteBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination);

    /**
     * Interpolates from {@code voxelsSource} to {@code voxelsDestination} for unsigned 16-bit
     * buffers.
     *
     * <p>Both buffers must be 2-dimensional, not 3-dimensional.
     *
     * @param voxelsSource voxels to interpolate from
     * @param voxelsDestination voxels to write the interpolated values into
     * @param extentSource extent corresponding to {@code voxelsSource}
     * @param extentDestination extent corresponding to {@code extentDestination}
     * @return the destination buffer (either as passed, or a new one that was created)
     */
    VoxelBuffer<ShortBuffer> interpolateShort(
            VoxelBuffer<ShortBuffer> voxelsSource,
            VoxelBuffer<ShortBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination);

    /**
     * Returns TRUE if it's possible for values to be created after interpolation that aren't found
     * in the input-image. Returns the destination buffer (either as passed, or a new one that was
     * created)
     *
     * @return
     */
    boolean isNewValuesPossible();
}
