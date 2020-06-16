package org.anchoranalysis.image.objectmask.morph.accept;

import java.nio.ByteBuffer;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class RejectIterationIfLowDisconnected implements AcceptIterationConditon {

	@Override
	public boolean acceptIteration(VoxelBox<ByteBuffer> buffer, BinaryValues bvb) throws OperationFailedException {
		BinaryVoxelBoxByte nextBinary = new BinaryVoxelBoxByte(buffer, bvb.createInverted() );
		
		ObjectMask omMask = new ObjectMask(nextBinary);
		return omMask.checkIfConnected();
		
		
	}
	
}