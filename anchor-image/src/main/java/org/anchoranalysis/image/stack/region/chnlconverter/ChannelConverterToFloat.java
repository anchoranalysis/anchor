/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverterToFloatNoScaling;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;

// Converts an existing channel to a float
public class ChannelConverterToFloat extends ChannelConverter<FloatBuffer> {

    public ChannelConverterToFloat() {
        super(
                VoxelDataTypeFloat.INSTANCE,
                new VoxelBoxConverterToFloatNoScaling(),
                VoxelBoxFactory.getFloat());
    }
}
