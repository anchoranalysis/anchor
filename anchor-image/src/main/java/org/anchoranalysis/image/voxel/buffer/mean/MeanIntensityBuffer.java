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
package org.anchoranalysis.image.voxel.buffer.mean;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;

public abstract class MeanIntensityBuffer<T extends Buffer> {

    private VoxelBox<T> flatVoxelBox;
    private VoxelBox<FloatBuffer> sumVoxelBox;
    private int cntVoxelBox = 0;

    /** Simple constructor since no preprocessing is necessary. */
    public MeanIntensityBuffer(VoxelBoxFactoryTypeBound<T> flatType, Extent srcExtent) {
        Extent flattened = srcExtent.flattenZ();
        flatVoxelBox = flatType.create(flattened);
        sumVoxelBox = VoxelBoxFactory.getFloat().create(flattened);
    }

    public void projectSlice(T pixels) {

        int maxIndex = volumeXY();
        for (int i = 0; i < maxIndex; i++) {
            processPixel(pixels, i);
        }
        finalizeBuffer();
        cntVoxelBox++;
    }

    protected abstract void processPixel(T pixels, int index);

    protected abstract void finalizeBuffer();

    /** Increments a particular offset in the sum bufffer by a certain amount */
    protected void incrSumBuffer(int index, int toAdd) {
        FloatBuffer sumBuffer = sumBuffer();
        sumBuffer.put(index, sumBuffer.get(index) + toAdd);
    }

    protected FloatBuffer sumBuffer() {
        return sumVoxelBox.getPixelsForPlane(0).buffer();
    }

    protected T flatBuffer() {
        return flatVoxelBox.getPixelsForPlane(0).buffer();
    }

    protected int count() {
        return cntVoxelBox;
    }

    /** How many pixels in an XY slice */
    protected int volumeXY() {
        return flatVoxelBox.extent().getVolumeXY();
    }

    public VoxelBox<T> getFlatBuffer() {
        return flatVoxelBox;
    }
}
