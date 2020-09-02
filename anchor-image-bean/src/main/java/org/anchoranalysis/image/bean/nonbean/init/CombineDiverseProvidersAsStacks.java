/*-
 * #%L
 * anchor-image-bean
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
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.extent.box.BoundingBox;
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

        Channel channelNew = FACTORY.createEmptyInitialised(sourceObject.dimensions());

        BinaryVoxels<ByteBuffer> bvb = sourceObject.binaryVoxels();

        // For each region we get a mask for what equals the binary mask
        ObjectMask object =
                bvb.voxels()
                        .extract()
                        .voxelsEqualTo(bvb.binaryValues().getOnInt())
                        .deriveObject(new BoundingBox(bvb.voxels()));
        try {
            channelNew.replaceVoxels(object.voxels());
        } catch (IncorrectImageSizeException e) {
            assert false;
        }
        return new Stack(channelNew);
    }
}
