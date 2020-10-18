/*-
 * #%L
 * anchor-imagej
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
package org.anchoranalysis.io.imagej.convert;

import ij.process.ImageProcessor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferWrap;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;

/**
 * Converts a {@link ImageProcessor} to a voxel-buffer of particular data-type.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvertToVoxelBuffer {

    /**
     * Convert a {@link ImageProcessor} to {@code VoxelBuffer<UnsignedByteBuffer>}
     *
     * @param processor the processor to convert
     * @return a voxel-buffer that reuses the memory of the processor (no duplication)
     */
    public static VoxelBuffer<UnsignedByteBuffer> asByte(ImageProcessor processor) {
        byte[] arr = (byte[]) processor.getPixels();
        return VoxelBufferWrap.unsignedByteArray(arr);
    }

    /**
     * Convert a {@link ImageProcessor} to {@code VoxelBuffer<UnsignedShortBuffer>}
     *
     * @param processor the processor to convert
     * @return a voxel-buffer that reuses the memory of the processor (no duplication)
     */
    public static VoxelBuffer<UnsignedShortBuffer> asShort(ImageProcessor processor) {
        short[] arr = (short[]) processor.getPixels();
        return VoxelBufferWrap.unsignedShortArray(arr);
    }
}
