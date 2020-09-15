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

/**
 * Processes a point that is a neighbor of another - includes global (absolute) coordinates for this
 * point.
 *
 * @param <T> result-type that can be collected after processing
 */
public interface ProcessVoxelNeighborAbsolute<T> {

    /**
     * The value and offset for the source point (around which we process neighbors)
     *
     * <p>This function should always be called before {@link #processPoint}
     *
     * <p>It can be called repeatedly for different points (resetting state each time).
     *
     * @param sourceVal the value of the source pixel
     * @param sourceOffsetXY the offset of the source pixel in XY
     */
    void initSource(int sourceVal, int sourceOffsetXY);

    /**
     * Notifies the processor that there has been a change in z-coordinate
     *
     * @param zChange the change in the Z-dimension to reach this neighbor relative to the source
     *     coordinate
     */
    default void notifyChangeZ(int zChange, int z) {}

    /**
     * Processes a particular point
     *
     * @param xChange the change in x-dimension to reach this neighbor relative to the source
     *     coordinate
     * @param yChange the change in y-dimension to reach this neighbor relative to the source
     *     coordinate
     * @param x the cordinates for this point (the neighboring point) in global (absolute) terms
     *     i.e. NOT relative to a bounding-box
     * @param y the cordinates for this point (the neighboring point) in global (absolute) terms
     *     i.e. NOT relative to a bounding-box
     */
    boolean processPoint(int xChange, int yChange, int x, int y);

    /** Collects the result of the operation after processing neighbor pixels */
    public abstract T collectResult();
}
