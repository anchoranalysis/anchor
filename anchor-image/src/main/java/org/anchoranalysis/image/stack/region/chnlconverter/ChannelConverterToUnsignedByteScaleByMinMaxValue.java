/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverterToByteScaleByMinMaxValue;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

// Converts from other data types to Byte (unsigned 8-bit) without scaling any other data types
public class ChannelConverterToUnsignedByteScaleByMinMaxValue extends ChannelConverter<ByteBuffer> {

    // Min and max are inclusive
    public ChannelConverterToUnsignedByteScaleByMinMaxValue(int min, int max) {
        super(
                VoxelDataTypeUnsignedByte.INSTANCE,
                new VoxelBoxConverterToByteScaleByMinMaxValue(min, max),
                VoxelBoxFactory.getByte());
    }
}
