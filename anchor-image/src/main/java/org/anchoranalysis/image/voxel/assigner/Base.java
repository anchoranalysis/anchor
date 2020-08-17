package org.anchoranalysis.image.voxel.assigner;

import java.nio.Buffer;
import java.util.Optional;
import java.util.function.IntPredicate;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAsInt;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class Base<T extends Buffer> implements VoxelsAssigner {

    // START REQUIRED ARGUMENTS
    /** The voxels that are assigned to */
    private final Voxels<T> voxels;
    
    /** The voxel-value to assign */
    protected final int valueToAssign;
    // END REQUIRED ARGUMENTS
    
    @Override
    public void toAll() {
        voxels.extent().iterateOverZ( z ->
            assignToEntireBuffer( voxels.sliceBuffer(z) )
        );
    }

    @Override
    public void toBox(BoundingBox box) {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calcCornerMax();
        Extent extent = voxels.extent();

        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            T buffer = voxels.sliceBuffer(z);

            for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
                for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {
                    int offset = extent.offset(x, y);
                    assignAtBufferPosition(buffer, offset);
                }
            }
        }
    }
    
    @Override
    public void toVoxel(int x, int y, int z) {
        T buffer = voxels.sliceBuffer(z);
        assignAtBufferPosition(buffer, voxels.extent().offset(x, y));
    }
    
    /**
     * Assigns the constant value at current and all remaining positions in the buffer
     * 
     * @param buffer the buffer (set to a particular position)
     */
    protected abstract void assignToEntireBuffer(T buffer);
    
    /**
     * Assigns the constant value at a particular position in the buffer
     * 
     * @param buffer the buffer
     * @param index index of the positoion
     */
    protected abstract void assignAtBufferPosition(T buffer, int index);
    
    @Override
    public int toObject(ObjectMask object) {
        return toObject(
                object,
                Optional.empty()
        );
    }

    @Override
    public int toObject(ObjectMask object, IntPredicate voxelPredicate) {
        
        // First check if all voxels on the object match the predicate
        if (IterateVoxelsAsInt.allPointsMatchPredicate(voxels, object, voxelPredicate)) {
            return toObject(object);
        } else {
            return -1;  // Failure code that at least one voxel didn't match
        }
    }
    
    
    @Override
    public int toObject(ObjectMask object, BoundingBox restrictTo) {
        return toObject(
                object,
                Optional.of(restrictTo)
         );
    }
    
    /**
     * Sets voxels in a box to a particular value if they match a object-mask (but only a part of the object-mask)
     *
     * <p>Pixels are unchanged if they do not match the mask, or outside the part of the mask that is considered.
     *
     * <p>Bounding boxes can be used to restrict regions in both the mask and destination, but
     * must be equal in volume.
     *
     * @param object the object-mask to restrict where voxels are set
     * @param restrictTo optionally, a restriction on where in the object-mask to process (expressed in the same coordinates as {@code object}). Its extent must be equal to {@code boxToBeAssigned}.
     * @return the number of voxels successfully "set"
     */
    private int toObject(
        ObjectMask object,
        Optional<BoundingBox> restrictTo
    ) {
        return IterateVoxelsAsInt.callEachPoint(voxels, object, restrictTo, (Point3i point, VoxelBuffer<T> buffer, int offset) -> buffer.putInt(offset, valueToAssign));
    }

    @Override
    public int toEitherTwoObjects( // NOSONAR
            ObjectMask object1,
            ObjectMask object2,
            BoundingBox restrictTo
    ) {
        int countFirst = toObject(object1, restrictTo);
        int countSecond = toObject(object2, restrictTo);
        return countFirst + countSecond;
    }
}
