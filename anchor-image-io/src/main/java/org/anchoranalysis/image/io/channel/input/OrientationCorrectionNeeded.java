package org.anchoranalysis.image.io.channel.input;

/**
 * Several corrections needed to correct orientation of an image in the XY plane.
 *
 * <p>The corrections refer to what rotation should be applied to the natural XY byte ordering in an
 * image file, to present in the orientation expected in the eventual image.
 *
 * @author Owen Feehan
 */
public enum OrientationCorrectionNeeded {
    /** No rotation is needed. */
    NO_ROTATION,

    /** Pixels should appear 90 degrees rotated in the clockwise direction. */
    ROTATE_90_CLOCKWISE,

    /** Pixels should appear 180 degrees rotated in the clockwise direction. */
    ROTATE_180_CLOCKWISE,

    /** Pixels should appear 270 degrees rotated in the clockwise direction. */
    ROTATE_270_CLOCKWISE
}
