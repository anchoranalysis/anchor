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

import static org.anchoranalysis.image.core.dimensions.size.suggestion.ImageResizeSuggestionHelper.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ImageSizeSuggestionFactory}.
 *
 * @author Owen Feehan
 */
class ImageResizeSuggestionFactoryTest {

    /**
     * Tests a suggestion with <b>both width and height</b> that <b>does not</b> preserve aspect
     * ratio.
     */
    @Test
    void testBothNoPreserve() throws SuggestionFormatException {
        testScaleTo("67x43", Optional.of(67), Optional.of(43), false);
    }

    /**
     * Tests a suggestion with <b>both width and height</b> that <b>does</b> preserve aspect ratio.
     */
    @Test
    void testBothPreserve() throws SuggestionFormatException {
        testScaleTo("31x92+", Optional.of(31), Optional.of(92), true);
    }

    /** Tests a suggestion with <b>width only</b> that <b>does</b> preserve aspect ratio. */
    @Test
    void testWidthOnly() throws SuggestionFormatException {
        testScaleTo("31x", Optional.of(31), Optional.empty(), true);
    }

    /**
     * Tests a suggestion with <b>width only</b> that <b>does</b> preserve aspect ratio - and
     * contains a trailing plus.
     */
    @Test
    void testWidthOnlyWithPlis() throws SuggestionFormatException {
        testScaleTo("31x+", Optional.of(31), Optional.empty(), true);
    }

    /** Tests a suggestion with <b>width only</b> that <b>does</b> preserve aspect ratio. */
    @Test
    void testHeightOnly() throws SuggestionFormatException {
        testScaleTo("x78", Optional.empty(), Optional.of(78), true);
    }

    /**
     * Tests a suggestion with <b>both width and height</b>, one of which is a zero, which is not
     * allowed.
     */
    @Test
    void testBothWithZero() {
        assertThrows(SuggestionFormatException.class, () -> test("67x0"));
    }

    /**
     * Tests a suggestion with <b>both width and height</b>, one of which is negative, which is not
     * allowed.
     */
    @Test
    void testBothWithNegative() {
        assertThrows(SuggestionFormatException.class, () -> test("-4x20"));
    }

    /** Tests a <b>integer</b> scale-factor. */
    @Test
    void testScaleFactorInteger() throws SuggestionFormatException {
        testScaleFactor("7", 7);
    }

    /** Tests a <b>floating-point</b> scale-factor. */
    @Test
    void testScaleFactorFloat() throws SuggestionFormatException {
        testScaleFactor("0.33", 0.33f);
    }

    /** Tests a <b>zero-valued</b> scale-factor. */
    @Test
    void testScaleFactorWithZero() {
        assertThrows(SuggestionFormatException.class, () -> test("0"));
    }

    /** Tests a <b>negative-valued</b> scale-factor. */
    @Test
    void testScaleFactorWithNegative() {
        assertThrows(SuggestionFormatException.class, () -> test("-8.9"));
    }

    /** Tests an empty string. */
    @Test
    void testEmpty() {
        assertThrows(SuggestionFormatException.class, () -> test(""));
    }

    /** Tests non-numeric text. */
    @Test
    void testNonNumericText() {
        assertThrows(SuggestionFormatException.class, () -> test("fooBar"));
    }
}
