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
import org.anchoranalysis.image.core.stack.Stack;

/**
 * Creates {@link StackWriteOptions} to describe certain attributes.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StackWriteOptionsFactory {

    private static final StackWriteOptions RGB_ALWAYS_2D = new StackWriteOptions(true, true, true);

    private static final StackWriteOptions RGB_MAYBE_3D = new StackWriteOptions(false, true, true);

    private static final StackWriteOptions ONE_OR_THREE_CHANNELS_ALWAYS_2D =
            new StackWriteOptions(true, true, false);

    private static final StackWriteOptions ONE_OR_THREE_CHANNELS_MAYBE_3D =
            new StackWriteOptions(false, true, false);

    /**
     * Creates a {@link StackWriteOptions} which depending on a flag will always be 2D.
     *
     * @param always2D if the stack is guaranteed to be always 2D.
     * @return a newly created {@link StackWriteOptions}
     */
    public static StackWriteOptions maybeAlways2D(boolean always2D) {
        return new StackWriteOptions(always2D, false, false);
    }

    public static StackWriteOptions rgbAlways2D() {
        return RGB_ALWAYS_2D;
    }

    public static StackWriteOptions rgbMaybe3D() {
        return RGB_MAYBE_3D;
    }

    public static StackWriteOptions binaryChannelMaybe3D() {
        return singleChannelMaybe3D(false);
    }

    public static StackWriteOptions singleChannelMaybe3D(boolean always2D) {
        if (always2D) {
            return ONE_OR_THREE_CHANNELS_ALWAYS_2D;
        } else {
            return ONE_OR_THREE_CHANNELS_MAYBE_3D;
        }
    }

    public static StackWriteOptions alwaysOneOrThreeChannels(boolean always2D) {
        return singleChannelMaybe3D(always2D);
    }

    public static StackWriteOptions maybeRGB(boolean rgb, boolean always2D) {
        if (always2D) {
            return new StackWriteOptions(true, rgb, rgb);
        } else {
            return maybeRGB(rgb);
        }
    }

    public static StackWriteOptions maybeRGB(boolean rgb) {
        if (rgb) {
            return RGB_MAYBE_3D;
        } else {
            return new StackWriteOptions(false, false, false);
        }
    }

    public static StackWriteOptions rgb(boolean always2D) {
        if (always2D) {
            return RGB_ALWAYS_2D;
        } else {
            return RGB_MAYBE_3D;
        }
    }

    /**
     * The options that narrowly describe a stack as possible.
     *
     * @param stack the stack to derive options from
     * @return options that narrowly describe {@code stack}.
     */
    public static StackWriteOptions from(Stack stack) {
        int numberChannels = stack.getNumberChannels();
        boolean singleSlice = !stack.hasMoreThanOneSlice();
        if (numberChannels==3) {
            if (stack.isRgb()) {
                return rgb(singleSlice);
            } else {
                return alwaysOneOrThreeChannels(singleSlice);
            }
        } else if (numberChannels == 1) {
            return alwaysOneOrThreeChannels(singleSlice);
        } else {
            return new StackWriteOptions(singleSlice, false, false);
        }
    }
}
