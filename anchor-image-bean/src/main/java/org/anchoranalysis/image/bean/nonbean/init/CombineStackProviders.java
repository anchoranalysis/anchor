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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.identifier.provider.NamedProviderBridge;
import org.anchoranalysis.core.identifier.provider.NamedProviderCombine;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.channel.factory.ChannelFactoryUnsignedByte;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.mask.Mask;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * An implementation of {@link NamedProvider} that combines several different entities that have natural {@link Stack} representations.
 * 
 * <p>If multiple sources have the same identifier, only one identifier (arbitrarily selected) will exist in the unified {@link NamedProvider}.
 * 
 * @author Owen
 *
 */
class CombineStackProviders implements NamedProvider<Stack> {

    private static final ChannelFactorySingleType FACTORY = new ChannelFactoryUnsignedByte();

    /** The combined-provider. */
    private final NamedProvider<Stack> combined;

    /**
     * Create with the three {@link NamedProvider}s to combine.
     * 
     * @param stacks provides the {@link Stack}s.
     * @param channels provides the {@link Channel}s.
     * @param masks provides the {@link Mask}s.
     */
    public CombineStackProviders(
            NamedProvider<Stack> stacks,
            NamedProvider<Channel> channels,
            NamedProvider<Mask> masks) {
        combined = new NamedProviderCombine<>(Stream.of(stacks, convertChannels(channels), convertMasks(masks)));
    }

    @Override
    public Optional<Stack> getOptional(String key) throws NamedProviderGetException {
        return combined.getOptional(key);
    }

    @Override
    public Set<String> keys() {
        return combined.keys();
    }

    /** Converts each {@link Channel} to a {@link Stack}. */
    private static NamedProviderBridge<Channel, Stack> convertChannels(NamedProvider<Channel> channels) {
        return new NamedProviderBridge<>(channels, Stack::new, false);
    }

    /** Converts each {@link Mask} to a {@link Stack}. */
    private static NamedProviderBridge<Mask, Stack> convertMasks(NamedProvider<Mask> masks) {
        return new NamedProviderBridge<>(
                masks, CombineStackProviders::stackFromMask, false);
    }

    /** Converts a single {@link Mask} to a {@link Stack}. */
    private static Stack stackFromMask(Mask sourceObject) {

        Channel channelNew = FACTORY.createEmptyInitialised(sourceObject.dimensions());

        BinaryVoxels<UnsignedByteBuffer> voxels = sourceObject.binaryVoxels();

        // For each region we get a mask for what equals the binary mask
        ObjectMask object =
                voxels.voxels()
                        .extract()
                        .voxelsEqualTo(voxels.binaryValues().getOn())
                        .deriveObject(new BoundingBox(voxels.extent()));
        try {
            channelNew.replaceVoxels(object.voxels());
        } catch (IncorrectImageSizeException e) {
            assert false;
        }
        return new Stack(channelNew);
    }
}
