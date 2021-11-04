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
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Implementation of {@link SliceBufferIndex} with voxels of type <b>unsigned short</b>.
 *
 * @author Owen Feehan
 */
public class FromShort implements SliceBufferIndex<UnsignedShortBuffer> {

    private final VoxelBuffer<UnsignedShortBuffer>[] buffer;
    private final Extent extent;

    // START FACTORY METHODS
    /**
     * Create a buffer of a particular size, that <b>has been</b> initialized.
     *
     * @param extent the size of buffer to create.
     * @return the newly created buffer.
     */
    public static SliceBufferIndex<UnsignedShortBuffer> createInitialized(Extent extent) {
        FromShort p = new FromShort(extent);
        p.initialize();
        return p;
    }

    /**
     * Create a buffer of a particular size, that <b>has not been</b> initialized.
     *
     * @param extent the size of buffer to create.
     * @return the newly created buffer.
     */
    public static SliceBufferIndex<UnsignedShortBuffer> createUninitialized(Extent extent) {
        return new FromShort(extent);
    }
    // END FACTORY METHODS

    private FromShort(Extent extent) {
        assert (extent.z() > 0);

        this.extent = extent;

        buffer = VoxelBufferFactory.allocateUnsignedShortArray(extent.z());
    }

    private void initialize() {
        int volumeXY = extent.areaXY();
        for (int z = 0; z < extent.z(); z++) {
            buffer[z] = VoxelBufferFactory.allocateUnsignedShort(volumeXY);
        }
    }

    @Override
    public void replaceSlice(int z, VoxelBuffer<UnsignedShortBuffer> sliceToAssign) {
        sliceToAssign.buffer().clear();
        buffer[z] = sliceToAssign;
    }

    @Override
    public VoxelBuffer<UnsignedShortBuffer> slice(int z) {
        VoxelBuffer<UnsignedShortBuffer> buf = buffer[z];
        buf.buffer().clear();
        return buf;
    }

    @Override
    public Extent extent() {
        return extent;
    }
}
