/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter;

import java.nio.ShortBuffer;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverter;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverterToShortNoScaling;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

// Converts from other data types to Byte (unsigned 8-bit) without scaling any other data types
public class ChannelConverterToUnsignedShort extends ChannelConverter<ShortBuffer> {

    public ChannelConverterToUnsignedShort() {
        this(new VoxelBoxConverterToShortNoScaling());
    }

    public ChannelConverterToUnsignedShort(VoxelBoxConverter<ShortBuffer> voxelBoxConverter) {
        super(VoxelDataTypeUnsignedShort.INSTANCE, voxelBoxConverter, VoxelBoxFactory.getShort());
    }
}
