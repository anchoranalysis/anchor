package org.anchoranalysis.test.image;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.test.image.ChannelFixture.IntensityFunction;

/**
 * Creates stacks of 1 or more channels using {@link ChannelFixture}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor @AllArgsConstructor
public class StackFixture {
    
    /** If defines, specifies the voxel-data type of the first channel, taking precedence over the {@code channelsVoxelType} argument. */
    private Optional<VoxelDataType> firstChannelVoxelType = Optional.empty();
    
    /**
     * Creates a stack with a particular number of the channels of particular size.
     *
     * @param numberChannels how many channels in the stack?
     * @param extent the size of each channel.
     * @param defaultChannelVoxelType voxel data-type for all created channels if not otherwise specified. Note that {@code firstChannelVoxelType} takes precedence for the first channel, if defined.
     * @return the newly created-stack with newly-created channels.
     */
    public Stack create(int numberChannels, Extent extent, VoxelDataType defaultChannelVoxelType) {
        Stream<Channel> channels =
                IntStream.range(0, numberChannels)
                        .mapToObj(
                                index -> createChannel(index, extent, defaultChannelVoxelType) );
        try {
            return new Stack(channels);
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
    }
    
    private Channel createChannel(int index, Extent extent, VoxelDataType defaultChannelVoxelType) {
        
        VoxelDataType voxelType = (index==0 && firstChannelVoxelType.isPresent()) ? firstChannelVoxelType.get() : defaultChannelVoxelType;
        
        return ChannelFixture.createChannel(
                extent,
                multiplexIntensity(index),
                voxelType
        );
    }

    /**
     * Different intensity-functions for the first three channels so they look differently.
     *
     * <p>A fourth or greater channel is identical to the first channel.
     *
     * @param channelIndex the index of the channel
     * @return an intensity-function to use for creating a channel at that particualr index
     */
    private static IntensityFunction multiplexIntensity(int channelIndex) {
        switch (channelIndex) {
            case 0:
                return ChannelFixture::sumMod;
            case 1:
                return ChannelFixture::diffMod;
            case 2:
                return ChannelFixture::multMod;
            default:
                return ChannelFixture::sumMod;
        }
    }
}