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
import org.anchoranalysis.bean.shared.regex.RegEx;
import org.anchoranalysis.bean.shared.regex.RegExSimple;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class MatchHelper {

    /** A regular expression for a suggestion that specifies <b>both width and height<b>, but does <b>not preserve aspect ratio</b>. */
    private static final RegEx REG_EX_BOTH_DO_NOT_PRESERVE = new RegExSimple("^(\\d+)x(\\d+)$");
    
    /** A regular expression for a suggestion that specifies <b>both width and height<b>, but does <b>preserve aspect ratio</b>. */
    private static final RegEx REG_EX_BOTH_PRESERVE = new RegExSimple("^(\\d+)x(\\d+)\\+$");
    
    /** A regular expression for a suggestion that specifies <b>width only<b> (assuming any trailing plus is removed). */
    private static final RegEx REG_EX_WIDTH_ONLY = new RegExSimple("^(\\d+)x$");
    
    /** A regular expression for a suggestion that specifies <b>height only<b> (assuming any trailing plus is removed). */
    private static final RegEx REG_EX_HEIGHT_ONLY = new RegExSimple("^x(\\d+)$");
    
    /** A regular expression for a suggestion that specifies a scale-factor (assuming any trailing plus is removed). */
    private static final RegEx REG_EX_SCALE_FACTOR = new RegExSimple("^([\\d.]+)$");

    /** Tries matching patterns that do not contain both and a width and a height */
    public static Optional<ImageResizeSuggestion> matchPreserveNotBoth(String suggestion) throws SuggestionFormatException {
        return OptionalUtilities.orElseGetFlat(
            matchRegEx(suggestion, REG_EX_WIDTH_ONLY, groups -> groups.extractOne(true)),
            () -> matchRegEx(suggestion, REG_EX_HEIGHT_ONLY, groups -> groups.extractOne(false))
        );
    }
    
    /** Tries matching patterns that contain <b>both</b> a width and a height */
    public static Optional<ImageResizeSuggestion> matchBothWidthAndHeight(String suggestion, boolean preserveAspectRatio) throws SuggestionFormatException {
        RegEx regEx = preserveAspectRatio ? REG_EX_BOTH_PRESERVE : REG_EX_BOTH_DO_NOT_PRESERVE;
        return matchRegEx(suggestion, regEx, groups -> groups.extractBoth(preserveAspectRatio) );
    }
    
    /** Tries matching a pattern for a constant scale-factor. */
    public static Optional<ImageResizeSuggestion> matchScaleFactor(String suggestion) throws SuggestionFormatException {
        return matchRegEx(suggestion, REG_EX_SCALE_FACTOR, SuggestionFromArray::extractScaleFactor);
    }

    /** Tries matching a particular regular-expression against the string, and if successful applies a function to extract a suggestion. */
    private static Optional<ImageResizeSuggestion> matchRegEx(String suggestion, RegEx regEx, CheckedFunction<SuggestionFromArray,ImageResizeSuggestion,SuggestionFormatException> extractSuggestion ) throws SuggestionFormatException {
        return OptionalUtilities.map(
               regEx.match(suggestion),
               groups -> extractSuggestion.apply( new SuggestionFromArray(groups) )
        );
    }
}
