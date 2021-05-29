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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Optional;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.operator.FeatureUnaryGeneric;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.cache.SessionInput;
import org.anchoranalysis.feature.input.FeatureInputWithResolution;
import org.anchoranalysis.image.core.dimensions.Resolution;

/**
 * Base-class for a feature that requires input-resolution to be calculated.
 * 
 * @author Owen Feehan
 *
 * @param <T> feature-input type.
 */
@NoArgsConstructor
public abstract class WithResolutionBase<T extends FeatureInputWithResolution>
        extends FeatureUnaryGeneric<T> {

    // START BEAN FIELDS
    /**
     * Whether to throw an exception (if true) if image-resolution is missing, or return {@code Double.Nan} (if false). 
     */
    private @BeanField @Getter @Setter boolean acceptMissingResolution = false;
    // END BEAN FIELDS
    
    protected WithResolutionBase(Feature<T> feature) {
        super(feature);
    }

    @Override
    public final double calculate(SessionInput<T> input) throws FeatureCalculationException {

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

    protected abstract double calculateWithResolution(double value, Resolution resolution)
            throws FeatureCalculationException;
}
