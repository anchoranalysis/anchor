/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.channel.factory;

import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFactoryMultiplexer;

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

    /**
     * Creates an empty initialized channel for a particular data-type
     *
     * @param dimensions channel dimensions
     * @param channelDataType data-type
     * @return a newly created channel with newly created buffers
     */
    public Channel create(Dimensions dimensions, VoxelDataType channelDataType) {
        return get(channelDataType).createEmptyInitialised(dimensions);
    }

    public Channel createUninitialised(Dimensions dimensions, VoxelDataType channelDataType) {
        return get(channelDataType).createEmptyUninitialised(dimensions);
    }

    public Channel create(Voxels<?> voxels) {
        return create(voxels, Optional.empty());
    }

    public Channel create(Voxels<?> voxels, Optional<Resolution> resolution) {
        return get(voxels.dataType()).create(voxels, resolution);
    }

    /**
     * Creates an empty initialized channel with discrete type with as minimal as needed data-type
     * to support a maximum value
     *
     * <p>byte, short and int are tried in that order.
     *
     * @param dimensions dimensions
     * @param maxIntensityValueNeeded the maximum value that the channel's data-type needs to
     *     support
     * @return a newly created channel of selected data-type with newly created buffers
     * @throws CreateException if the max value exceeds all supported data types
     */
    public Channel createEmptyInitialisedToSupportMaxValue(
            Dimensions dimensions, long maxIntensityValueNeeded) throws CreateException {
        return create(dimensions, selectDataTypeToSupport(maxIntensityValueNeeded));
    }

    private static VoxelDataType selectDataTypeToSupport(long maxIntensityValueNeeded)
            throws CreateException {
        if (maxIntensityValueNeeded < UnsignedByteVoxelType.INSTANCE.maxValue()) {
            return UnsignedByteVoxelType.INSTANCE;
        } else if (maxIntensityValueNeeded < UnsignedShortVoxelType.INSTANCE.maxValue()) {
            return UnsignedShortVoxelType.INSTANCE;
        } else if (maxIntensityValueNeeded < UnsignedIntVoxelType.INSTANCE.maxValue()) {
            return UnsignedIntVoxelType.INSTANCE;
        } else {
            throw new CreateException(
                    "No data-type can support a max intensity-value of " + maxIntensityValueNeeded);
        }
    }
}
