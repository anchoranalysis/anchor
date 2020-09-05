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

package org.anchoranalysis.image.voxel.sliceindex;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFloat;

public class FromFloat implements SliceBufferIndex<FloatBuffer> {

    private final VoxelBuffer<FloatBuffer>[] buffer;
    private final Extent extent;

    // START FACTORY METHODS
    public static SliceBufferIndex<FloatBuffer> createInitialized(Extent extent) {
        FromFloat p = new FromFloat(extent);
        p.init();
        return p;
    }

    public static SliceBufferIndex<FloatBuffer> createUninitialized(Extent extent) {
        return new FromFloat(extent);
    }
    // END FACTORY METHODS
    
    private FromFloat(Extent extent) {
        this.extent = extent;
        buffer = new VoxelBufferFloat[extent.z()];
    }

    private void init() {
        int volumeXY = extent.volumeXY();
        for (int z = 0; z < extent.z(); z++) {
            buffer[z] = VoxelBufferFloat.allocate(volumeXY);
        }
    }

    @Override
    public void replaceSlice(int z, VoxelBuffer<FloatBuffer> pixels) {
        pixels.buffer().clear();
        buffer[z] = pixels;
    }

    @Override
    public VoxelBuffer<FloatBuffer> slice(int z) {
        VoxelBuffer<FloatBuffer> buf = buffer[z];
        buf.buffer().clear();
        return buf;
    }

    @Override
    public Extent extent() {
        return extent;
    }
}
