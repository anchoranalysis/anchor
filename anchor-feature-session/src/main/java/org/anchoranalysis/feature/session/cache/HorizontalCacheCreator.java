/*-
 * #%L
 * anchor-feature-session
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

package org.anchoranalysis.feature.session.cache;

import java.util.ArrayList;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureInitParams;
import org.anchoranalysis.feature.calculate.cache.CacheCreator;
import org.anchoranalysis.feature.calculate.cache.FeatureSessionCache;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputType;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HorizontalCacheCreator implements CacheCreator {
    
    private FeatureList<? extends FeatureInput> namedFeatures;
    private SharedFeatureMulti sharedFeatures;
    private FeatureInitParams featureInitParams;
    private Logger logger;

    @Override
    public <T extends FeatureInput> FeatureSessionCache<T> create(
            Class<? extends FeatureInput> inputType) {
        FeatureList<T> featureList = filterFeatureList(inputType);
        return createCache(featureList, inputType, featureInitParams, logger);
    }

    /**
     * Filters a feature-list to only include features compatible with a particular {@code
     * inputType}
     *
     * <p>A feature in the list is deemed compatible if its class is either equal to or a
     * super-class of {@code inputType}.
     */
    @SuppressWarnings("unchecked")
    private <T extends FeatureInput> FeatureList<T> filterFeatureList(
            Class<? extends FeatureInput> inputType) {
        return namedFeatures.filterAndMap(
                f -> FeatureInputType.isCompatibleWith(f.inputType(), inputType),
                f -> (Feature<T>) f);
    }

    private <T extends FeatureInput> FeatureSessionCache<T> createCache(
            FeatureList<T> namedFeatures,
            Class<? extends FeatureInput> inputType,
            FeatureInitParams featureInitParams,
            Logger logger) {
        SharedFeatureSet<T> sharedFeaturesSet = sharedFeatures.subsetCompatibleWith(inputType);

        try {
            sharedFeaturesSet.initRecursive(featureInitParams, logger);
        } catch (InitException e) {
            logger.errorReporter()
                    .recordError(
                            HorizontalCacheCreator.class,
                            "An error occurred initializing shared-features, proceeding anyway.");
            logger.errorReporter().recordError(HorizontalCacheCreator.class, e);
        }

        FeatureSessionCache<T> cache = createCache(namedFeatures, sharedFeaturesSet);
        cache.init(featureInitParams, logger);
        return cache;
    }
    
    private static <S extends FeatureInput> FeatureSessionCache<S> createCache(
            FeatureList<S> namedFeatures, SharedFeatureSet<S> sharedFeatures) {

        FeatureSessionCache<S> cacheCalculation = new CalculationCache<>(sharedFeatures);

        return new FeatureCache<>(
                cacheCalculation, namedFeatures, sharedFeatures, new ArrayList<>());
    }
}
