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

import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Processes a point which has been translated (changed) relative to another point - and includes
 * global coordinates and includes an object-mask buffer.
 *
 * @param <T> result-type that can be collected after processing.
 */
public interface ProcessChangedPointAbsoluteMasked<T> {

    /**
     * The value and offset for the source point (around which we process neighbors).
     *
     * <p>This function should always be called before {@link #processPoint}.
     *
     * <p>It can be called repeatedly for different points (resetting state each time).
     *
     * @param sourceValue the value of the source pixel.
     * @param sourceOffsetXY the offset of the source pixel in XY.
     */
    void initSource(int sourceValue, int sourceOffsetXY);

    /**
     * Notifies the processor that there has been a change in z-coordinate.
     *
     * @param zChange the relative change in the Z-dimension (relative to the original coordinate
     *     value).
     * @param z the absolute value in the Z-dimension of the point currently being processed.
     * @param objectMaskBuffer the voxels for the particular z-slice of the object-mask being
     *     processed.
     */
    default void notifyChangeZ(int zChange, int z, UnsignedByteBuffer objectMaskBuffer) {}

    /**
     * Processes a particular point.
     *
     * @param xChange the relative change in the X-dimension (relative to the original coordinate
     *     value).
     * @param yChange the relative change in the Y-dimension (relative to the original coordinate
     *     value).
     * @param x the absolute value in the X-dimension of the point currently being processed.
     * @param y the absolute value in the Y-dimension of the point currently being processed.
     * @param objectMaskOffset the offset in the respective z-slice buffer for the object-mask being
     *     processed.
     */
    void processPoint(int xChange, int yChange, int x, int y, int objectMaskOffset);

    /**
     * Collects the result of the operation after processing neighbor voxels.
     *
     * @return the result.
     */
    public abstract T collectResult();
}
