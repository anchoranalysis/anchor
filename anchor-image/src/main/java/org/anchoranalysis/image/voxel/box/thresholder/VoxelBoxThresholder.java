/* (C)2020 */
package org.anchoranalysis.image.voxel.box.thresholder;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.iterator.IterateVoxels;

/** Performs threshold operation on a VoxelBox */
public class VoxelBoxThresholder {

    private VoxelBoxThresholder() {}

    public static void thresholdForLevel(
            VoxelBox<ByteBuffer> inputBuffer, int level, BinaryValuesByte bvOut) {
        // We know that as the inputType is byte, it will be performed in place
        thresholdForLevel(VoxelBoxWrapper.wrap(inputBuffer), level, bvOut, Optional.empty(), false);
    }

    // Perform inplace
    public static BinaryVoxelBox<ByteBuffer> thresholdForLevel(
            VoxelBoxWrapper inputBuffer,
            int level,
            BinaryValuesByte bvOut,
            Optional<ObjectMask> mask,
            boolean alwaysDuplicate) {
        VoxelBox<ByteBuffer> boxOut = inputBuffer.asByteOrCreateEmpty(alwaysDuplicate);

        if (inputBuffer.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {

            IterateVoxels.callEachPoint(
                    mask, inputBuffer.asByte(), new PointProcessor(level, boxOut, bvOut));
        }

        return new BinaryVoxelBoxByte(boxOut, bvOut.createInt());
    }
}
