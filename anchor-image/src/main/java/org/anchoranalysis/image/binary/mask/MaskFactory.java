package org.anchoranalysis.image.binary.mask;

import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Helper routines to create new instances of {@link Mask}
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class MaskFactory {

    /**
     * Creates a binary-mask for specific dimensions with all voxels set to OFF
     * 
     * <p>The mask uses default binary-values of OFF (0) and ON (255)
     * 
     * @param dimensions the dimensions to create the mask for
     * 
     * @return a newly created binary-mask with newly-created buffers
     */
    public static Mask createMaskOff(Dimensions dimensions) {
        return createMaskOff(dimensions, BinaryValues.getDefault());
    }
    
    /**
     * Creates a binary-mask for specific dimensions with all voxels set to OFF
     * 
     * @param dimensions the dimensions to create the mask for
     * @param binaryValues binary-values
     * 
     * @return a newly created binary-mask with newly-created buffers
     */
    public static Mask createMaskOff(Dimensions dimensions, BinaryValues binaryValues) {
        Mask mask = new Mask(
                ChannelFactory.instance()
                .create(dimensions, UnsignedByteVoxelType.INSTANCE), binaryValues     
        );
        // By default the voxels are 0. If OFF value is not 0, it needs to be explicitly assigned.
        if (binaryValues.getOffInt()!=0) {
            mask.assignOff().toAll();
        }
        return mask;
    }

    /**
     * Creates a binary-mask for specific dimensions with all voxels set to ON
     * 
     * <p>The mask uses default binary-values of OFF (0) and ON (255)
     * 
     * @param dimensions the dimensions to create the mask for
     * 
     * @return a newly created binary-mask with newly-created buffers
     */
    public static Mask createMaskOn(Dimensions dimensions) {
        return createMaskOn(dimensions, BinaryValues.getDefault());
    }
    
    /**
     * Creates a binary-mask for specific dimensions with all voxels set to ON
     * 
     * @param dimensions the dimensions to create the mask for
     * @param binaryValues binary-values
     * 
     * @return a newly created binary-mask with newly-created buffers
     */
    public static Mask createMaskOn(Dimensions dimensions, BinaryValues binaryValues) {
        Mask mask = createMaskOff(dimensions, binaryValues);
        mask.assignOn().toAll();
        return mask;
    }
}
