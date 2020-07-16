/* (C)2020 */
package org.anchoranalysis.image.bean.nonbean.init;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderBridge;
import org.anchoranalysis.core.name.provider.NamedProviderCombine;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.Stack;

class CombineDiverseProvidersAsStacks implements NamedProvider<Stack> {

    private static final ChannelFactorySingleType FACTORY = new ChannelFactoryByte();

    private final NamedProvider<Stack> stacks;
    private final NamedProvider<Channel> channels;
    private final NamedProvider<Mask> masks;

    @Getter private final NamedProvider<Stack> combinedStackProvider;

    public CombineDiverseProvidersAsStacks(
            NamedProvider<Stack> stacks,
            NamedProvider<Channel> channels,
            NamedProvider<Mask> masks) {
        this.stacks = stacks;
        this.channels = channels;
        this.masks = masks;

        combinedStackProvider = createCombinedStackProvider();
    }

    @Override
    public Optional<Stack> getOptional(String key) throws NamedProviderGetException {
        return combinedStackProvider.getOptional(key);
    }

    @Override
    public Set<String> keys() {
        return combinedStackProvider.keys();
    }

    private NamedProvider<Stack> createCombinedStackProvider() {
        NamedProviderCombine<Stack> combined = new NamedProviderCombine<>();
        combined.add(stacks);
        combined.add(channelsBridge());
        combined.add(masksBridge());
        return combined;
    }

    private NamedProviderBridge<Channel, Stack> channelsBridge() {
        return new NamedProviderBridge<>(channels, Stack::new, false);
    }

    private NamedProviderBridge<Mask, Stack> masksBridge() {
        return new NamedProviderBridge<>(
                masks, CombineDiverseProvidersAsStacks::stackFromBinary, false);
    }

    private static Stack stackFromBinary(Mask sourceObject) {

        Channel chnlNew = FACTORY.createEmptyInitialised(sourceObject.getDimensions());

        BinaryVoxelBox<ByteBuffer> bvb = sourceObject.binaryVoxelBox();

        // For each region we get a mask for what equals the binary mask
        ObjectMask object =
                bvb.getVoxelBox()
                        .equalMask(
                                new BoundingBox(bvb.getVoxelBox().extent()),
                                bvb.getBinaryValues().getOnInt());
        try {
            chnlNew.getVoxelBox().asByte().replaceBy(object.getVoxelBox());
        } catch (IncorrectImageSizeException e) {
            assert false;
        }
        return new Stack(chnlNew);
    }
}
