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

package org.anchoranalysis.image.voxel.buffer.slice;

import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFactory;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.spatial.box.Extent;


/**
 * Implementation of {@link SliceBufferIndex} with voxels of type <b>unsigned int</b>.
 * 
 * @author Owen Feehan
 *
 */
public class FromInt implements SliceBufferIndex<UnsignedIntBuffer> {

    private final VoxelBuffer<UnsignedIntBuffer>[] buffer;
    private final Extent extent;

    // START FACTORY METHODS
    /**
     * Create a buffer of a particular size, that <b>has been</b> initialized.
     * 
     * @param extent the size of buffer to create.
     * @return the newly created buffer.
     */
    public static SliceBufferIndex<UnsignedIntBuffer> createInitialized(Extent extent) {
        FromInt p = new FromInt(extent);
        p.initialize();
        return p;
    }

    /**
     * Create a buffer of a particular size, that <b>has not been</b> initialized.
     * 
     * @param extent the size of buffer to create.
     * @return the newly created buffer.
     */
    public static SliceBufferIndex<UnsignedIntBuffer> createUninitialized(Extent extent) {
        return new FromInt(extent);
    }
    // END FACTORY METHODS

    private FromInt(Extent extent) {
        assert (extent.z() > 0);

        this.extent = extent;

        buffer = VoxelBufferFactory.allocateUnsignedIntArray(extent.z());
    }

    private void initialize() {
        int volumeXY = extent.areaXY();
        for (int z = 0; z < extent.z(); z++) {
            buffer[z] = VoxelBufferFactory.allocateUnsignedInt(volumeXY);
        }
    }

    @Override
    public void replaceSlice(int z, VoxelBuffer<UnsignedIntBuffer> pixels) {
        pixels.buffer().clear();
        buffer[z] = pixels;
    }

    @Override
    public VoxelBuffer<UnsignedIntBuffer> slice(int z) {
        VoxelBuffer<UnsignedIntBuffer> buf = buffer[z];
        buf.buffer().clear();
        return buf;
    }

    @Override
    public Extent extent() {
        return extent;
    }
}
