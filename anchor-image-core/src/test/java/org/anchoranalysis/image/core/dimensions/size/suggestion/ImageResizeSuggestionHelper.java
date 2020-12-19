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

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ImageResizeSuggestionHelper {

    public static void testScaleTo(
            String suggestionAsString,
            Optional<Integer> expectedWidth,
            Optional<Integer> expectedHeight,
            boolean expectedPreserveAspectRatio)
            throws SuggestionFormatException {
        try {
            assertScaleTo(
                    test(suggestionAsString),
                    expectedWidth,
                    expectedHeight,
                    expectedPreserveAspectRatio);
        } catch (CreateException e) {
            throw new AnchorFriendlyRuntimeException(e);
        }
    }

    public static void testScaleFactor(String suggestionAsString, float expectedScaleFactor)
            throws SuggestionFormatException {
        assertScaleFactor(test(suggestionAsString), expectedScaleFactor);
    }

    public static ImageSizeSuggestion test(String suggestionAsString)
            throws SuggestionFormatException {
        return ImageSizeSuggestionFactory.create(suggestionAsString);
    }

    private static void assertScaleTo(
            ImageSizeSuggestion suggestion,
            Optional<Integer> expectedWidth,
            Optional<Integer> expectedHeight,
            boolean expectPreserveAspectRatio)
            throws CreateException {
        assertEquals(
                new ScaleToSuggestion(expectedWidth, expectedHeight, expectPreserveAspectRatio),
                suggestion);
    }

    private static void assertScaleFactor(
            ImageSizeSuggestion suggestion, float expectedScaleFactor) {
        assertEquals(new ScaleFactorSuggestion(expectedScaleFactor), suggestion);
    }
}
