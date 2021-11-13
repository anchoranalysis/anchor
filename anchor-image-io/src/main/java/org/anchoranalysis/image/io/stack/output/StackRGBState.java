package org.anchoranalysis.image.io.stack.output;

/**
 * Whether a stack is RGB or RGBA or neither.
 *
 * @author Owen Feehan
 */
public enum StackRGBState {

    /** Not a RGB image. */
    NOT_RGB,

    /** RGB without any alpha channel. */
    RGB_WITHOUT_ALPHA,

    /** RGB with a alpha channel. */
    RGB_WITH_ALPHA;

    /**
     * Returns one of the three {@code RGB_} states based upon two boolean flags.
     *
     * @param rgb true if RGB or RGBA, false otherwise.
     * @param withAlpha true if alpha is present, false if it is not. Ignored when {@code
     *     rgb==false}.
     * @return {@code RGB_WITHOUT_ALPHA} or {@code RGB_WITH_ALPHA} as matches {@code withAlpha}.
     */
    public static StackRGBState multiplex(boolean rgb, boolean withAlpha) {
        if (rgb) {
            return multiplexAlpha(withAlpha);
        } else {
            return NOT_RGB;
        }
    }

    /**
     * Returns one of the two {@code RGB_} states based upon a boolean flag.
     *
     * @param withAlpha true if alpha is present, false if it is not.
     * @return {@code RGB_WITHOUT_ALPHA} or {@code RGB_WITH_ALPHA} as matches {@code withAlpha}.
     */
    public static StackRGBState multiplexAlpha(boolean withAlpha) {
        if (withAlpha) {
            return RGB_WITH_ALPHA;
        } else {
            return RGB_WITHOUT_ALPHA;
        }
    }

    /**
     * Finds the <b>minimum</b> of this value and another, according to an ordering.
     *
     * <p>The ordering is increasingly: {@code NOT_RGB}, {@code RGB_WITHOUT_ALPHA}, {@code
     * RGB_WITH_ALPHA}.
     *
     * @param other the other.
     * @return the minimum state among the two, according to the ordering.
     */
    public StackRGBState min(StackRGBState other) {
        if (this == NOT_RGB || other == NOT_RGB) {
            return NOT_RGB;
        } else if (this == RGB_WITHOUT_ALPHA || other == RGB_WITHOUT_ALPHA) {
            return RGB_WITHOUT_ALPHA;
        } else {
            return RGB_WITH_ALPHA;
        }
    }

    /**
     * Finds the <b>maximum</b> of this value and another, according to an ordering.
     *
     * <p>The ordering is increasingly: {@code NOT_RGB}, {@code RGB_WITHOUT_ALPHA}, {@code
     * RGB_WITH_ALPHA}.
     *
     * @param other the other.
     * @return the maximum state among the two, according to the ordering.
     */
    public StackRGBState max(StackRGBState other) {
        if (this == RGB_WITH_ALPHA || other == RGB_WITH_ALPHA) {
            return RGB_WITH_ALPHA;
        } else if (this == RGB_WITHOUT_ALPHA || other == RGB_WITHOUT_ALPHA) {
            return RGB_WITHOUT_ALPHA;
        } else {
            return NOT_RGB;
        }
    }
}
