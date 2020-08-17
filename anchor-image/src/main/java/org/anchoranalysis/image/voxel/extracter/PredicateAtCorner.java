package org.anchoranalysis.image.voxel.extracter;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.VoxelsPredicate;

/**
 * Projects a {@link VoxelsPredicate} to a corner in a larger global space
 *
 * <p>Coordinates are translated appropriately for any calls from the larger global space to the
 * space on which {@code delegate} is defined.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class PredicateAtCorner implements VoxelsPredicate {

    /** The corner at which the voxels referred to by {@code delegate} are considered to exist */
    private ReadableTuple3i corner;

    /** Delegate */
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
    public int countForObject(ObjectMask object) {
        return delegate.countForObject(object);
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
    public ObjectMask deriveObject(BoundingBox box) {
        return delegate.deriveObject(box.shiftBackBy(corner)).shiftBy(corner);
    }
}
