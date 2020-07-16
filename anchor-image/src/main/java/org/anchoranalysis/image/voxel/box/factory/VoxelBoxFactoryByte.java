/* (C)2020 */
package org.anchoranalysis.image.voxel.box.factory;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxByte;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsForPlane;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromByteBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

final class VoxelBoxFactoryByte implements VoxelBoxFactoryTypeBound<ByteBuffer> {

    private static final VoxelDataType DATA_TYPE = VoxelDataTypeUnsignedByte.INSTANCE;

    @Override
    public VoxelBox<ByteBuffer> create(PixelsForPlane<ByteBuffer> pixelsForPlane) {
        return new VoxelBoxByte(pixelsForPlane);
    }

    @Override
    public VoxelBox<ByteBuffer> create(Extent e) {
        return new VoxelBoxByte(PixelsFromByteBufferArr.createInitialised(e));
    }

    @Override
    public VoxelDataType dataType() {
        return DATA_TYPE;
    }
}
