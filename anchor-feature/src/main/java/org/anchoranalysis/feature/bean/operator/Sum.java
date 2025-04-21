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
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureCalculationInput;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Sums the results after calculating a list of {@link Feature}s.
 *
 * @author Owen Feehan
 * @param <T> feature input-type of all features in the list, as well as the returned result.
 */
@NoArgsConstructor
public class Sum<T extends FeatureInput> extends FeatureFromList<T> {

    // START BEAN PROPERTIES
    /** If true, we ignore any NaN values. Otherwise the sum becomes NaN */
    @BeanField @Getter @Setter private boolean ignoreNaN;

    // END BEAN PROPERTIES

    /**
     * Create with a list of {@link Feature}s which become summed.
     *
     * @param features the features.
     */
    public Sum(FeatureList<T> features) {
        super(features);
    }

    @Override
    public double calculate(FeatureCalculationInput<T> input) throws FeatureCalculationException {

        double result = 0;

        for (Feature<T> elem : getList()) {
            double resultInd = input.calculate(elem);

            if (ignoreNaN && Double.isNaN(resultInd)) {
                continue;
            }

            result += resultInd;
        }

        return result;
    }

    @Override
    public String descriptionLong() {
        return descriptionForList("+");
    }
}
