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

package org.anchoranalysis.image.voxel.buffer;

import java.nio.Buffer;
import org.anchoranalysis.image.histogram.HistogramFactory;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * A buffer of voxel-values, usually corresponding to a single z-slice in {@link Voxels}.
 * 
 * <p>The operations are modelled on the NIO {@link Buffer} classes that can provide
 * the underlying buffers, but parameter {@code T} need not strictly be a sub-class of {@link Buffer}.
 * This is useful for automatically wrapping signed to unsigned values with custom buffers.
 * 
 * @author Owen Feehan
 * @param <T> buffer-type
 */
public abstract class VoxelBuffer<T> {

    public abstract VoxelDataType dataType();

    public abstract T buffer();

    public abstract VoxelBuffer<T> duplicate();

    // Gets the underlying buffer-item converted to an int
    public abstract int getInt(int index);

    public abstract void putInt(int index, int value);

    public abstract void putByte(int index, byte value);

    public abstract int size();
    
    /** 
     * Are there voxels remaining in a buffer?
     * 
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     *
     * @return true if there are voxels remaining in the buffer, false otherwise.
     */
    public abstract boolean hasRemaining();
        
    /**
     * Assigns a new position to the buffer.
     * 
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     * 
     * @param newPosition the offset to assign as position.
     */
    public abstract void position(int newPosition);
    
    /**
     * Is this buffer direct or non-direct?
     * 
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     * 
     * @return true iff the buffer is direct.
     */    
    public abstract boolean isDirect();

    public void transferFromConvert(int destinationIndex, VoxelBuffer<?> source, int sourceIndex) {
        putInt(destinationIndex, source.getInt(sourceIndex));
    }

    @Override
    public String toString() {
        return HistogramFactory.create(this).toString();
    }

    public abstract void transferFrom(int destinationIndex, VoxelBuffer<T> source, int sourceIndex);
}
