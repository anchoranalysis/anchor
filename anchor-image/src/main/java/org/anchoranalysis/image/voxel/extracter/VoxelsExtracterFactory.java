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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.voxel.Voxels;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoxelsExtracterFactory {

    /**
     * Create voxels-extracter for {@link ByteBuffer}
     *
     * @param voxels the voxels to extract from
     * @return a newly created extracter
     */
    public static VoxelsExtracter<ByteBuffer> createByte(Voxels<ByteBuffer> voxels) {
        return new ByteImplementation(voxels);
    }

    /**
     * Create voxels-extracter for {@link ShortBuffer}
     *
     * @param voxels the voxels to extract from
     * @return a newly created extracter
     */
    public static VoxelsExtracter<ShortBuffer> createShort(Voxels<ShortBuffer> voxels) {
        return new ShortImplementation(voxels);
    }

    /**
     * Create voxels-extracter for {@link FloatBuffer}
     *
     * @param voxels the voxels to extract from
     * @return a newly created extracter
     */
    public static VoxelsExtracter<FloatBuffer> createFloat(Voxels<FloatBuffer> voxels) {
        return new FloatImplementation(voxels);
    }

    /**
     * Create voxels-extracter for {@link IntBuffer}
     *
     * @param voxels the voxels to extract from
     * @return a newly created extracter
     */
    public static VoxelsExtracter<IntBuffer> createInt(Voxels<IntBuffer> voxels) {
        return new IntImplementation(voxels);
    }

    /**
     * Projects a {@link VoxelsExtracter} to a corner in a larger global space
     *
     * <p>Coordinates are translated appropriately for any calls from the larger global space to the
     * space on which {@code delegate} is defined.
     *
     * @param <T> buffer-type
     * @param corner the corner at which the voxels referred to by {@code delegate} are considered
     *     to exist
     * @param delegate the delegate
     * @return an extracter that performs translation from global-coordinates to the coordinate
     *     system expected by the delegate
     */
    public static <T extends Buffer> VoxelsExtracter<T> atCorner(
            ReadableTuple3i corner, VoxelsExtracter<T> delegate) {
        return new AtCorner<>(corner, delegate);
    }
}
