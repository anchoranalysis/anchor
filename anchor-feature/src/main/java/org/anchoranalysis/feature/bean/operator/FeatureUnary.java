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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * A feature that contains another feature as a bean-parameter.
 *
 * <p>Usually some conversion occurs between one feature-type and another.
 *
 * @author Owen Feehan
 * @param <T> input-type used for calculating feature
 * @param <S> input-type used for the "item" that is the single element
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class FeatureUnary<T extends FeatureInput, S extends FeatureInput>
        extends Feature<T> {

    // START BEAN PARAMETERS
    @BeanField @Getter @Setter private Feature<S> item = null;

    // END BEAN PARAMETERS

    @Override
    public Class<? extends FeatureInput> inputType() {
        return item.inputType();
    }
}
