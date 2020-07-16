/* (C)2020 */
package org.anchoranalysis.image.object.morph.accept;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class RejectIterationIfLowDisconnected implements AcceptIterationConditon {

    @Override
    public boolean acceptIteration(VoxelBox<ByteBuffer> buffer, BinaryValues bvb)
            throws OperationFailedException {
        BinaryVoxelBoxByte nextBinary = new BinaryVoxelBoxByte(buffer, bvb.createInverted());
        return new ObjectMask(nextBinary).checkIfConnected();
    }
}
