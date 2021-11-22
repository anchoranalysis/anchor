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
package org.anchoranalysis.image.core.stack;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.convert.attached.ChannelConverterAttached;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

/**
 * Helps retrieve channel and an associated converter and apply operation on them jointly.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ChannelMapper {

    /** Gets a {@link Channel} corresponding to a particular index (zero-indexed). */
    private IntFunction<Channel> channelGetter;

    /** Gets a converter corresponding to a channel at a particular index (zero-indexed). */
    private IntFunction<Optional<ChannelConverterAttached<Channel, UnsignedByteBuffer>>>
            converterGetter;

    /**
     * Maps a {@link Channel} with {@code mapFunction} if a corresponding converter exists,
     * otherwise with {@code fallback}.
     *
     * @param <T> the destination type to map to.
     * @param channelIndex the index of the channel to map, as well as the corresponding converter.
     * @param mapFunction used for the mapping, if a corresponding converter for {@code
     *     channelIndex} <b>exists</b>.
     * @param fallback used for the mapping, if a corresponding converter for {@code channelIndex}
     *     <b>does not exist</b>.
     * @return the result of the mapping.
     */
    public <T> T mapChannelIfSupported(
            int channelIndex,
            BiFunction<Channel, ChannelConverterAttached<Channel, UnsignedByteBuffer>, T>
                    mapFunction,
            Function<Channel, T> fallback) {

        Channel channel = channelGetter.apply(channelIndex);
        Optional<ChannelConverterAttached<Channel, UnsignedByteBuffer>> convertor =
                converterGetter.apply(channelIndex);

        if (convertor.isPresent()) {
            return mapFunction.apply(channel, convertor.get());
        } else {
            if (!channel.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {
                // Datatype is not supported
                throw new AnchorFriendlyRuntimeException("Unsupported data-type");
            }
            return fallback.apply(channel);
        }
    }

    /**
     * Like {@link #mapChannelIfSupported(int, BiFunction, Function)} but the mapping has no
     * return-type.
     *
     * @param channelIndex the index of the channel to consume, as well as the corresponding
     *     converter.
     * @param consumer used for the consuming, if a corresponding converter for {@code channelIndex}
     *     <b>exists</b>.
     * @param fallback used for the consuming, if a corresponding converter for {@code channelIndex}
     *     <b>does not exist</b>.
     */
    public void consumeChannelIfSupported(
            int channelIndex,
            BiConsumer<Channel, ChannelConverterAttached<Channel, UnsignedByteBuffer>> consumer,
            Consumer<Channel> fallback) {
        // We perform the call via a mapping that returns null
        mapChannelIfSupported(channelIndex, convertBiConsumer(consumer), convertConsumer(fallback));
    }

    /** Wraps a {@link BiConsumer} as a {@link BiFunction} that always returns a null value. */
    private static <S, T> BiFunction<S, T, Void> convertBiConsumer(BiConsumer<S, T> consumer) {
        return (input1, input2) -> {
            consumer.accept(input1, input2);
            return null;
        };
    }

    /** Wraps a {@link Consumer} as a {@link Function} that always returns a null value. */
    private static <T> Function<T, Void> convertConsumer(Consumer<T> consumer) {
        return input -> {
            consumer.accept(input);
            return null;
        };
    }
}
