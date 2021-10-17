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

package org.anchoranalysis.image.voxel.kernel;

import java.util.Optional;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Caches a small number of slices around which we wish to work, so the memory is efficiently
 * accessed.
 *
 * @author Owen Feehan
 */
public class LocalSlices implements BufferRetriever {

    private final byte[][] array;
    private final int shift;

    /**
     * Create with focus around a particular slice.
     * 
     * @param z index of the slice in the z-dimension.
     * @param windowSize how big the windows is across the z-dimension for storing slices.
     * @param voxels the voxels whose z-dimension is traversed for slices.
     */
    public LocalSlices(int z, int windowSize, Voxels<UnsignedByteBuffer> voxels) {
        array = new byte[windowSize][];

        shift = ((windowSize - 1) / 2);

        for (int i = 0; i < windowSize; i++) {

            int rel = z + i - shift;

            if (rel >= 0 && rel < voxels.extent().z()) {
                array[i] = voxels.sliceBuffer(rel).array();
            } else {
                array[i] = null;
            }
        }
    }

    @Override
    public Optional<UnsignedByteBuffer> getLocal(int relativeZIndex) {
        int index = relativeZIndex + shift;
        if (index >= 0 && index < array.length) {
            byte[] slice = array[index];
            return Optional.ofNullable(slice).map(UnsignedByteBuffer::wrapRaw);
        } else {
            return Optional.empty();
        }
    }
}
