package org.anchoranalysis.image.voxel.assigner;

import java.util.function.IntPredicate;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Assigns values to some or all voxels
 *
 * <p>The co-ordinates of the bounding-box, object-mask etc. are always <i>relative</i> to a
 * particular coordinate frame.
 *
 * <p>Throughout the code-base, usually this interface is only exposed expecting <b>global</b>
 * coordinates (i.e. not relative to anything else) for bounding-boxes etc.
 *
 * @author Owen Feehan
 */
public interface VoxelsAssigner {

    /**
     * Assigns one particular voxel
     *
     * @param point point to assign to
     */
    default void toVoxel(Point3i point) {
        toVoxel(point.x(), point.y(), point.z());
    }

    /**
     * Assigns one particular voxel
     *
     * @param x coordinate in x dimension
     * @param y coordinate in y dimension
     * @param z coordinate in z dimension (slice index)
     */
    void toVoxel(int x, int y, int z);

    /**
     * Assigns to only voxels inside a bounding-box
     *
     * @param box the bounding-box
     */
    void toBox(BoundingBox box);

    /** Assigns to all the voxels */
    void toAll();

    /**
     * Sets voxels in a box to a particular value if they match an object-mask
     *
     * @param object the object-mask to restrict which values in the buffer are written to
     */
    void toObject(ObjectMask object);

    /**
     * Sets voxels in a box to a particular value if they match an object-mask <b>and</b> each voxel 
     * matches a predicate
     *
     * <p>If any one of the voxels in the object doesn't match the predicate, the operation is
     * aborted, and nothing is written at all.
     *
     * @param object the object-mask to restrict which values in the buffer are assigned
     * @param voxelPredicate the existing value of every voxel to be written must match this
     *     predicate, otherwise no voxels are set at all
     * @return if at least one voxel was set
     */
    boolean toObject(ObjectMask object, IntPredicate voxelPredicate);

    /**
     * Sets voxels in a box to a particular value if they match a object-mask (but only a part of
     * the object-mask)
     *
     * <p>Pixels are unchanged if they do not match the mask, or outside the part of the mask that
     * is considered.
     *
     * @param object the object-mask to restrict where voxels are set
     * @param restrictTo a restriction on where to process in the object-mask (expressed in the same
     *     coordinates as {@code object}).
     */
    void toObject(ObjectMask object, BoundingBox restrictTo);

    /**
     * Sets voxels to a value if the position is ON in either of two masks
     *
     * @param voxels1 first-object
     * @param voxels2 second-object
     * @param restrictTo only process this region (which is sensibly part or all of the intersection
     *     of the two objects bounding-boxes)
     */
    void toEitherTwoObjects(ObjectMask object1, ObjectMask object2, BoundingBox restrictTo);
}
