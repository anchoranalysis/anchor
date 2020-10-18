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

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.extent.box.BoundingBox;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Projects a {@link VoxelsPredicate} to a corner in a larger global space
 *
 * <p>Coordinates are translated appropriately for any calls from the larger global space to the
 * space on which {@code delegate} is defined.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class PredicateAtCorner implements VoxelsPredicate {

    /** The corner at which the voxels referred to by {@code delegate} are considered to exist. */
    private ReadableTuple3i corner;

    /** Delegate. */
    private VoxelsPredicate delegate;

    @Override
    public boolean anyExists() {
        return delegate.anyExists();
    }

    @Override
    public int count() {
        return delegate.count();
    }

    @Override
    public boolean higherCountExistsThan(int threshold) {
        return delegate.higherCountExistsThan(threshold);
    }

    @Override
    public boolean lowerCountExistsThan(int threshold) {
        return delegate.lowerCountExistsThan(threshold);
    }
    
    @Override
    public int countForObject(ObjectMask object) {
        return delegate.countForObject(object);
    }
    
    @Override
    public ObjectMask deriveObject(BoundingBox box) {
        return delegate.deriveObject(box.shiftBackBy(corner)).shiftBy(corner);
    }
}
