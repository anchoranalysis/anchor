package org.anchoranalysis.image.io.stack;

import org.anchoranalysis.image.core.stack.Stack;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Creates {@link StackWriteOptions} to describe certain attributes.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
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
     * 
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
     * <p>Note that a stack with three channels is assumed to be RGB.
     *
     * @param stack the stack to derive options from
     * @return options that narrowly describe {@code stack}.
     */
    public static StackWriteOptions from(Stack stack) {
        int numberChannels = stack.getNumberChannels();
        boolean singleSlice = !stack.hasMoreThanOneSlice();
        if (numberChannels == 1 || numberChannels==3) {
            return alwaysOneOrThreeChannels(singleSlice);
        } else {
            return new StackWriteOptions(singleSlice, false, false);
        }
    }
}
