/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.dimensions.size.suggestion;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.core.functional.OptionalUtilities;

/**
 * Extracts a suggested-scale from an array of strings.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class SuggestionFromArray {

    private final String[] array;

    /**
     * Extracts a suggestion with <b>both</b> a width and a height.
     *
     * @param preserveAspectRatio whether to preserve the aspect ratio
     * @return the created suggestion
     * @throws SuggestionFormatException if any array elements is not a positive integer
     */
    public ScaleToSuggestion extractBoth(boolean preserveAspectRatio)
            throws SuggestionFormatException {
        return extract(Optional.of(0), Optional.of(1), preserveAspectRatio);
    }

    /**
     * Extracts a suggestion with <b>either</b> a width or a height.
     *
     * @param multiplexWidth if true, the width is extracted, otherwise the height
     * @return the created suggestion
     * @throws SuggestionFormatException if any array elements is not a positive integer
     */
    public ScaleToSuggestion extractOne(boolean multiplexWidth) throws SuggestionFormatException {
        Optional<Integer> widthPosition = OptionalFactory.create(multiplexWidth, 0);
        Optional<Integer> heightPosition = OptionalFactory.create(!multiplexWidth, 0);
        return extract(widthPosition, heightPosition, true);
    }

    /**
     * Extracts a suggestion that is a scale-factor only.
     *
     * @return the created suggestion.
     * @throws SuggestionFormatException if the suggestion string does not correspond to a
     *     recognized format.
     */
    public ScaleFactorSuggestion extractScaleFactor() throws SuggestionFormatException {
        return new ScaleFactorSuggestion(extractFloat(0));
    }

    /**
     * Creates a {@link ScaleToSuggestion} by extracting particular positions for width and height
     * (or not at all).
     */
    private ScaleToSuggestion extract(
            Optional<Integer> widthPosition,
            Optional<Integer> heightPosition,
            boolean preserveAspectRatio)
            throws SuggestionFormatException {
        try {
            return new ScaleToSuggestion(
                    extractIntegerOptional(widthPosition),
                    extractIntegerOptional(heightPosition),
                    preserveAspectRatio);
        } catch (CreateException e) {
            throw new SuggestionFormatException("Neither a width nor a height is specified");
        }
    }

    /** Like {@link #extractInteger(int)} but takes an {@link Optional} as an parameter. */
    private Optional<Integer> extractIntegerOptional(Optional<Integer> position)
            throws SuggestionFormatException {
        return OptionalUtilities.map(position, pos -> this.extractInteger(pos));
    }

    /** Extracts an integer at a particular position in the array. */
    private int extractInteger(int position) throws SuggestionFormatException {
        String string = array[position]; // NOSONAR
        int value = Integer.parseInt(string);
        checkValue(value);
        return value;
    }

    /** Extracts a float at a particular position in the array. */
    private float extractFloat(int position) throws SuggestionFormatException {
        String string = array[position]; // NOSONAR
        float value = Float.parseFloat(string);
        checkValue(value);
        return value;
    }

    /** Checks that value is positive. */
    private static void checkValue(float value) throws SuggestionFormatException {
        if (value == 0) {
            throw new SuggestionFormatException(
                    "An image resize suggestion must not specify a 0 as width or height");
        }
        if (value < 0) {
            throw new SuggestionFormatException(
                    "An image resize suggestion must not specify a negative-number as width or height");
        }
    }
}
