/* (C)2020 */
package org.anchoranalysis.image.channel.factory;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromByteBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChannelFactoryByte implements ChannelFactorySingleType {

    private static Log log = LogFactory.getLog(ChannelFactoryByte.class);
    private static final VoxelDataTypeUnsignedByte DATA_TYPE = VoxelDataTypeUnsignedByte.INSTANCE;

    private static final VoxelBoxFactoryTypeBound<ByteBuffer> FACTORY = VoxelBoxFactory.getByte();

    @Override
    public Channel createEmptyInitialised(ImageDimensions dim) {
        VoxelBox<ByteBuffer> vb = FACTORY.create(dim.getExtent());

        log.debug(String.format("Creating empty initialised: %s", dim.getExtent().toString()));

        return create(vb, dim.getRes());
    }

    @Override
    public Channel createEmptyUninitialised(ImageDimensions dimensions) {
        PixelsFromByteBufferArr pixels =
                PixelsFromByteBufferArr.createEmpty(dimensions.getExtent());

        VoxelBox<ByteBuffer> vb = FACTORY.create(pixels);
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
