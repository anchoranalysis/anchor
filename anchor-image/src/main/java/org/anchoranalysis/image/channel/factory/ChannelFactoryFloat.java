package org.anchoranalysis.image.channel.factory;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.voxel.box.VoxelBoxFloat;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromFloatBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;

public class ChannelFactoryFloat extends ChannelFactorySingleType {

	private static final VoxelDataTypeFloat DATA_TYPE = VoxelDataTypeFloat.instance;
	
	@Override
	public Channel createEmptyInitialised(ImageDim dim) {
		VoxelBoxFloat vb = new VoxelBoxFloat( PixelsFromFloatBufferArr.createInitialised(dim.getExtnt()) );
		return create(vb, dim.getRes() );
	}

	@Override
	public Channel createEmptyUninitialised(ImageDim dim) {
		
		PixelsFromFloatBufferArr pixels = PixelsFromFloatBufferArr.createEmpty( dim.getExtnt() );
		
		VoxelBoxFloat vb = new VoxelBoxFloat( pixels );
		return create(vb, dim.getRes() );
	}

	@Override
	public VoxelDataType dataType() {
		return DATA_TYPE;
	}
}
