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

package org.anchoranalysis.feature.bean.operator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.calculate.FeatureCalculationInput;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * A constant value that is entirely invariant to the feature-input.
 *
 * @author Owen Feehan
 * @param <T> the input-type (even though it is irrelevant and ignored, but it keeps the class
 *     hierarchy consistent).
 */
@NoArgsConstructor
public class Constant<T extends FeatureInput> extends FeatureGeneric<T> {

    // START BEAN PARAMETERS
    /** The constant value. */
    @BeanField @Getter @Setter private double value;
    // END BEAN PARAMETERS

    /**
     * Constructor with a particular value.
     *
     * @param value the value.
     */
    public Constant(double value) {
        this.value = value;
    }

    /**
     * Constructor with a particular value and a custom-name.
     *
     * @param customName a custom-name for feature.
     * @param value the value.
     */
    public Constant(String customName, double value) {
        this.value = value;
        setCustomName(customName);
    }

    @Override
    public double calculate(FeatureCalculationInput<T> input) {
        return value;
    }

    @Override
    public String describeParameters() {
        return String.format("%2.2f", value);
    }

    @Override
    public String descriptionLong() {
        return describeParameters();
    }
}
