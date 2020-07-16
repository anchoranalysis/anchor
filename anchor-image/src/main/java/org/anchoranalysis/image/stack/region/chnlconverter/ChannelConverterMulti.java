/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class ChannelConverterMulti {

    private ConversionPolicy conversionPolicy = ConversionPolicy.DO_NOT_CHANGE_EXISTING;

    public ChannelConverterMulti() {
        super();
    }

    public Channel convert(Channel chnlIn, VoxelDataType outputType) {

        if (chnlIn.getVoxelDataType().equals(outputType)) {
            return chnlIn;
        } else if (outputType.equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
            return new ChannelConverterToUnsignedByte().convert(chnlIn, conversionPolicy);
        } else if (outputType.equals(VoxelDataTypeUnsignedShort.INSTANCE)) {
            return new ChannelConverterToUnsignedShort().convert(chnlIn, conversionPolicy);
        } else if (outputType.equals(VoxelDataTypeFloat.INSTANCE)) {
            return new ChannelConverterToFloat().convert(chnlIn, conversionPolicy);
        } else if (outputType.equals(VoxelDataTypeUnsignedInt.INSTANCE)) {
            throw new UnsupportedOperationException(
                    "UnsignedInt is not yet supported for this operation");
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
