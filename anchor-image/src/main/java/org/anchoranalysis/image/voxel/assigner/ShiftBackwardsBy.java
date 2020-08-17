package org.anchoranalysis.image.voxel.assigner;

import java.util.function.IntPredicate;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import lombok.AllArgsConstructor;

/**
 * Shifts all coordinates BACKWARDS before passing to another {@link VoxelsAssigner}
 * 
 * <p>This is useful for translating from global coordinates to relative coordinates
 * e.g. translating the global coordinate systems used in {@code BoundedVoxels} to
 * relative coordinates for underlying voxel buffer.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
class ShiftBackwardsBy implements VoxelsAssigner {

    /** The delegate where the shifted coordinates are passed to */
    private final VoxelsAssigner voxelsAssigner;
    
    /** How much to shift back by */
    private final ReadableTuple3i shift;

    @Override
    public void toVoxel(int x, int y, int z) {
        voxelsAssigner.toVoxel(x - shift.x(), y - shift.y(), z - shift.z());
    }

    @Override
    public void toBox(BoundingBox box) {
        voxelsAssigner.toBox( shift(box) );
    }

    @Override
    public void toAll() {
        voxelsAssigner.toAll();
    }

    @Override
    public int toObject(ObjectMask object) {
        return voxelsAssigner.toObject( shift(object) );
    }
    

    @Override
    public int toObject(ObjectMask object, IntPredicate voxelPredicate) {
        return voxelsAssigner.toObject( shift(object), voxelPredicate );
    }
    
    @Override
    public int toObject(ObjectMask object, BoundingBox restrictTo) {
        return voxelsAssigner.toObject(shift(object), shift(restrictTo));
    }

    @Override
    public int toEitherTwoObjects(ObjectMask object1, ObjectMask object2,
            BoundingBox restrictTo) {
        return voxelsAssigner.toEitherTwoObjects( shift(object1), shift(object2), shift(restrictTo));
    }
    
    private BoundingBox shift( BoundingBox box ) {
        return box.shiftBackBy(shift);
    }
    
    private ObjectMask shift( ObjectMask object ) {
        return object.shiftBackBy(shift);
    }

}
