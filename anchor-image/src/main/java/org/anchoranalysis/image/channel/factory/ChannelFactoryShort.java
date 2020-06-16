package org.anchoranalysis.image.channel.factory;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.voxel.box.VoxelBoxShort;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromShortBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class ChannelFactoryShort extends ChannelFactorySingleType {

	private static VoxelDataType dataType = VoxelDataTypeUnsignedShort.instance;
	
	@Override
	public Channel createEmptyInitialised(ImageDim dim) {
		assert(dim!=null);
		VoxelBoxShort vb = new VoxelBoxShort( PixelsFromShortBufferArr.createInitialised(dim.getExtnt()) );
		return create(vb, dim.getRes() );
	}

	@Override
	public Channel createEmptyUninitialised(ImageDim dim) {
		
		PixelsFromShortBufferArr pixels = PixelsFromShortBufferArr.createEmpty( dim.getExtnt() );
		
		VoxelBoxShort vb = new VoxelBoxShort( pixels );
		return create(vb, dim.getRes() );
	}

	@Override
	public VoxelDataType dataType() {
		return dataType;
	}
	
	public static VoxelDataType staticDataType() {
		return dataType;
	}
}
