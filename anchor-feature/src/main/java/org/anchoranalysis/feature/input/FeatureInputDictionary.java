/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.input;

import java.util.Optional;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;

/**
 * A {@link FeatureInputWithResolution} that has an optional associated {@link Dictionary}.
 *
 * @author Owen Feehan
 */
public interface FeatureInputDictionary extends FeatureInputWithResolution {

    /**
     * The associated dictionary, if it exists.
     *
     * @return the dictionary, if it exists.
     */
    Optional<Dictionary> getDictionaryOptional();

    /**
     * The associated dictionary, or an exception if it doesn't exist.
     *
     * @return the dictionary.
     * @throws FeatureCalculationException if the dictionary doesn't exist.
     */
    default Dictionary getDictionaryRequired() throws FeatureCalculationException {
        return getDictionaryOptional()
                .orElseThrow(
                        () ->
                                new FeatureCalculationException(
                                        "A dictionary is required for this input."));
    }
}
