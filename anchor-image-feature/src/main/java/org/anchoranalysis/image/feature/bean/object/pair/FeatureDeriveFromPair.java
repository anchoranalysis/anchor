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

package org.anchoranalysis.image.feature.bean.object.pair;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureCalculationInput;
import org.anchoranalysis.feature.calculate.cache.ChildCacheName;
import org.anchoranalysis.image.feature.calculation.CalculateInputFromPair;
import org.anchoranalysis.image.feature.calculation.CalculateInputFromPair.Extract;
import org.anchoranalysis.image.feature.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.input.FeatureInputSingleObject;

/**
 * Base class for evaluating {@link FeaturePairObjects} in terms of another feature that operations
 * on elements (first, second, merged etc.)
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class FeatureDeriveFromPair extends FeaturePairObjects {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private Feature<FeatureInputSingleObject> item;
    // END BEAN PROPERTIES

    public static final ChildCacheName CACHE_NAME_FIRST =
            new ChildCacheName(FeatureDeriveFromPair.class, "first");
    public static final ChildCacheName CACHE_NAME_SECOND =
            new ChildCacheName(FeatureDeriveFromPair.class, "second");
    public static final ChildCacheName CACHE_NAME_MERGED =
            new ChildCacheName(FeatureDeriveFromPair.class, "merged");

    protected double valueFromFirst(FeatureCalculationInput<FeatureInputPairObjects> input)
            throws FeatureCalculationException {
        return featureValFrom(input, Extract.FIRST, CACHE_NAME_FIRST);
    }

    protected double valueFromSecond(FeatureCalculationInput<FeatureInputPairObjects> input)
            throws FeatureCalculationException {
        return featureValFrom(input, Extract.SECOND, CACHE_NAME_SECOND);
    }

    protected double valueFromMerged(FeatureCalculationInput<FeatureInputPairObjects> input)
            throws FeatureCalculationException {
        return featureValFrom(input, Extract.MERGED, CACHE_NAME_MERGED);
    }

    private double featureValFrom(
            FeatureCalculationInput<FeatureInputPairObjects> input,
            Extract extract,
            ChildCacheName cacheName)
            throws FeatureCalculationException {

        return input.forChild().calculate(item, new CalculateInputFromPair(extract), cacheName);
    }
}
