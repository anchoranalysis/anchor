/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.mpp.feature.bean.mark;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.input.FeatureInputDictionary;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.mpp.mark.Mark;

/**
 * A feature input that wraps a {@link Mark} along with optional dimensions and dictionary.
 *
 * <p>This class implements {@link FeatureInputDictionary} to provide dictionary-related functionality
 * for feature calculations on marks.</p>
 */
@AllArgsConstructor
@EqualsAndHashCode
public class FeatureInputMark implements FeatureInputDictionary {

    /** The mark associated with this feature input. */
    @Getter private final Mark mark;

    /** Optional dimensions associated with the mark. */
    private final Optional<Dimensions> dimensions;

    /** Optional dictionary associated with the mark. */
    private final Optional<Dictionary> dictionary;

    /**
     * Constructs a new instance with a mark and optional dimensions.
     *
     * @param mark the mark
     * @param dimensions optional dimensions
     */
    public FeatureInputMark(Mark mark, Optional<Dimensions> dimensions) {
        this(mark, dimensions, Optional.empty());
    }

    /**
     * Constructs a new instance with a mark, dimensions, and dictionary.
     *
     * @param mark the mark
     * @param dimensions the dimensions
     * @param dictionary the dictionary
     */
    public FeatureInputMark(Mark mark, Dimensions dimensions, Dictionary dictionary) {
        this(mark, Optional.of(dimensions), Optional.of(dictionary));
    }

    @Override
    public Optional<Resolution> getResolutionOptional() {
        return dimensions.flatMap(Dimensions::resolution);
    }

    @Override
    public Optional<Dictionary> getDictionaryOptional() {
        return dictionary;
    }

    /**
     * Gets the optional dimensions associated with this feature input.
     *
     * @return an {@link Optional} containing the dimensions, if present
     */
    public Optional<Dimensions> getDimensionsOptional() {
        return dimensions;
    }

    /**
     * Gets the dimensions associated with this feature input, throwing an exception if not present.
     *
     * @return the dimensions
     * @throws FeatureCalculationException if dimensions are not present
     */
    public Dimensions getDimensionsRequired() throws FeatureCalculationException {
        return dimensions.orElseThrow(
                () ->
                        new FeatureCalculationException(
                                "Dimensions are required in the input for this operation"));
    }
}