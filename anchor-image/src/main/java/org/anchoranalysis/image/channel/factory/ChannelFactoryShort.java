/* (C)2020 */
package org.anchoranalysis.image.channel.factory;

import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.VoxelBoxShort;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromShortBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class ChannelFactoryShort implements ChannelFactorySingleType {

    private static final VoxelDataType DATA_TYPE = VoxelDataTypeUnsignedShort.INSTANCE;

    @Override
    public Channel createEmptyInitialised(ImageDimensions dim) {
        VoxelBoxShort vb =
                new VoxelBoxShort(PixelsFromShortBufferArr.createInitialised(dim.getExtent()));
        return create(vb, dim.getRes());
    }

    @Override
    public Channel createEmptyUninitialised(ImageDimensions dimensions) {

        PixelsFromShortBufferArr pixels =
                PixelsFromShortBufferArr.createEmpty(dimensions.getExtent());

        VoxelBoxShort vb = new VoxelBoxShort(pixels);
        return create(vb, dimensions.getRes());
    }

    @Override
    public VoxelDataType dataType() {
        return DATA_TYPE;
    }

    public static VoxelDataType staticDataType() {
        return DATA_TYPE;
    }
}
