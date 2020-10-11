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
package org.anchoranalysis.image.io.stack;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Options describing stack which may determine which writer is used.
 *
 * <p>This class is <i>immutable</i>.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StackWriteOptions {

    private static final StackWriteOptions RGB_ALWAYS_2D =
            new StackWriteOptions(true, true, true);

    private static final StackWriteOptions RGB_MAYBE_3D =
            new StackWriteOptions(false, true, true);

    private static final StackWriteOptions ONE_OR_THREE_CHANNELS_ALWAYS_2D =
            new StackWriteOptions(true, true, false);

    private static final StackWriteOptions ONE_OR_THREE_CHANNELS_MAYBE_3D =
            new StackWriteOptions(false, true, false);

    /** True the output is guaranteed to only ever 2D i.e. maximally one z-slice? */
    private boolean always2D;

    /** The number of channels is guaranteed to be 1 or 3 in the output. */
    private boolean alwaysOneOrThreeChannels;

    /*** Whether it's an RGB image (exactly three channels visualized jointly, rather than independently) */
    private boolean rgb;

    /**
     * Combines with another {@link StackWriteOptions} by performing a logical <i>and</i> on each
     * field.
     *
     * @param other the other {@link StackWriteOptions} to combine with.
     * @return a newly created {@link StackWriteOptions} where each field is the logical and of the
     *     two inputs
     */
    public StackWriteOptions and(StackWriteOptions other) {
        return new StackWriteOptions(
                always2D && other.always2D,
                alwaysOneOrThreeChannels && other.alwaysOneOrThreeChannels,
                rgb && other.rgb);
    }

    /**
     * Creates a copy of the {@link StackWriteOptions} which will always be 2D.
     *
     * @return a newly created {@link StackWriteOptions} with identical fields, except {@code
     *     always2D} is true.
     */
    public StackWriteOptions always2D() {
        return new StackWriteOptions(true, alwaysOneOrThreeChannels, rgb);
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
}
