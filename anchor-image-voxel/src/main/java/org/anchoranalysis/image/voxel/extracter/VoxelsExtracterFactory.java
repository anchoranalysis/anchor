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

import java.nio.FloatBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Creates {@link VoxelsExtracter} corresponding to {@link Voxels} of differing data-type.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoxelsExtracterFactory {

    /**
     * Create voxels-extracter for {@link UnsignedByteBuffer}.
     *
     * @param voxels the voxels to extract from.
     * @return a newly created extracter.
     */
    public static VoxelsExtracter<UnsignedByteBuffer> createUnsignedByte(
            Voxels<UnsignedByteBuffer> voxels) {
        return new UnsignedByteImplementation(voxels);
    }

    /**
     * Create voxels-extracter for {@link UnsignedShortBuffer}.
     *
     * @param voxels the voxels to extract from.
     * @return a newly created extracter.
     */
    public static VoxelsExtracter<UnsignedShortBuffer> createUnsignedShort(
            Voxels<UnsignedShortBuffer> voxels) {
        return new UnsignedShortImplementation(voxels);
    }

    /**
     * Create voxels-extracter for {@link UnsignedIntBuffer}.
     *
     * @param voxels the voxels to extract from.
     * @return a newly created extracter.
     */
    public static VoxelsExtracter<UnsignedIntBuffer> createUnsignedInt(
            Voxels<UnsignedIntBuffer> voxels) {
        return new UnsignedIntImplementation(voxels);
    }

    /**
     * Create voxels-extracter for {@link FloatBuffer}.
     *
     * @param voxels the voxels to extract from.
     * @return a newly created extracter.
     */
    public static VoxelsExtracter<FloatBuffer> createFloat(Voxels<FloatBuffer> voxels) {
        return new FloatImplementation(voxels);
    }

    /**
     * Projects a {@link VoxelsExtracter} to a corner in a larger global space.
     *
     * <p>Coordinates are translated appropriately for any calls from the larger global space to the
     * space on which {@code delegate} is defined.
     *
     * @param <T> buffer-type
     * @param corner the corner at which the voxels referred to by {@code delegate} are considered
     *     to exist.
     * @param delegate the delegate.
     * @return an extracter that performs translation from global-coordinates to the coordinate
     *     system expected by the delegate.
     */
    public static <T> VoxelsExtracter<T> atCorner(
            ReadableTuple3i corner, VoxelsExtracter<T> delegate) {
        return new AtCorner<>(corner, delegate);
    }
}
