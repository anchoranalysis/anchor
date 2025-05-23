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
package org.anchoranalysis.image.feature.calculator;

import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.name.FeatureNameList;

/**
 * A {@link FeatureCalculatorMulti} with associated feature-names.
 *
 * <p>This class combines a multi-feature calculator with a list of feature names, allowing for
 * named feature calculations.
 *
 * @param <T> feature input type
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class NamedFeatureCalculatorMulti<T extends FeatureInput> {

    /** The multi-feature calculator. */
    private FeatureCalculatorMulti<T> calculator;

    /** The list of feature names associated with the calculator. */
    private FeatureNameList names;

    /**
     * Creates a new instance by applying a mapping function to the calculator.
     *
     * @param mapOperator a unary operator that transforms the calculator
     * @return a new NamedFeatureCalculatorMulti with the transformed calculator and the same names
     */
    public NamedFeatureCalculatorMulti<T> mapCalculator(
            UnaryOperator<FeatureCalculatorMulti<T>> mapOperator) {
        return new NamedFeatureCalculatorMulti<>(mapOperator.apply(calculator), names);
    }
}
