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
package org.anchoranalysis.image.voxel.arithmetic;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.function.IntFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.extent.Extent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoxelsArithmeticFactory {

    /**
     * Create voxels-arithmethic for {@link ByteBuffer}
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer)
     * @return a newly-created voxels-arithmetic
     */
    public static VoxelsArithmetic createByte(
            Extent extent, IntFunction<ByteBuffer> bufferForSlice) {
        return new ByteImplementation(extent, bufferForSlice);
    }

    /**
     * Create voxels-arithmethic for {@link ShortBuffer}
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer)
     * @return a newly-created voxels-arithmetic
     */
    public static VoxelsArithmetic createShort(
            Extent extent, IntFunction<ShortBuffer> bufferForSlice) {
        return new ShortImplementation(extent, bufferForSlice);
    }

    /**
     * Create voxels-arithmethic for {@link FloatBuffer}
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer)
     * @return
     */
    public static VoxelsArithmetic createFloat(
            Extent extent, IntFunction<FloatBuffer> bufferForSlice) {
        return new FloatImplementation(extent, bufferForSlice);
    }

    /**
     * Create voxels-arithmethic for {@link IntBuffer}
     *
     * @param extent the extent of the voxels on which arithmetic is to be performed
     * @param bufferForSlice a buffer for a particular slice index (set at the initial position in
     *     the buffer)
     * @return
     */
    public static VoxelsArithmetic createInt(Extent extent, IntFunction<IntBuffer> bufferForSlice) {
        return new IntImplementation(extent, bufferForSlice);
    }
}
