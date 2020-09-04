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
package org.anchoranalysis.image.stack;

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.converter.attached.ChannelConverterAttached;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

/**
 * Helps a retrieve channel and an associated converter and apply operation on them jointly
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ChannelMapper {

    private IntFunction<Channel> channelGetter;
    private IntFunction<Optional<ChannelConverterAttached<Channel, UnsignedByteBuffer>>> converterGetter;

    public <T> T mapChannelIfSupported(
            int channelIndex,
            BiFunction<Channel, ChannelConverterAttached<Channel, UnsignedByteBuffer>, T> mapper,
            Function<Channel, T> fallback) {

        Channel channel = channelGetter.apply(channelIndex);
        Optional<ChannelConverterAttached<Channel, UnsignedByteBuffer>> optional =
                converterGetter.apply(channelIndex);

        if (optional.isPresent()) {
            return mapper.apply(channel, optional.get());
        } else {
            if (!channel.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {
                // Datatype is not supported
                throw new AnchorFriendlyRuntimeException("Unsupported data-type");
            }
            return fallback.apply(channel);
        }
    }

    public void callChannelIfSupported(
            int channelIndex,
            BiConsumer<Channel, ChannelConverterAttached<Channel, UnsignedByteBuffer>> consumer,
            Consumer<Channel> fallback) {
        // We perform the call via a mapping that returns null
        mapChannelIfSupported(channelIndex, convertConsumer(consumer), convertConsumer(fallback));
    }

    private static <S, T> BiFunction<S, T, Void> convertConsumer(BiConsumer<S, T> consumer) {
        return (input1, input2) -> {
            consumer.accept(input1, input2);
            return null;
        };
    }

    private static <T> Function<T, Void> convertConsumer(Consumer<T> consumer) {
        return input -> {
            consumer.accept(input);
            return null;
        };
    }
}
