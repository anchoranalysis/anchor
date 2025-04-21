/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
