package org.anchoranalysis.io.bioformats;

import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactoryFloat;
import org.anchoranalysis.image.channel.factory.ChannelFactoryInt;
import org.anchoranalysis.image.channel.factory.ChannelFactoryShort;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeSignedShort;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

import loci.formats.FormatTools;

class MultiplexDataTypes {

	public static VoxelDataType multiplexFormat( int pixelType ) throws RasterIOException {
		switch(pixelType) {
		case FormatTools.UINT8:
			return VoxelDataTypeUnsignedByte.instance;
		case FormatTools.UINT16:
			return VoxelDataTypeUnsignedShort.instance;
		case FormatTools.INT16:
			return VoxelDataTypeSignedShort.instance;			
		case FormatTools.FLOAT:
			return VoxelDataTypeFloat.instance;
		default:
			throw new RasterIOException( String.format("File has unknown type %s",  FormatTools.getPixelTypeString(pixelType) ) );
		}	
	}
	
	public static ChannelFactorySingleType multiplexVoxelDataType( VoxelDataType voxelDataType ) {
		if (voxelDataType.equals(VoxelDataTypeUnsignedByte.instance)) {
			return new ChannelFactoryByte();
		} else if (voxelDataType.equals(VoxelDataTypeUnsignedShort.instance)) {
			return new ChannelFactoryShort();
		} else if (voxelDataType.equals(VoxelDataTypeSignedShort.instance)) {
			return new ChannelFactoryShort();			
		} else if (voxelDataType.equals(VoxelDataTypeFloat.instance)) {
			return new ChannelFactoryFloat();
		} else if (voxelDataType.equals(VoxelDataTypeUnsignedInt.instance)) {
			return new ChannelFactoryInt();
		} else {
			throw new UnsupportedOperationException();
		}
	}
}
