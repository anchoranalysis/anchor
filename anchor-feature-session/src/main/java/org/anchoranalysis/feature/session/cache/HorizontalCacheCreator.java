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
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.cache.CacheCreator;
import org.anchoranalysis.feature.calculate.cache.FeatureCalculationCache;
import org.anchoranalysis.feature.initialization.FeatureInitialization;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputType;
import org.anchoranalysis.feature.shared.SharedFeatures;
import org.anchoranalysis.feature.shared.SharedFeaturesSubset;

/**
 * Creates a {@link FeatureCalculationCache} which caches the results of calls to features, even if
 * the same feature appears multiple times, directly or indirectly.
 *
 * <p>One feature may indirectly call another feature when calculating, or explicitly by reference.
 *
 * <p>It is termed <b>horizontal</b> as it does not cache computations between successive inputs,
 * but rather for the same input across multiple features.
 *
 * @author Owen
 */
@AllArgsConstructor
public class HorizontalCacheCreator implements CacheCreator {

    /**
     * A super set of named-features, from which those compatible with the input-type are used in
     * each created cache.
     */
    private FeatureList<? extends FeatureInput> namedFeatures;

    /** The shared-features to use for initialization. */
    private SharedFeatures sharedFeatures;

    /** What to initialize features with. */
    private FeatureInitialization initialization;

    /** The logger for writing informative messages or errors. */
    private Logger logger;

    @Override
    public <T extends FeatureInput> FeatureCalculationCache<T> create(
            Class<? extends FeatureInput> inputType) {
        FeatureList<T> featureList = filterFeatureList(inputType);
        return createCache(featureList, inputType, initialization, logger);
    }

    /**
     * Filters a feature-list to only include features compatible with a particular {@code
     * inputType}.
     *
     * <p>A feature in the list is deemed compatible if its class is either equal to or a
     * super-class of {@code inputType}.
     */
    @SuppressWarnings("unchecked")
    private <T extends FeatureInput> FeatureList<T> filterFeatureList(
            Class<? extends FeatureInput> inputType) {
        return namedFeatures.filterAndMap(
                feature -> FeatureInputType.isCompatibleWith(feature.inputType(), inputType),
                feature -> (Feature<T>) feature); // NOSONAR
    }

    /**
     * Initializes shared-features, and a newly-created {@link FeatureCalculationCache} for all
     * named-features.
     */
    private <T extends FeatureInput> FeatureCalculationCache<T> createCache(
            FeatureList<T> namedFeatures,
            Class<? extends FeatureInput> inputType,
            FeatureInitialization initialization,
            Logger logger) {
        SharedFeaturesSubset<T> sharedFeaturesSet = sharedFeatures.subsetCompatibleWith(inputType);

        try {
            sharedFeaturesSet.initializeRecursive(initialization, logger);
        } catch (InitializeException e) {
            logger.errorReporter()
                    .recordError(
                            HorizontalCacheCreator.class,
                            "An error occurred initializing shared-features, proceeding anyway.");
            logger.errorReporter().recordError(HorizontalCacheCreator.class, e);
        }

        FeatureCalculationCache<T> cache = createCache(namedFeatures, sharedFeaturesSet);
        cache.initialize(initialization, logger);
        return cache;
    }

    /**
     * Creates a {@link FeatureCalculationCache} for all features in {@code namedFeatures}.
     *
     * <p>This cache remembers feature-calculations, to avoid redundant new calculation.
     */
    private static <S extends FeatureInput> FeatureCalculationCache<S> createCache(
            FeatureList<S> namedFeatures, SharedFeaturesSubset<S> sharedFeatures) {

        FeatureCalculationCache<S> cacheCalculation = new CalculationCache<>(sharedFeatures);

        return new FeatureCache<>(
                cacheCalculation, namedFeatures, sharedFeatures, new ArrayList<>());
    }
}
