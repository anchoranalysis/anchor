package org.anchoranalysis.image.voxel.extracter;

import java.nio.Buffer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsPredicate;

/**
 * Projects a {@link VoxelsExtracter} to a corner in a larger global space
 *
 * <p>Coordinates are translated appropriately for any calls from the larger global space to the
 * space on which {@code delegate} is defined.
 *
 * @param <T> buffer-type
 * @author Owen Feehan
 */
@AllArgsConstructor
class AtCorner<T extends Buffer> implements VoxelsExtracter<T> {

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
    public void objectCopyTo(
            ObjectMask from, Voxels<T> voxelsDestination, BoundingBox destinationBox) {
        delegate.objectCopyTo(shiftBack(from), voxelsDestination, destinationBox);
    }

    @Override
    public Voxels<T> resizedXY(int sizeX, int sizeY, Interpolator interpolator) {
        return delegate.resizedXY(sizeX, sizeY, interpolator);
    }

    @Override
    public Voxels<T> projectionMax() {
        return delegate.projectionMax();
    }

    @Override
    public Voxels<T> projectionMean() {
        return delegate.projectionMean();
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
    public int voxelWithMaxIntensity() {
        return delegate.voxelWithMaxIntensity();
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
