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

package org.anchoranalysis.image.feature.calculator.merged;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMultiChangeInput;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.initialization.FeatureInitialization;
import org.anchoranalysis.feature.input.FeatureInputEnergy;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.cache.finder.CacheTransferSourceCollection;
import org.anchoranalysis.feature.session.cache.finder.CheckCacheForSpecificChildren;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorCache;
import org.anchoranalysis.feature.session.replace.BoundReplaceStrategy;
import org.anchoranalysis.feature.session.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.replace.ReuseSingletonStrategy;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.feature.calculator.InitializationFactory;
import org.anchoranalysis.image.feature.input.FeatureInputSingleObject;

/** Helper class for creating feature calculators with various configurations. */
@RequiredArgsConstructor
class CreateCalculatorHelper {

    // Prefixes that are ignored
    private final Optional<EnergyStack> energyStack;
    private final Logger logger;

    /**
     * Creates a cached feature calculator.
     *
     * @param <T> The type of feature input
     * @param features The list of features to calculate
     * @param initialization The image initialization parameters
     * @param replacePolicyFactory The strategy for replacing and caching calculations
     * @return A multi-feature calculator with caching
     * @throws InitializeException If initialization fails
     */
    public <T extends FeatureInputEnergy> FeatureCalculatorMulti<T> createCached(
            FeatureList<T> features,
            ImageInitialization initialization,
            BoundReplaceStrategy<T, ? extends ReplaceStrategy<T>> replacePolicyFactory)
            throws InitializeException {
        return wrapWithEnergy(
                FeatureCalculatorCache.cache(
                        createWithoutEnergy(features, initialization, replacePolicyFactory)));
    }

    /**
     * Creates a pair-calculator.
     *
     * <p>This method substitutes existing caches where they exist for specific sub-caches of Pair
     * features to reduce calculation, as they've already been calculated for the "single" features.
     *
     * @param <T> The type of feature input
     * @param features The list of features to calculate
     * @param initialization The image initialization parameters
     * @param cacheTransferSource Collection of cache transfer sources
     * @return A multi-feature calculator for pair features
     * @throws InitializeException If initialization fails
     */
    public <T extends FeatureInputEnergy> FeatureCalculatorMulti<T> createPair(
            FeatureList<T> features,
            ImageInitialization initialization,
            CacheTransferSourceCollection cacheTransferSource)
            throws InitializeException {

        BoundReplaceStrategy<T, ReplaceStrategy<T>> replaceStrategy =
                new BoundReplaceStrategy<>(
                        cacheCreator ->
                                new ReuseSingletonStrategy<>(
                                        cacheCreator,
                                        new CheckCacheForSpecificChildren(
                                                FeatureInputSingleObject.class,
                                                cacheTransferSource)));

        return wrapWithEnergy(createWithoutEnergy(features, initialization, replaceStrategy));
    }

    /**
     * Creates a feature calculator without energy.
     *
     * @param <T> The type of feature input
     * @param features The list of features to calculate
     * @param initialization The image initialization parameters
     * @param replacePolicyFactory The strategy for replacing and caching calculations
     * @return A multi-feature calculator without energy
     * @throws InitializeException If initialization fails
     */
    private <T extends FeatureInputEnergy> FeatureCalculatorMulti<T> createWithoutEnergy(
            FeatureList<T> features,
            ImageInitialization initialization,
            BoundReplaceStrategy<T, ? extends ReplaceStrategy<T>> replacePolicyFactory)
            throws InitializeException {
        return FeatureSession.with(
                features,
                createInitialization(initialization),
                Optional.empty(),
                logger,
                replacePolicyFactory);
    }

    /**
     * Ensures any input-parameters have the energy-stack attached.
     *
     * @param <T> The type of feature input
     * @param calculator The calculator to wrap
     * @return A wrapped calculator that attaches the energy stack to inputs
     */
    private <T extends FeatureInputEnergy> FeatureCalculatorMulti<T> wrapWithEnergy(
            FeatureCalculatorMulti<T> calculator) {
        return new FeatureCalculatorMultiChangeInput<>(
                calculator, input -> input.setEnergyStack(energyStack));
    }

    /**
     * Creates a FeatureInitialization from ImageInitialization.
     *
     * @param initialization The image initialization parameters
     * @return A FeatureInitialization object
     */
    private FeatureInitialization createInitialization(ImageInitialization initialization) {
        return InitializationFactory.create(
                Optional.of(initialization.sharedObjects()), energyStack);
    }
}
