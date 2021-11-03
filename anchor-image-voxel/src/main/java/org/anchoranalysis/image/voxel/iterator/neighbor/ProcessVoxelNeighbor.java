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

import org.anchoranalysis.spatial.point.Point3i;

/**
 * Processes a point that is an neighbor of another.
 *
 * <p>It assumes there is an associated sliding buffer containing voxel-values.
 *
 * @author Owen Feehan
 * @param <T> result-type that can be collected after processing
 */
public interface ProcessVoxelNeighbor<T> {

    /**
     * Specify the source-point (of which all the processed points are neighbors).
     *
     * <p>This must be called before any calls to {@link #processPoint}.
     *
     * @param pointSource the source point in global coordinates.
     * @param sourceValue the value of the source pixel (in the associated sliding buffer).
     * @param sourceOffsetXY the offset of the source pixel in XY (in the associated sliding
     *     buffer).
     */
    void initSource(Point3i pointSource, int sourceValue, int sourceOffsetXY);

    /**
     * Notifies the processor that there has been a change in z-coordinate.
     *
     * @param zChange the change in the Z-dimension to reach this neighbor relative to the source
     *     coordinate.
     * @return true if processing should continue on this slice, or false if processing should stop
     *     for this slice.
     */
    default boolean notifyChangeZ(int zChange) {
        return true;
    }

    /**
     * Processes a particular point.
     *
     * @param xChange the change in x-dimension to reach this neighbor relative to the source
     *     coordinate.
     * @param yChange the change in y-dimension to reach this neighbor relative to the source
     *     coordinate.
     */
    void processPoint(int xChange, int yChange);

    /**
     * Collects the result of the operation after processing neighbor pixels.
     *
     * @return the result.
     */
    public abstract T collectResult();
}
