package org.anchoranalysis.image.bean.nonbean.spatial.align;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;

/**
 * The choices of text that may describe the position of an entity along a particular axis.
 *
 * <p>All choices are case insensitive.
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class PositionChoices {

    // START ARGUMENTS
    /** The text to describe the <b>minimum</b> position along the axis. */
    private final String textMin;

    /** The text to describe the <b>center</b> position along the axis. */
    private final String textCenter;

    /** The text to describe the <b>maximum</b> position along the axis. */
    private final String textMax;

    /**
     * An additional alternative text to describe the <b>minimum</b> position along the axis, if one
     * exists.
     */
    private Optional<String> textMinAlternative = Optional.empty();
    // END ARGUMENTS

    /**
     * Calculates how to align a particular axis.
     *
     * @param fieldName the name of the field where the position was specified.
     * @param fieldValue the value of the field.
     * @return the minimum corner on the particular axis to locate the overlay.
     * @throws BeanMisconfiguredException if an invalid value for a field was used.
     */
    public AlignmentOnDimension alignmentForDimension(String fieldName, String fieldValue)
            throws BeanMisconfiguredException {
        if (fieldValue.equalsIgnoreCase(textMin) || matchesAlternative(fieldValue)) {
            return AlignmentOnDimension.MIN;
        } else if (fieldValue.equalsIgnoreCase(textCenter)) {
            return AlignmentOnDimension.CENTER;
        } else if (fieldValue.equalsIgnoreCase(textMax)) {
            return AlignmentOnDimension.MAX;
        } else {
            String describeAllChoices = String.join(", ", allChoices());
            throw new BeanMisconfiguredException(
                    String.format(
                            "The string '%s' is an invalid value for field %s. Acceptable values are: %s",
                            fieldValue, fieldName, describeAllChoices));
        }
    }

    /** All possible textual choices to describe a position. */
    private List<String> allChoices() {
        if (textMinAlternative.isPresent()) {
            return Arrays.asList(textMin, textCenter, textMax, textMinAlternative.get());
        } else {
            return Arrays.asList(textMin, textCenter, textMax);
        }
    }

    /** If a alternative minimum text exists <b>and</b> it is identical to {@code alignText}. */
    private boolean matchesAlternative(String fieldValue) {
        if (textMinAlternative.isPresent()) {
            return fieldValue.equalsIgnoreCase(textMinAlternative.get());
        } else {
            return false;
        }
    }
}
