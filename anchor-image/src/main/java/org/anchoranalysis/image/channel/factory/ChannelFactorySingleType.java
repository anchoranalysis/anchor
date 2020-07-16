/* (C)2020 */
package org.anchoranalysis.image.channel.factory;

import java.nio.Buffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/** Creates a Chnl for a specific data-type */
public interface ChannelFactorySingleType {

    Channel createEmptyInitialised(ImageDimensions dimensions);

    Channel createEmptyUninitialised(ImageDimensions dimensions);

    default Channel create(VoxelBox<? extends Buffer> bufferAccess, ImageResolution res) {
        return new Channel(bufferAccess, res);
    }

    VoxelDataType dataType();
}
