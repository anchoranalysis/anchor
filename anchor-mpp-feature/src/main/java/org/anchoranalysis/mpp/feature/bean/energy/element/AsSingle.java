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

package org.anchoranalysis.mpp.feature.bean.energy.element;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.operator.FeatureUnary;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureCalculationInput;
import org.anchoranalysis.feature.calculate.cache.ChildCacheName;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.mpp.feature.input.FeatureInputPairMemo;
import org.anchoranalysis.mpp.feature.input.FeatureInputSingleMemo;

/**
 * Extracts one of the memos from the pair, and processes as a {@link FeatureSingleMemo}
 *
 * @author Owen Feehan
 */
public class AsSingle extends FeatureUnary<FeatureInputPairMemo, FeatureInputSingleMemo> {

    // START BEAN PROPERTIES
    /** Iff true, first object is used, otherwise the second */
    @BeanField @Getter @Setter private boolean first = true;
    // END BEAN PROPERTIES

    private static final ChildCacheName CACHE_NAME_FIRST =
            new ChildCacheName(AsSingle.class, "first");
    private static final ChildCacheName CACHE_NAME_SECOND =
            new ChildCacheName(AsSingle.class, "second");

    @Override
    public double calculate(FeatureCalculationInput<FeatureInputPairMemo> input)
            throws FeatureCalculationException {
        return input.forChild()
                .calculate(
                        getItem(),
                        new CalculateDeriveSingleMemoFromPair(first),
                        first ? CACHE_NAME_FIRST : CACHE_NAME_SECOND);
    }

    // We change the default behaviour, as we don't want to give the same input-type as the item we
    // pass to.
    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputPairMemo.class;
    }

    @Override
    public String describeParameters() {
        return getItem().describeParameters();
    }
}
