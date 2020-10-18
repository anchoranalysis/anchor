/*-
 * #%L
 * anchor-test-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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
            VoxelDataType channelVoxelType,
            boolean firstChannelForced) {
        StringBuilder builder = new StringBuilder();
        builder.append(channelVoxelType.toString());
        builder.append("_");
        builder.append(identifierForNumberChannels(numberChannels, makeRGB));
        builder.append("_");
        builder.append(identifierForDimensions(do3D));
        builder.append("_");
        builder.append(extentIdentifier);
        if (firstChannelForced) {
            builder.append("_firstChannelDifferentType");
        }
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
