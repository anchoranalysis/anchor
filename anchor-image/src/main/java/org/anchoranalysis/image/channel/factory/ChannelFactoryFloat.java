/* (C)2020 */
package org.anchoranalysis.image.channel.factory;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.VoxelBoxFloat;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromFloatBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;

public class ChannelFactoryFloat implements ChannelFactorySingleType {

    private static final VoxelDataTypeFloat DATA_TYPE = VoxelDataTypeFloat.INSTANCE;

    @Override
    public Channel createEmptyInitialised(ImageDimensions dim) {
        VoxelBoxFloat vb =
                new VoxelBoxFloat(PixelsFromFloatBufferArr.createInitialised(dim.getExtent()));
        return create(vb, dim.getRes());
    }

    @Override
    public Channel createEmptyUninitialised(ImageDimensions dimensions) {

        PixelsFromFloatBufferArr pixels =
                PixelsFromFloatBufferArr.createEmpty(dimensions.getExtent());

        VoxelBoxFloat vb = new VoxelBoxFloat(pixels);
        return create(vb, dimensions.getRes());
    }

    @Override
    public VoxelDataType dataType() {
        return DATA_TYPE;
    }
}
