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

package org.anchoranalysis.image.voxel.iterator.neighbor;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

/**
 * @author Owen Feehan
 * @param <T> result-type that is collected
 */
public abstract class ProcessVoxelNeighborAbsoluteWithSlidingBuffer<T>
        implements ProcessVoxelNeighborAbsolute<T> {

    private final SlidingBuffer<?> rbb;
    private final Extent extent;

    private VoxelBuffer<?> buffer;
    protected int zChange;
    protected int sourceVal;
    private int sourceOffsetXY;

    protected ProcessVoxelNeighborAbsoluteWithSlidingBuffer(SlidingBuffer<?> rbb) {
        this.rbb = rbb;
        this.extent = rbb.extent();
    }

    @Override
    public void initSource(int sourceVal, int sourceOffsetXY) {
        this.sourceOffsetXY = sourceOffsetXY;
        this.sourceVal = sourceVal;
    }

    @Override
    public void notifyChangeZ(int zChange, int z) {
        buffer = rbb.bufferRel(zChange);
        this.zChange = zChange;
    }

    protected int offset(int xChange, int yChange) {
        return extent.offset(xChange, yChange);
    }

    protected int changedOffset(int xChange, int yChange) {
        return sourceOffsetXY + extent.offset(xChange, yChange);
    }

    protected int getInt(int index) {
        return buffer.getInt(index);
    }

    protected void putInt(int index, int valToAssign) {
        buffer.putInt(index, valToAssign);
    }

    protected int getInt(int xChange, int yChange) {
        return buffer.getInt(changedOffset(xChange, yChange));
    }

    public Extent extent() {
        return extent;
    }
}
