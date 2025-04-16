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
 * Base class for evaluating {@link FeaturePairObjects} in terms of another feature that operates
 * on elements (first, second, merged etc.).
 *
 * <p>This class provides methods to calculate feature values for the first, second, and merged objects
 * in a pair, using a specified feature for single objects.</p>
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class FeatureDeriveFromPair extends FeaturePairObjects {

    // START BEAN PROPERTIES
    /** The feature to be applied to individual objects. */
    @BeanField @Getter @Setter private Feature<FeatureInputSingleObject> item;
    // END BEAN PROPERTIES

    /** Cache name for the first object in the pair. */
    public static final ChildCacheName CACHE_NAME_FIRST =
            new ChildCacheName(FeatureDeriveFromPair.class, "first");

    /** Cache name for the second object in the pair. */
    public static final ChildCacheName CACHE_NAME_SECOND =
            new ChildCacheName(FeatureDeriveFromPair.class, "second");

    /** Cache name for the merged object from the pair. */
    public static final ChildCacheName CACHE_NAME_MERGED =
            new ChildCacheName(FeatureDeriveFromPair.class, "merged");

    /**
     * Calculates the feature value for the first object in the pair.
     *
     * @param input the input for feature calculation
     * @return the calculated feature value for the first object
     * @throws FeatureCalculationException if an error occurs during calculation
     */
    protected double valueFromFirst(FeatureCalculationInput<FeatureInputPairObjects> input)
            throws FeatureCalculationException {
        return featureValueFrom(input, Extract.FIRST, CACHE_NAME_FIRST);
    }

    /**
     * Calculates the feature value for the second object in the pair.
     *
     * @param input the input for feature calculation
     * @return the calculated feature value for the second object
     * @throws FeatureCalculationException if an error occurs during calculation
     */
    protected double valueFromSecond(FeatureCalculationInput<FeatureInputPairObjects> input)
            throws FeatureCalculationException {
        return featureValueFrom(input, Extract.SECOND, CACHE_NAME_SECOND);
    }

    /**
     * Calculates the feature value for the merged object from the pair.
     *
     * @param input the input for feature calculation
     * @return the calculated feature value for the merged object
     * @throws FeatureCalculationException if an error occurs during calculation
     */
    protected double valueFromMerged(FeatureCalculationInput<FeatureInputPairObjects> input)
            throws FeatureCalculationException {
        return featureValueFrom(input, Extract.MERGED, CACHE_NAME_MERGED);
    }

    /**
     * Calculates the feature value for a specified part of the pair.
     *
     * @param input the input for feature calculation
     * @param extract specifies which part of the pair to extract (FIRST, SECOND, or MERGED)
     * @param cacheName the cache name to use for this calculation
     * @return the calculated feature value
     * @throws FeatureCalculationException if an error occurs during calculation
     */
    private double featureValueFrom(
            FeatureCalculationInput<FeatureInputPairObjects> input,
            Extract extract,
            ChildCacheName cacheName)
            throws FeatureCalculationException {

        return input.forChild().calculate(item, new CalculateInputFromPair(extract), cacheName);
    }
}