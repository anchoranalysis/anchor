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

import java.nio.ShortBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferShort;

public class PixelsFromShortBufferArr implements PixelsForPlane<ShortBuffer> {

    private final VoxelBuffer<ShortBuffer>[] buffer;
    private final Extent extent;

    private PixelsFromShortBufferArr(Extent extent) {
        assert (extent.getZ() > 0);

        this.extent = extent;

        buffer = new VoxelBufferShort[extent.getZ()];
    }

    private void init() {
        int volumeXY = extent.getVolumeXY();
        for (int z = 0; z < extent.getZ(); z++) {
            buffer[z] = VoxelBufferShort.allocate(volumeXY);
            assert (buffer[z].buffer().array().length == volumeXY);
        }
    }

    // START FACTORY METHODS
    public static PixelsFromShortBufferArr createInitialised(Extent extent) {
        PixelsFromShortBufferArr p = new PixelsFromShortBufferArr(extent);
        p.init();
        return p;
    }

    public static PixelsFromShortBufferArr createEmpty(Extent extent) {
        return new PixelsFromShortBufferArr(extent);
    }
    // END FACTORY METHODS

    @Override
    public void setPixelsForPlane(int z, VoxelBuffer<ShortBuffer> pixels) {
        pixels.buffer().clear();
        buffer[z] = pixels;
        assert (pixels.buffer().array().length == extent.getVolumeXY());
    }

    @Override
    public VoxelBuffer<ShortBuffer> getPixelsForPlane(int z) {
        VoxelBuffer<ShortBuffer> buf = buffer[z];
        buf.buffer().clear();
        return buf;
    }

    @Override
    public Extent extent() {
        return extent;
    }
}
