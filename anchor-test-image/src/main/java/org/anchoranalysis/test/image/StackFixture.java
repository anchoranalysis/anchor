package org.anchoranalysis.test.image;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.test.image.ChannelFixture.IntensityFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Creates stacks of 1 or more channels using {@link ChannelFixture}.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class StackFixture {

    /**
     * Creates a stack with a particular number of the channels of particular size.
     * 
     * @param numberChannels how many channels in the stack?
     * @param extent the size of each channel.
     * @return the newly created-stack with newly-created channels.
     */
    public static Stack create(int numberChannels, Extent extent) {
        Stream<Channel> channels = IntStream.range(0, numberChannels).mapToObj( index ->
            ChannelFixture.createChannel(extent, multiplexIntensity(index))
        );
        try {
            return new Stack(channels);
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
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
        switch(channelIndex) {
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
