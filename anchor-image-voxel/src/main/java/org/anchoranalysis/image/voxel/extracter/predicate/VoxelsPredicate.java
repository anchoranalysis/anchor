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
package org.anchoranalysis.image.voxel.extracter.predicate;

import java.util.Optional;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Methods to find or count voxels that satisfy a predicate.
 *
 * @author Owen Feehan
 */
public interface VoxelsPredicate {

    /**
     * Does at least one value satisfy the predicate - across all voxels?
     * 
     * <p>The computational cost of the operation is {@code O(n)} in the number of voxels in the worst-case. Typically, it exits earlier.
     *
     * @return true iff at least one value exists.
     */
    boolean anyExists();

    /**
     * Counts the number of values satisfying the predicate - across all voxels.
     * 
     * <p>The computational cost of the operation is {@code O(n)} in the number of voxels.
     *
     * @return the total count.
     */
    int count();

    /**
     * Counts the number of values satisfying the predicate - but restricted to voxels corresponding
     * to <i>on</i> in an object-mask.
     * 
     * <p>The computational cost of the operation is {@code O(n)} in the number of voxels.
     *
     * @param object the object-mask.
     * @return the total count according to the above constraint.
     */
    int countForObject(ObjectMask object);

    /**
     * Whether the count is greater than a particular threshold.
     * 
     * <p>The computational cost of the operation is {@code O(n)} in the number of voxels in the worst-case. Typically, it exits earlier.
     *
     * @param threshold the threshold.
     * @return true as soon as more voxels are counted than the threshold, false if it never occurs.
     */
    boolean higherCountExistsThan(int threshold);

    /**
     * Whether the count is less than a particular threshold.
     * 
     * <p>The computational cost of the operation is {@code O(n)} in the number of voxels in the worst-case. Typically, it exits earlier.
     *
     * @param threshold the threshold.
     * @return false as soon as as many voxels as threshold, true if it never occurs.
     */
    public boolean lowerCountExistsThan(int threshold);

    /**
     * Creates an {@link ObjectMask} for all the voxels inside the bounding-box satisfying the
     * predicate.
     *
     * <p>Any voxels satisfying the predicate are set to <i>on</i>.
     *
     * <p>All other voxels are set to <i>off</i>.
     *
     * <p>Unlike {@link #deriveObjectTight}, this operates only within a certain bounding-box, and
     * always uses an identical bounding-box for the created {@link ObjectMask} as {@code box}.
     * 
     * <p>The computational cost of the operation is {@code O(n)} in the number of voxels in {@code box} only.
     *
     * @param box bounding-box.
     * @return an object-mask referring to the bounding-box, with the same corner-position and size
     *     as {@code box}.
     */
    ObjectMask deriveObject(BoundingBox box);

    /**
     * Creates an {@link ObjectMask} for all the voxels satisfying the predicate, minimally fitting
     * the bounding-box to the <i>on</i> voxels only.
     *
     * <p>Any voxels satisfying the predicate are set to <i>on</i>.
     *
     * <p>All other voxels are set to <i>off</i>.
     *
     * <p>Unlike {@link #deriveObject(BoundingBox)}, this operates all the voxels, and will
     * typically use a much smaller bounding-box (fewer voxels) to describe the <i>on</i> voxels.
     * 
     * <p>The computational cost of the operation is {@code O(n)} in the number of voxels in {@code box} only.
     *
     * @return an object-mask indicating all voxels that match the predicate, and with as minimal a
     *     bounding-box as possible to contain these. If no voxels match, then {@link
     *     Optional#empty()}.
     */
    Optional<ObjectMask> deriveObjectTight();
}
