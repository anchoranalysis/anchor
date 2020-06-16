package org.anchoranalysis.image.channel.factory;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.voxel.box.VoxelBoxInt;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromIntBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;

public class ChannelFactoryInt extends ChannelFactorySingleType {

	private static VoxelDataType dataType = VoxelDataTypeUnsignedInt.instance;
	
	@Override
	public Channel createEmptyInitialised(ImageDim dim) {
		assert(dim!=null);
		VoxelBoxInt vb = new VoxelBoxInt( PixelsFromIntBufferArr.createInitialised(dim.getExtnt()) );
		return create(vb, dim.getRes() );
	}

	@Override
	public Channel createEmptyUninitialised(ImageDim dim) {
		
		PixelsFromIntBufferArr pixels = PixelsFromIntBufferArr.createEmpty( dim.getExtnt() );
		
		VoxelBoxInt vb = new VoxelBoxInt( pixels );
		return create(vb, dim.getRes() );
	}

	@Override
	public VoxelDataType dataType() {
		return dataType;
	}
}
