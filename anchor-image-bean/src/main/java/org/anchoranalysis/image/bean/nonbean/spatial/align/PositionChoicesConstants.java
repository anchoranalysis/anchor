package org.anchoranalysis.image.bean.nonbean.spatial.align;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;

/**
 * A constant set of text strings used to indicate particular choices.
 *
 * <p>These let these strings be reused within beans consistently.
 *
 * <p>Note that the concept of top and bottom in the Y dimension uses the coordinate system expected
 * by anchor images, where the minimum value indicates the top of the image. Other image processing
 * libraries can use different coordinate systems.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionChoicesConstants {

    // START: strings used for position choices

    /** Align to the left in the X-dimension/ */
    public static final String LEFT = "left";

    /** Align to the right in the X-dimension/ */
    public static final String RIGHT = "right";

    /** Align to the top in the Y-dimension. */
    public static final String TOP = "top";

    /** Align to the center (middle) in the respective dimension. */
    public static final String CENTER = "center";

    /** Align to the bottom in the Y-dimension. */
    public static final String BOTTOM = "bottom";

    /**
     * The choice which will cause a single-slice in the z-dimension to be duplicated across the
     * z-dimension to match the z-size onto which it is projected.
     */
    public static final String REPEAT = "repeat";
    // END: strings used for position choices

    /** The three valid choices for aligning across the <b>X-</b> dimension. */
    private static final PositionChoices CHOICES_X = new PositionChoices(LEFT, CENTER, RIGHT);

    /** The three valid choices for aligning across the <b>Y-</b> dimension. */
    private static final PositionChoices CHOICES_Y = new PositionChoices(TOP, CENTER, BOTTOM);

    /** The four valid choices for aligning across the <b>Z-</b> dimension. */
    private static final PositionChoices CHOICES_Z =
            new PositionChoices(BOTTOM, CENTER, TOP, Optional.of(REPEAT));

    /**
     * How to align on the <b>X-</b>axis.
     *
     * @param fieldValue the text value entered into the field.
     * @return alignment for the respective dimension.
     * @throws BeanMisconfiguredException if {@code fieldValue} contains an unrecognized value.
     */
    public static AlignmentOnDimension alignX(String fieldValue) throws BeanMisconfiguredException {
        return CHOICES_X.alignmentForDimension("alignX", fieldValue);
    }

    /**
     * How to align on the <b>Y-</b>axis.
     *
     * @param fieldValue the text value entered into the field.
     * @return alignment for the respective dimension.
     * @throws BeanMisconfiguredException if {@code fieldValue} contains an unrecognized value.
     */
    public static AlignmentOnDimension alignY(String fieldValue) throws BeanMisconfiguredException {
        return CHOICES_Y.alignmentForDimension("alignY", fieldValue);
    }

    /**
     * How to align on the <b>Z-</b>axis.
     *
     * @param fieldValue the text value entered into the field.
     * @return alignment for the respective dimension.
     * @throws BeanMisconfiguredException if {@code fieldValue} contains an unrecognized value.
     */
    public static AlignmentOnDimension alignZ(String fieldValue) throws BeanMisconfiguredException {
        return CHOICES_Z.alignmentForDimension("alignZ", fieldValue);
    }
}