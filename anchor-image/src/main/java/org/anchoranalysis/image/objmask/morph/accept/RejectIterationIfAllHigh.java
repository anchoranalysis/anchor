package org.anchoranalysis.image.objmask.morph.accept;

import java.nio.ByteBuffer;

import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class RejectIterationIfAllHigh implements AcceptIterationConditon {

	@Override
	public boolean acceptIteration(VoxelBox<ByteBuffer> buffer, BinaryValues bvb) {
		// We exit early if there's no off-pixel
		BinaryVoxelBoxByte nextBinary = new BinaryVoxelBoxByte(buffer, bvb );
		return nextBinary.hasOffVoxel();
	}
	
}