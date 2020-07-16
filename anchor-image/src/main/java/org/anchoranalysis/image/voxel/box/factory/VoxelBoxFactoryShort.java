/* (C)2020 */
package org.anchoranalysis.image.voxel.box.factory;

import java.nio.ShortBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxShort;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsForPlane;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsFromShortBufferArr;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

final class VoxelBoxFactoryShort implements VoxelBoxFactoryTypeBound<ShortBuffer> {

    private static final VoxelDataType DATA_TYPE = VoxelDataTypeUnsignedShort.INSTANCE;

    @Override
    public VoxelBox<ShortBuffer> create(PixelsForPlane<ShortBuffer> pixelsForPlane) {
        return new VoxelBoxShort(pixelsForPlane);
    }

    @Override
    public VoxelBox<ShortBuffer> create(Extent e) {
        return new VoxelBoxShort(PixelsFromShortBufferArr.createInitialised(e));
    }

    @Override
    public VoxelDataType dataType() {
        return DATA_TYPE;
    }
}
