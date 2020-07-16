/* (C)2020 */
package org.anchoranalysis.image.channel.factory;

import java.nio.Buffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.factory.VoxelDataTypeFactoryMultiplexer;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/** Creates a channel for one of several data-types */
public class ChannelFactory extends VoxelDataTypeFactoryMultiplexer<ChannelFactorySingleType> {

    // Singleton
    private static ChannelFactory instance;

    private ChannelFactory() {
        super(
                new ChannelFactoryByte(),
                new ChannelFactoryShort(),
                new ChannelFactoryInt(),
                new ChannelFactoryFloat());
    }

    /** Singleton */
    public static ChannelFactory instance() {
        if (instance == null) {
            instance = new ChannelFactory();
        }
        return instance;
    }

    public Channel createEmptyInitialised(ImageDimensions dimensions, VoxelDataType chnlDataType) {
        ChannelFactorySingleType factory = get(chnlDataType);
        return factory.createEmptyInitialised(dimensions);
    }

    public Channel createEmptyUninitialised(
            ImageDimensions dimensions, VoxelDataType chnlDataType) {
        ChannelFactorySingleType factory = get(chnlDataType);
        return factory.createEmptyUninitialised(dimensions);
    }

    public Channel create(VoxelBox<? extends Buffer> voxelBox, ImageResolution res) {

        VoxelDataType chnlDataType = voxelBox.dataType();

        ChannelFactorySingleType factory = get(chnlDataType);
        return factory.create(voxelBox, res);
    }
}
