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
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureCalculationInput;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Uses another feature to calculate the value.
 *
 * <p>The other feature is identified by it's name, or a string that otherwise resolves to its name.
 *
 * @author Owen Feehan
 * @param <T> the feature input-type
 */
@NoArgsConstructor
public class Reference<T extends FeatureInput> extends FeatureGeneric<T> {

    // START BEAN
    /** The identifier that uniquely determines the other feature to reference. */
    @BeanField @Getter @Setter private String id;

    // END BEAN

    /**
     * Create with a specific identifier.
     *
     * @param id the identifier.
     */
    public Reference(String id) {
        this.id = id;
    }

    @Override
    public double calculate(FeatureCalculationInput<T> input) throws FeatureCalculationException {
        // We resolve the ID before its calculated
        String resolvedID = input.bySymbol().resolveFeatureIdentifier(id);
        return input.bySymbol().calculateFeatureByIdentifier(resolvedID, input);
    }

    @Override
    public String descriptionLong() {
        return "_" + id;
    }
}
