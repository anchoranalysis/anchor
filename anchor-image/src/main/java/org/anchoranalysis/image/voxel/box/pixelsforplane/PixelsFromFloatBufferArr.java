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
/* (C)2020 */
package org.anchoranalysis.image.voxel.box.pixelsforplane;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFloat;

public class PixelsFromFloatBufferArr implements PixelsForPlane<FloatBuffer> {

    private final VoxelBuffer<FloatBuffer>[] buffer;
    private final Extent extent;

    private PixelsFromFloatBufferArr(Extent extent) {
        assert (extent.getZ() > 0);

        this.extent = extent;

        buffer = new VoxelBufferFloat[extent.getZ()];
    }

    private void init() {
        int volumeXY = extent.getVolumeXY();
        for (int z = 0; z < extent.getZ(); z++) {
            buffer[z] = VoxelBufferFloat.allocate(volumeXY);
        }
    }

    // START FACTORY METHODS
    public static PixelsFromFloatBufferArr createInitialised(Extent extent) {
        PixelsFromFloatBufferArr p = new PixelsFromFloatBufferArr(extent);
        p.init();
        return p;
    }

    public static PixelsFromFloatBufferArr createEmpty(Extent extent) {
        return new PixelsFromFloatBufferArr(extent);
    }
    // END FACTORY METHODS

    @Override
    public void setPixelsForPlane(int z, VoxelBuffer<FloatBuffer> pixels) {
        pixels.buffer().clear();
        buffer[z] = pixels;
    }

    @Override
    public VoxelBuffer<FloatBuffer> getPixelsForPlane(int z) {
        VoxelBuffer<FloatBuffer> buf = buffer[z];
        buf.buffer().clear();
        return buf;
    }

    @Override
    public Extent extent() {
        return extent;
    }
}
