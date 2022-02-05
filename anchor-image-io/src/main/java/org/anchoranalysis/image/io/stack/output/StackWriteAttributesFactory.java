/*-
 * #%L
 * anchor-image-io
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
package org.anchoranalysis.image.io.stack.output;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.stack.Stack;

/**
 * Creates {@link StackWriteAttributes} to describe certain attributes.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StackWriteAttributesFactory {

    private static final StackWriteAttributes THREE_CHANNELS_ALWAYS_2D =
            new StackWriteAttributes(true, false, true, StackRGBState.NOT_RGB, false);

    private static final StackWriteAttributes THREE_CHANNELS_MAYBE_3D =
            new StackWriteAttributes(false, false, true, StackRGBState.NOT_RGB, false);

    private static final StackWriteAttributes SINGLE_CHANNEL_ALWAYS_2D =
            new StackWriteAttributes(true, true, false, StackRGBState.NOT_RGB, false);

    private static final StackWriteAttributes SINGLE_CHANNEL_MAYBE_3D =
            new StackWriteAttributes(false, true, false, StackRGBState.NOT_RGB, false);

    private static final StackWriteAttributes BINARY_CHANNEL_MAYBE_3D =
            new StackWriteAttributes(false, true, false, StackRGBState.NOT_RGB, true);

    private static final StackWriteAttributes BINARY_CHANNEL_ALWAYS_2D =
            new StackWriteAttributes(true, true, false, StackRGBState.NOT_RGB, true);

    /**
     * Creates a {@link StackWriteAttributes} which, depending on a flag, may always be 2D.
     *
     * @param always2D when true, this indicates that the stack is guaranteed to be always 2D.
     * @return a newly created {@link StackWriteAttributes}.
     */
    public static StackWriteAttributes maybeAlways2D(boolean always2D) {
        return new StackWriteAttributes(always2D, false, false, StackRGBState.NOT_RGB, false);
    }

    public static StackWriteAttributes rgbMaybe3D(boolean plusAlpha) {
        return new StackWriteAttributes(
                false, false, !plusAlpha, StackRGBState.multiplexAlpha(plusAlpha), false);
    }

    public static StackWriteAttributes binaryChannel(boolean always2D) {
        if (always2D) {
            return BINARY_CHANNEL_ALWAYS_2D;
        } else {
            return BINARY_CHANNEL_MAYBE_3D;
        }
    }

    public static StackWriteAttributes singleChannelMaybe3D(boolean always2D) {
        if (always2D) {
            return SINGLE_CHANNEL_ALWAYS_2D;
        } else {
            return SINGLE_CHANNEL_MAYBE_3D;
        }
    }

    public static StackWriteAttributes maybeRGB(boolean rgb, boolean always2D, boolean plusAlpha) {
        return new StackWriteAttributes(
                always2D, false, !plusAlpha, StackRGBState.multiplex(rgb, plusAlpha), false);
    }

    public static StackWriteAttributes maybeRGBWithoutAlpha(boolean rgb) {
        if (rgb) {
            return new StackWriteAttributes(
                    true, false, true, StackRGBState.RGB_WITHOUT_ALPHA, false);
        } else {
            return new StackWriteAttributes(false, false, false, StackRGBState.NOT_RGB, false);
        }
    }

    public static StackWriteAttributes rgb(boolean always2D, boolean plusAlpha) {
        if (always2D) {
            return new StackWriteAttributes(
                    true, false, !plusAlpha, StackRGBState.multiplexAlpha(plusAlpha), false);
        } else {
            return rgbMaybe3D(plusAlpha);
        }
    }

    /**
     * The options that narrowly describe a stack as possible.
     *
     * @param stack the stack to derive options from
     * @return options that narrowly describe {@code stack}.
     */
    public static StackWriteAttributes from(Stack stack) {
        StackWriteAttributes attributes = fromWithoutBitDepthCheck(stack);

        if (hasAllEightBitChannels(stack)) {
            return attributes.allChannelsEightBit();
        } else {
            return attributes;
        }
    }

    /** Checks if each channel is 8-bit. */
    private static boolean hasAllEightBitChannels(Stack stack) {
        for (Channel channel : stack) {
            if (channel.getVoxelDataType().bitDepth() != 8) {
                return false;
            }
        }
        return true;
    }

    /** The options that narrowly describe a stack as possible, without checking bit-depth. */
    private static StackWriteAttributes fromWithoutBitDepthCheck(Stack stack) {
        int numberChannels = stack.getNumberChannels();
        boolean singleSlice = !stack.hasMoreThanOneSlice();
        if (numberChannels == 3) {
            if (stack.isRGB()) {
                return rgb(singleSlice, false);
            } else {
                return threeChannels(singleSlice);
            }
        } else if (numberChannels == 4 && stack.isRGB()) {
            return rgb(singleSlice, true);
        } else if (numberChannels == 1) {
            return singleChannelMaybe3D(singleSlice);
        } else {
            return new StackWriteAttributes(
                    singleSlice, false, false, StackRGBState.NOT_RGB, false);
        }
    }

    private static StackWriteAttributes threeChannels(boolean always2D) {
        return always2D ? THREE_CHANNELS_ALWAYS_2D : THREE_CHANNELS_MAYBE_3D;
    }
}
