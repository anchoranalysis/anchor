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

import org.anchoranalysis.image.voxel.buffer.SlidingBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Like {@link ProcessVoxelNeighborAbsolute} but additionally includes a {@link SlidingBuffer}.
 * 
 * @author Owen Feehan
 * @param <T> result-type that is collected
 */
public abstract class ProcessVoxelNeighborAbsoluteWithSlidingBuffer<T>
        implements ProcessVoxelNeighborAbsolute<T> {

    private final SlidingBuffer<?> slidingBuffer;
    private final Extent extent;
    
    /** The change in the Z-dimension to reach this neighbor relative to the source coordinate. */
    protected int zChange;
    
    /** The intensity value of the source voxel. */
    protected int sourceValue;

    private VoxelBuffer<?> buffer;
    
    private int sourceOffsetXY;

    /**
     * Creates for a particular sliding buffer.
     * 
     * @param slidingBuffer the buffer.
     */
    protected ProcessVoxelNeighborAbsoluteWithSlidingBuffer(SlidingBuffer<?> slidingBuffer) {
        this.slidingBuffer = slidingBuffer;
        this.extent = slidingBuffer.extent();
    }

    @Override
    public void initSource(int sourceValue, int sourceOffsetXY) {
        this.sourceOffsetXY = sourceOffsetXY;
        this.sourceValue = sourceValue;
    }

    @Override
    public void notifyChangeZ(int zChange, int z) {
        buffer = slidingBuffer.bufferRelative(zChange);
        this.zChange = zChange;
    }
    
    /**
     * The size of the associated {@link SlidingBuffer}.
     * 
     * @return the size.
     */
    public Extent extent() {
        return extent;
    }

    /**
     * Calculates the offset in the buffer for a voxel, indicated by its relative-change in position.
     * 
     * @param xChange the relative change in the X-dimension, compared to the current buffer position.
     * @param yChange the relative change in the Y-dimension, compared to the current buffer position.
     * @return the position in the buffer where the value is located.
     */
    protected int changedOffset(int xChange, int yChange) {
        return sourceOffsetXY + extent.offset(xChange, yChange);
    }

    /**
     * Get a value from the buffer at a particular <b>absolute</b> position.
     * 
     * @param index the absolute position in the buffer.
     * @return the value as an <i>unsigned int</i>.
     */
    protected int getInt(int index) {
        return buffer.getInt(index);
    }

    /**
     * Get a value from the buffer at a particular <b>relative</b> position.
     * 
     * @param xChange the relative change in the X-dimension, compared to the current buffer position.
     * @param yChange the relative change in the Y-dimension, compared to the current buffer position.
     * @return the value as an <i>unsigned int</i>.
     */
    protected int getInt(int xChange, int yChange) {
        return buffer.getInt(changedOffset(xChange, yChange));
    }

    /**
     * Assign a value to the buffer at a particular <b>absolute</b> position.
     * 
     * @param index the absolute position in the buffer.
     * @param valueToAssign the value to assign.
     */
    protected void putInt(int index, int valueToAssign) {
        buffer.putInt(index, valueToAssign);
    }
}
