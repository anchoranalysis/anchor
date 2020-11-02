package org.anchoranalysis.test.image.rasterwriter;

import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import lombok.Value;

/**
 * Specifies a type and number of channels in a stack.
 * 
 * @author Owen Feehan
 */
@Value
public class ChannelSpecification {
    
    /** Default data-type for all channels in the stack if not otherwise specified. */
    VoxelDataType channelVoxelType;
    
    /** How many channels in the stack? */
    int numberChannels;
    
    /** If true, the the RGB-flag is set on the created stack, if false it is not. */
    boolean makeRGB;
}
