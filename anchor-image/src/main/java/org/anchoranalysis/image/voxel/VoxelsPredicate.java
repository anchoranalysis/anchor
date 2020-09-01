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
package org.anchoranalysis.image.voxel;

import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Methods to find or count voxels that satisfy a predicate
 *
 * @author Owen Feehan
 */
public interface VoxelsPredicate {

    /**
     * Does at least one value satisfy the predicate - across all voxels?
     *
     * @return true iff at least one value exists
     */
    boolean anyExists();

    /**
     * Counts the number of values satisfying the predicate - across all voxels
     *
     * @return the total count
     */
    int count();

    /**
     * Counts the number of values satisfying the predicate - but restricted to voxels corresponding
     * to ON in an object-mask
     *
     * @param object the object-mask
     * @return the total count according to the above constraint
     */
    int countForObject(ObjectMask object);

    /**
     * Whether the count is greater than a particular threshold
     *
     * @param threshold the threshold
     * @return true as soon as more voxels are counted than the threshold, false if it never occurs
     */
    boolean higherCountExistsThan(int threshold);

    /**
     * Whether the count is less than a particular threshold
     *
     * @param threshold the threshold
     * @return false as soon as as many voxels as threshold, true if it never occurs
     */
    public boolean lowerCountExistsThan(int threshold);

    /**
     * Creates an object-mask for all the voxels inside the bounding-box satisfying the predicate
     *
     * <p>Any voxels satisfying the predicate are set to ON
     *
     * <p>All other voxels are set to OFF
     *
     * @param box bounding-box
     * @return an object-mask referring to the bounding-box
     */
    ObjectMask deriveObject(BoundingBox box);
}
