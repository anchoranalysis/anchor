package org.anchoranalysis.image.voxel.assigner;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.voxel.Voxels;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoxelsAssignerFactory {

    /**
     * Create voxels-assigner for {@link ByteBuffer}
     *
     * @param voxels the voxels on which arithmetic is to be performed
     * @param valueToAssign the voxel-value to assign
     * @return a newly created assigner
     */
    public static VoxelsAssigner createByte(Voxels<ByteBuffer> voxels, int valueToAssign) {
        return new ByteImplementation(voxels, valueToAssign);
    }

    /**
     * Create voxels-assigner for {@link ShortBuffer}
     *
     * @param voxels the voxels on which arithmetic is to be performed
     * @param valueToAssign the voxel-value to assign
     * @return a newly created assigner
     */
    public static VoxelsAssigner createShort(Voxels<ShortBuffer> voxels, int valueToAssign) {
        return new ShortImplementation(voxels, valueToAssign);
    }

    /**
     * Create voxels-assigner for {@link FloatBuffer}
     *
     * @param voxels the voxels on which arithmetic is to be performed
     * @param valueToAssign the voxel-value to assign
     * @return a newly created assigner
     */
    public static VoxelsAssigner createFloat(Voxels<FloatBuffer> voxels, int valueToAssign) {
        return new FloatImplementation(voxels, valueToAssign);
    }

    /**
     * Create voxels-assigner for {@link IntBuffer}
     *
     * @param voxels the voxels on which arithmetic is to be performed
     * @param valueToAssign the voxel-value to assign
     * @return a newly created assigner
     */
    public static VoxelsAssigner createInt(Voxels<IntBuffer> voxels, int valueToAssign) {
        return new IntImplementation(voxels, valueToAssign);
    }

    /**
     * Shifts all coordinates BACKWARDS before passing to another {@link VoxelsAssigner}
     *
     * <p>This is useful for translating from global coordinates to relative coordinates e.g.
     * translating the global coordinate systems used in {@code BoundedVoxels} to relative
     * coordinates for underlying voxel buffer.
     *
     * @param voxelsAssigner the delegate where the shifted coordinates are passed to
     * @param shift how much to shift back by
     * @return a newly created assigner that performs a shift after calling the existing assigner
     */
    public static VoxelsAssigner shiftBackBy(VoxelsAssigner voxelsAssigner, ReadableTuple3i shift) {
        return new ShiftBackwardsBy(voxelsAssigner, shift);
    }
}
