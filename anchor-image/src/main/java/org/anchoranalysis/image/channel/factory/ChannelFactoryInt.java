/* (C)2020 */
package org.anchoranalysis.image.channel.factory;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.VoxelBoxInt;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromIntBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;

public class ChannelFactoryInt implements ChannelFactorySingleType {

    private static final VoxelDataType DATA_TYPE = VoxelDataTypeUnsignedInt.INSTANCE;

    @Override
    public Channel createEmptyInitialised(ImageDimensions dim) {
        VoxelBoxInt vb = new VoxelBoxInt(PixelsFromIntBufferArr.createInitialised(dim.getExtent()));
        return create(vb, dim.getRes());
    }

    @Override
    public Channel createEmptyUninitialised(ImageDimensions dimensions) {

        PixelsFromIntBufferArr pixels = PixelsFromIntBufferArr.createEmpty(dimensions.getExtent());

        VoxelBoxInt vb = new VoxelBoxInt(pixels);
        return create(vb, dimensions.getRes());
    }

    @Override
    public VoxelDataType dataType() {
        return DATA_TYPE;
    }
}
