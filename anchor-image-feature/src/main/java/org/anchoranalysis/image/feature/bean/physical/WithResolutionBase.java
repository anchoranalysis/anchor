/*-
 * #%L
 * anchor-image-feature
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

package org.anchoranalysis.image.feature.bean.physical;

import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.operator.FeatureUnaryGeneric;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureCalculationInput;
import org.anchoranalysis.feature.input.FeatureInputWithResolution;
import org.anchoranalysis.image.core.dimensions.Resolution;

/**
 * Base class for a feature that requires input resolution to be calculated.
 *
 * <p>This class provides a framework for features that depend on image resolution for their
 * calculations. It handles cases where resolution might be missing.
 *
 * @param <T> feature input type, which must include resolution information
 * @author Owen Feehan
 */
@NoArgsConstructor
public abstract class WithResolutionBase<T extends FeatureInputWithResolution>
        extends FeatureUnaryGeneric<T> {

    // START BEAN FIELDS
    /**
     * Whether to return {@code Double.NaN} (if true) or throw an exception (if false) when image
     * resolution is missing.
     */
    @BeanField @Getter @Setter private boolean acceptMissingResolution = false;
    // END BEAN FIELDS

    /**
     * Creates a new instance with a specified feature.
     *
     * @param feature the feature to be used in calculations
     */
    protected WithResolutionBase(Feature<T> feature) {
        super(feature);
    }

    /**
     * Calculates the feature value, handling cases where resolution might be missing.
     *
     * @param input the input for feature calculation
     * @return the calculated feature value
     * @throws FeatureCalculationException if an error occurs during calculation
     */
    @Override
    public final double calculate(FeatureCalculationInput<T> input)
            throws FeatureCalculationException {

        double value = input.calculate(getItem());

        if (acceptMissingResolution) {
            Optional<Resolution> resolution = input.get().getResolutionOptional();
            if (resolution.isPresent()) {
                return calculateWithResolution(value, resolution.get());
            } else {
                return Double.NaN;
            }
        } else {
            return calculateWithResolution(value, input.get().getResolutionRequired());
        }
    }

    /**
     * Calculates the feature value using the provided value and resolution.
     *
     * @param value the input value to be used in the calculation
     * @param resolution the resolution to be used in the calculation
     * @return the calculated feature value
     * @throws FeatureCalculationException if an error occurs during calculation
     */
    protected abstract double calculateWithResolution(double value, Resolution resolution)
            throws FeatureCalculationException;
}
