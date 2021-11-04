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
package org.anchoranalysis.image.voxel.extracter;

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.extracter.predicate.PredicateAtCorner;
import org.anchoranalysis.image.voxel.extracter.predicate.VoxelsPredicate;
import org.anchoranalysis.image.voxel.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Projects a {@link VoxelsExtracter} to a corner in a larger global space.
 *
 * <p>Coordinates are translated appropriately for any calls from the larger global space to the
 * space on which {@code delegate} is defined.
 *
 * @param <T> buffer-type
 * @author Owen Feehan
 */
@AllArgsConstructor
class AtCorner<T> implements VoxelsExtracter<T> {

    /** The corner at which the voxels referred to by {@code delegate} are considered to exist */
    private ReadableTuple3i corner;

    /** Delegate */
    private VoxelsExtracter<T> delegate;

    @Override
    public int voxel(ReadableTuple3i point) {
        return delegate.voxel(shiftBack(point));
    }

    @Override
    public Voxels<T> slice(int sliceIndex) {
        return delegate.slice(sliceIndex - corner.z());
    }

    @Override
    public Voxels<T> region(BoundingBox box, boolean reuseIfPossible) {
        return delegate.region(shiftBack(box), reuseIfPossible);
    }

    @Override
    public void boxCopyTo(
            BoundingBox from, Voxels<T> voxelsDestination, BoundingBox destinationBox) {
        delegate.boxCopyTo(shiftBack(from), voxelsDestination, destinationBox);
    }

    @Override
    public Voxels<T> resizedXY(int sizeX, int sizeY, Interpolator interpolator) {
        return delegate.resizedXY(sizeX, sizeY, interpolator);
    }

    @Override
    public Voxels<T> projectMax() {
        return delegate.projectMax();
    }

    @Override
    public Voxels<T> projectMean() {
        return delegate.projectMean();
    }

    @Override
    public VoxelsPredicate voxelsEqualTo(int equalToValue) {
        return new PredicateAtCorner(corner, delegate.voxelsEqualTo(equalToValue));
    }

    @Override
    public VoxelsPredicate voxelsGreaterThan(int threshold) {
        return new PredicateAtCorner(corner, delegate.voxelsGreaterThan(threshold));
    }

    @Override
    public long voxelWithMaxIntensity() {
        return delegate.voxelWithMaxIntensity();
    }

    @Override
    public void objectCopyTo(
            ObjectMask from, Voxels<T> voxelsDestination, BoundingBox destinationBox) {
        delegate.objectCopyTo(shiftBack(from), voxelsDestination, destinationBox);
    }

    private ReadableTuple3i shiftBack(ReadableTuple3i point) {
        return Point3i.immutableSubtract(point, corner);
    }

    private BoundingBox shiftBack(BoundingBox box) {
        return box.shiftBackBy(corner);
    }

    private ObjectMask shiftBack(ObjectMask object) {
        return object.shiftBackBy(corner);
    }
}
