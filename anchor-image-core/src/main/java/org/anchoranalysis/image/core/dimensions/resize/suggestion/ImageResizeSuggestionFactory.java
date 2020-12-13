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
package org.anchoranalysis.image.core.dimensions.resize.suggestion;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;

/**
 * Creates a {@link ImageResizeSuggestion}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageResizeSuggestionFactory {

    /**
     * Creates a suggestion for how to resize an image.
     *
     * <p>Various suggestions can be specified as follows:
     *
     * <ul>
     *   <li>{@code scaleFactor} &rarr; resize to size where width and height are multiplied by
     *       {@code scaleFactor}, preserving aspect ratio e.g. {@code 0.5} or {@code 2}
     *   <li>{@code width}x{@code height} &rarr; resize to a fixed size, <b>without</b> preserving
     *       aspect ratio e.g. {@code 1024x768}
     *   <li>{@code width}x &rarr; resize to a fixed width, preserving aspect ratio e.g. {@code
     *       1024x}
     *   <li>x{@code height} &rarr; resize to a fixed height, preserving aspect ratio e.g. {@code
     *       x768}
     *   <li>{@code width}x{@code height}+ &rarr; resize to maximally contained within a fixed size,
     *       preserving aspect ratio e.g. {@code 1024x768+}
     * </ul>
     *
     * <p>A plus at the end can optionally be validly, placed on any of formats which preserve
     * aspect ratio, but not on the second option.
     *
     * <p>Floating point dimensions are not allowed, so an integer width and height will always be
     * ultimately selected.
     *
     * @param suggestion a string in the format described above.
     * @return a newly created suggestion
     * @throws SuggestionFormatException if the suggestion string does not correspond to a
     *     recognized format
     */
    public static ImageResizeSuggestion create(String suggestion) throws SuggestionFormatException {

        // First try matching against the "do not preserve" and then try the "preserve".
        return matchAll(suggestion)
                .orElseThrow(
                        () ->
                                new SuggestionFormatException(
                                        String.format(
                                                "The suggestion %s does not have a recognised format.",
                                                suggestion)));
    }

    /**
     * Tries to match against all patterns, first those with both width and height, and then
     * anything else (where the plus is irrelevant).
     */
    private static Optional<ImageResizeSuggestion> matchAll(String suggestion)
            throws SuggestionFormatException {
        return OptionalUtilities.orElseGetFlat(
                MatchHelper.matchBothWidthAndHeight(
                        suggestion,
                        false), // Both width and height. Does not preserve aspect-ratio.
                () ->
                        MatchHelper.matchBothWidthAndHeight(
                                suggestion,
                                true), // Both width and height. Does preserve aspect-ratio.
                () -> matchWithoutTrailingPlus(removeTrailingPlus(suggestion)) // Everything else
                );
    }

    private static Optional<ImageResizeSuggestion> matchWithoutTrailingPlus(String suggestion)
            throws SuggestionFormatException {
        return OptionalUtilities.orElseGetFlat(
                MatchHelper.matchPreserveNotBoth(
                        suggestion), // Either width or height. Does preserve aspect-ratio.
                () -> MatchHelper.matchScaleFactor(suggestion) // Matches a scale factor
                );
    }

    /**
     * Removes a trailing plus-character from a string it exists, or otherwise returns the string
     * unchanged.
     */
    private static String removeTrailingPlus(String string) {
        if (string.endsWith("+")) {
            return string.substring(0, string.length() - 1);
        } else {
            return string;
        }
    }
}
