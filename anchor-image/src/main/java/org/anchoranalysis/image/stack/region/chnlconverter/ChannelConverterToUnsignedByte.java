/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverter;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverterToByteNoScaling;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

// Converts from other data types to Byte (unsigned 8-bit) without scaling any other data types
public class ChannelConverterToUnsignedByte extends ChannelConverter<ByteBuffer> {

    public ChannelConverterToUnsignedByte() {
        this(new VoxelBoxConverterToByteNoScaling());
    }

    public ChannelConverterToUnsignedByte(VoxelBoxConverter<ByteBuffer> voxelBoxConverter) {
        super(VoxelDataTypeUnsignedByte.INSTANCE, voxelBoxConverter, VoxelBoxFactory.getByte());
    }
}
