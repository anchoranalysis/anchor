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
 * Creates {@link StackWriteAttributes} to describe certain attributes.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StackWriteAttributesFactory {

    private static final StackWriteAttributes RGB_ALWAYS_2D = new StackWriteAttributes(true, false, true, true, false);

    private static final StackWriteAttributes RGB_MAYBE_3D = new StackWriteAttributes(false, false, true, true, false);

    private static final StackWriteAttributes THREE_CHANNELS_ALWAYS_2D =
            new StackWriteAttributes(true, false, true, false, false);

    private static final StackWriteAttributes THREE_CHANNELS_MAYBE_3D =
            new StackWriteAttributes(false, false, true, false, false);
    
    private static final StackWriteAttributes SINGLE_CHANNEL_ALWAYS_2D =
            new StackWriteAttributes(true, true, false, false, false);

    private static final StackWriteAttributes SINGLE_CHANNEL_MAYBE_3D =
            new StackWriteAttributes(false, true, false, false, false);
    
    private static final StackWriteAttributes BINARY_CHANNEL_MAYBE_3D =
            new StackWriteAttributes(false, true, false, false, true);
    
    private static final StackWriteAttributes BINARY_CHANNEL_ALWAYS_2D =
            new StackWriteAttributes(true, true, false, false, true);      

    /**
     * Creates a {@link StackWriteAttributes} which depending on a flag will always be 2D.
     *
     * @param always2D if the stack is guaranteed to be always 2D.
     * @return a newly created {@link StackWriteAttributes}
     */
    public static StackWriteAttributes maybeAlways2D(boolean always2D) {
        return new StackWriteAttributes(always2D, false, false, false, false);
    }

    public static StackWriteAttributes rgbAlways2D() {
        return RGB_ALWAYS_2D;
    }

    public static StackWriteAttributes rgbMaybe3D() {
        return RGB_MAYBE_3D;
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

    public static StackWriteAttributes maybeRGB(boolean rgb, boolean always2D) {
        if (always2D) {
            return new StackWriteAttributes(true, false, rgb, rgb, false);
        } else {
            return maybeRGB(rgb);
        }
    }

    public static StackWriteAttributes maybeRGB(boolean rgb) {
        if (rgb) {
            return RGB_MAYBE_3D;
        } else {
            return new StackWriteAttributes(false, false, false, false, false);
        }
    }

    public static StackWriteAttributes rgb(boolean always2D) {
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
    public static StackWriteAttributes from(Stack stack) {
        int numberChannels = stack.getNumberChannels();
        boolean singleSlice = !stack.hasMoreThanOneSlice();
        if (numberChannels == 3) {
            if (stack.isRGB()) {
                return rgb(singleSlice);
            } else {
                return threeChannels(singleSlice);
            }
        } else if (numberChannels == 1) {
            return singleChannelMaybe3D(singleSlice);
        } else {
            return new StackWriteAttributes(singleSlice, false, false, false, false);
        }
    }

    private static StackWriteAttributes threeChannels(boolean always2D) {
        return always2D ? THREE_CHANNELS_ALWAYS_2D : THREE_CHANNELS_MAYBE_3D;
    }
}
