/* (C)2020 */
package org.anchoranalysis.image.voxel.box.factory;

import java.nio.Buffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.pixelsforplane.PixelsForPlane;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * A factory for creating VoxelBoxes with a particular buffer-type
 *
 * <p>This class (and all its sub-classes) are IMMUTABLE.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
public interface VoxelBoxFactoryTypeBound<T extends Buffer> {

    VoxelBox<T> create(PixelsForPlane<T> pixelsForPlane);

    VoxelBox<T> create(Extent e);

    VoxelDataType dataType();
}
