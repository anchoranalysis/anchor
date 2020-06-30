package org.anchoranalysis.image.channel.factory;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.VoxelBoxShort;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromShortBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class ChannelFactoryShort extends ChannelFactorySingleType {

	private static final VoxelDataType DATA_TYPE = VoxelDataTypeUnsignedShort.instance;
	
	@Override
	public Channel createEmptyInitialised(ImageDimensions dim) {
		assert(dim!=null);
		VoxelBoxShort vb = new VoxelBoxShort( PixelsFromShortBufferArr.createInitialised(dim.getExtnt()) );
		return create(vb, dim.getRes() );
	}

	@Override
	public Channel createEmptyUninitialised(ImageDimensions dim) {
		
		PixelsFromShortBufferArr pixels = PixelsFromShortBufferArr.createEmpty( dim.getExtnt() );
		
		VoxelBoxShort vb = new VoxelBoxShort( pixels );
		return create(vb, dim.getRes() );
	}

	@Override
	public VoxelDataType dataType() {
		return DATA_TYPE;
	}
	
	public static VoxelDataType staticDataType() {
		return DATA_TYPE;
	}
}
