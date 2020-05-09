package org.anchoranalysis.image.bean.sgmn.binary;

import java.nio.ByteBuffer;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.sgmn.SgmnFailedException;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;

public abstract class BinarySgmnOne extends BinarySgmn {

	// START BEAN PROPERTIES
	@BeanField
	private BinarySgmn sgmn;
	// END BEAN PROPERTIES
	
	@Override
	public BinaryVoxelBox<ByteBuffer> sgmn(VoxelBoxWrapper voxelBox, BinarySgmnParameters params) throws SgmnFailedException {
		return sgmnFromSgmn(voxelBox, params, sgmn);
	}
	
	@Override
	public BinaryVoxelBox<ByteBuffer> sgmn(VoxelBoxWrapper voxelBox, BinarySgmnParameters params, ObjMask objMask)
			throws SgmnFailedException {
		return sgmnFromSgmn(voxelBox, params, objMask, sgmn);
	}
	
	protected abstract BinaryVoxelBox<ByteBuffer> sgmnFromSgmn(
		VoxelBoxWrapper voxelBox,
		BinarySgmnParameters params,
		BinarySgmn sgmn
	) throws SgmnFailedException;
	
	protected abstract BinaryVoxelBox<ByteBuffer> sgmnFromSgmn(
		VoxelBoxWrapper voxelBox,
		BinarySgmnParameters params,
		ObjMask objMask,
		BinarySgmn sgmn
	) throws SgmnFailedException;

	public BinarySgmn getSgmn() {
		return sgmn;
	}

	public void setSgmn(BinarySgmn sgmn) {
		this.sgmn = sgmn;
	}
}
