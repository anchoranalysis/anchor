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

package org.anchoranalysis.image.core.channel.factory;

import java.util.Optional;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFactoryMultiplexer;

/** Creates a {@link Channel} corresponding to one of several data-types. */
public class ChannelFactory extends VoxelDataTypeFactoryMultiplexer<ChannelFactorySingleType> {

    // Singleton
    private static ChannelFactory instance;

    private ChannelFactory() {
        super(
                new ChannelFactoryUnsignedByte(),
                new ChannelFactoryUnsignedShort(),
                new ChannelFactoryUnsignedInt(),
                new ChannelFactoryFloat());
    }

    /**
     * Singleton instance of {@link ChannelFactory}.
     *
     * @return the instance.
     */
    public static ChannelFactory instance() {
        if (instance == null) {
            instance = new ChannelFactory();
        }
        return instance;
    }

    /**
     * Creates an empty initialized channel for a particular data-type.
     *
     * @param dimensions channel dimensions.
     * @param channelDataType data-type.
     * @return a newly created channel with newly created buffers.
     */
    public Channel create(Dimensions dimensions, VoxelDataType channelDataType) {
        return get(channelDataType).createEmptyInitialised(dimensions);
    }

    /**
     * Create a {@link Channel} without initialization with voxel-buffers.
     *
     * @param dimensions the size of the channel.
     * @param channelDataType the data-type of the voxels in the channel.
     * @return the created channel.
     */
    public Channel createUninitialised(Dimensions dimensions, VoxelDataType channelDataType) {
        return get(channelDataType).createEmptyUninitialised(dimensions);
    }

    /**
     * Create a {@link Channel} from particular voxels.
     *
     * @param voxels the voxels to create the channel from.
     * @return the {@link Channel}.
     */
    public Channel create(Voxels<?> voxels) {
        return create(voxels, Optional.empty());
    }

    /**
     * Create a {@link Channel} from particular voxels.
     *
     * @param voxels the voxels to create the channel from.
     * @param resolution the resolution to assign.
     * @return the {@link Channel}.
     */
    public Channel create(Voxels<?> voxels, Optional<Resolution> resolution) {
        return get(voxels.dataType()).create(voxels, resolution);
    }

    /**
     * Creates an empty initialized channel with discrete type with as minimal as needed data-type
     * to support a maximum value.
     *
     * <p>{@code byte}, {@code short} and {@code int} are tried in that order.
     *
     * @param dimensions dimensions.
     * @param maxIntensityValueNeeded the maximum value that the channel's data-type needs to
     *     support.
     * @return a newly created channel of selected data-type with newly created buffers.
     * @throws CreateException if the max value exceeds all supported data types.
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
