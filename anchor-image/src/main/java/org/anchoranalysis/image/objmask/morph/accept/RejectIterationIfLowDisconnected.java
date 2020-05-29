package org.anchoranalysis.image.objmask.morph.accept;

import java.nio.ByteBuffer;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class RejectIterationIfLowDisconnected implements AcceptIterationConditon {

	@Override
	public boolean acceptIteration(VoxelBox<ByteBuffer> buffer, BinaryValues bvb) throws OperationFailedException {
		BinaryVoxelBoxByte nextBinary = new BinaryVoxelBoxByte(buffer, bvb.createInverted() );
		
		ObjMask omMask = new ObjMask(nextBinary);
		return omMask.checkIfConnected();
		
		
	}
	
}