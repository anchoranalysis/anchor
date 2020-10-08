package org.anchoranalysis.test.image.rasterwriter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * Builds an identifier (a filename without extension) for a saved-raster corresponding to certain
 * properties.
 *
 * <p>This is used to locate a saved-raster in the resources to compare against.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentifierHelper {

    /**
     * Builds an identifier for particular properties.
     *
     * @param numberChannels the number of channels
     * @param makeRGB whether a RGB image or not
     * @param do3D whether a 3D stack or not
     * @param extentIdentifier an identifier referring to the size of hte image
     * @param channelVoxelType the data-type of voxel in the channel(s)
     * @return a string providing an identifier for the particular combination of attributes.
     */
    public static String identiferFor(
            int numberChannels,
            boolean makeRGB,
            boolean do3D,
            String extentIdentifier,
            VoxelDataType channelVoxelType) {
        StringBuilder builder = new StringBuilder();
        builder.append(channelVoxelType.toString());
        builder.append("_");
        builder.append(identifierForNumberChannels(numberChannels, makeRGB));
        builder.append("_");
        builder.append(identifierForDimensions(do3D));
        builder.append("_");
        builder.append(extentIdentifier);
        return builder.toString();
    }

    private static String identifierForDimensions(boolean do3D) {
        if (do3D) {
            return "3D";
        } else {
            return "2D";
        }
    }

    private static String identifierForNumberChannels(int numberChannels, boolean makeRGB) {
        switch (numberChannels) {
            case 1:
                return "singleChannel";
            case 2:
                return "twoChannels";
            case 3:
                return makeRGB ? "threeChannelsRGB" : "threeChannelsSeparate";
            case 4:
                return "fourChannels";
            default:
                throw new IllegalArgumentException(
                        "Unsupported number of channels: " + numberChannels);
        }
    }
}
