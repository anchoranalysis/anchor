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
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.feature.session.calculator.multi.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.replace.BoundReplaceStrategy;
import org.anchoranalysis.feature.session.replace.CacheAndReuseStrategy;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.feature.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.input.FeatureInputSingleObject;
import org.anchoranalysis.image.feature.input.FeatureInputStack;

/**
 * Calculates a result for merged-pairs based upon combining the calculations from several
 * calculators
 *
 * @author Owen Feehan
 */
class CombinedCalculator {

    private final MergedPairsFeatures features;
    private final CreateCalculatorHelper calculatorCreator;
    private final MergedPairsInclude include;

    private final FeatureCalculatorMulti<FeatureInputStack> calculatorImage;

    /**
     * For calculating first and second single objects
     *
     * <p>We avoid using separate sessions for First and Second, as we want them to share the same
     * Vertical-Cache for object calculation.
     *
     * <p>Can be empty, if neither the first or second features are included
     */
    private final Optional<FeatureCalculatorMulti<FeatureInputSingleObject>> calculatorFirstSecond;

    /**
     * For calculating merged objects
     *
     * <p>Can be empty, if neither the merged are not included
     */
    private final Optional<FeatureCalculatorMulti<FeatureInputSingleObject>> calculatorMerged;

    /** For calculating pair objects */
    private final FeatureCalculatorMulti<FeatureInputPairObjects> calculatorPair;

    public CombinedCalculator(
            MergedPairsFeatures features,
            CreateCalculatorHelper calculatorCreator,
            MergedPairsInclude include,
            ImageInitialization initialization)
            throws InitException {
        super();
        this.calculatorCreator = calculatorCreator;
        this.features = features;
        this.include = include;

        calculatorImage = features.createCalculator(calculatorCreator, initialization, CachingStrategies.cacheAndReuse());

        BoundReplaceStrategy<
                        FeatureInputSingleObject, CacheAndReuseStrategy<FeatureInputSingleObject>>
                cachingStrategyFirstSecond = CachingStrategies.cacheAndReuse();

        calculatorFirstSecond = createFirstAndSecond(initialization, cachingStrategyFirstSecond);

        BoundReplaceStrategy<
                        FeatureInputSingleObject, CacheAndReuseStrategy<FeatureInputSingleObject>>
                cachingStrategyMerged = CachingStrategies.cacheAndReuse();

        calculatorMerged = createMerged(initialization, cachingStrategyMerged);

        calculatorPair = createPair(initialization, cachingStrategyFirstSecond, cachingStrategyMerged);
    }

    public ResultsVector calculateForInput(
            FeatureInputPairObjects input, Optional<ErrorReporter> errorReporter)
            throws NamedFeatureCalculateException {

        ResultsVectorBuilder helper = new ResultsVectorBuilder(sizeFeatures(), errorReporter);

        // First we calculate the Image features (we rely on the energy stack being added by the
        // calculator)
        // These are identical and are cached in the background, to avoid being repeatedly
        // calculated.
        helper.calculateAndInsert(new FeatureInputStack(), calculatorImage);

        // First features
        if (include.includeFirst()) {
            helper.calculateAndInsert(
                    input, FeatureInputPairObjects::getFirst, calculatorFirstSecond.get() // NOSONAR
                    );
        }

        // Second features
        if (include.includeSecond()) {
            helper.calculateAndInsert(
                    input,
                    FeatureInputPairObjects::getSecond,
                    calculatorFirstSecond.get() // NOSONAR
                    );
        }

        // Merged.
        if (include.includeMerged()) {
            helper.calculateAndInsert(
                    input, FeatureInputPairObjects::getMerged, calculatorMerged.get() // NOSONAR
                    );
        }

        // Pair features
        helper.calculateAndInsert(input, calculatorPair);

        return helper.getResultsVector();
    }

    public int sizeFeatures() {

        // Number of times we use the listSingle
        int numSingle =
                (1
                        + integerFromBoolean(include.includeFirst())
                        + integerFromBoolean(include.includeSecond()));

        return (features.numberImageFeatures()
                + features.numberPairFeatures()
                + (numSingle * features.numberSingleFeatures()));
    }

    private Optional<FeatureCalculatorMulti<FeatureInputSingleObject>> createFirstAndSecond(
            ImageInitialization initialization,
            BoundReplaceStrategy<
                            FeatureInputSingleObject,
                            CacheAndReuseStrategy<FeatureInputSingleObject>>
                    cachingStrategyFirstSecond)
            throws InitException {
        if (include.includeFirstOrSecond()) {
            return Optional.of(features.createSingle(calculatorCreator, initialization, cachingStrategyFirstSecond));
        } else {
            return Optional.empty();
        }
    }

    private Optional<FeatureCalculatorMulti<FeatureInputSingleObject>> createMerged(
            ImageInitialization initialization,
            BoundReplaceStrategy<
                            FeatureInputSingleObject,
                            CacheAndReuseStrategy<FeatureInputSingleObject>>
                    cachingStrategyMerged)
            throws InitException {
        if (include.includeMerged()) {
            return Optional.of(features.createSingle(calculatorCreator, initialization, cachingStrategyMerged));
        } else {
            return Optional.empty();
        }
    }

    private FeatureCalculatorMulti<FeatureInputPairObjects> createPair(
            ImageInitialization initialization,
            BoundReplaceStrategy<
                            FeatureInputSingleObject,
                            CacheAndReuseStrategy<FeatureInputSingleObject>>
                    cachingStrategyFirstSecond,
            BoundReplaceStrategy<
                            FeatureInputSingleObject,
                            CacheAndReuseStrategy<FeatureInputSingleObject>>
                    cachingStrategyMerged)
            throws InitException {
        return features.createPair(
                calculatorCreator,
                initialization,
                TransferSourceHelper.createTransferSource(
                        cachingStrategyFirstSecond, cachingStrategyMerged));
    }

    /**
     * Integer value from boolean
     *
     * @param b
     * @return 0 for false, 1 for true
     */
    private static int integerFromBoolean(boolean b) {
        return b ? 1 : 0;
    }
}
