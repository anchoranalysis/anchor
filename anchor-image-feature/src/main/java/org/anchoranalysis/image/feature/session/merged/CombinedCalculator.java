/* (C)2020 */
package org.anchoranalysis.image.feature.session.merged;

import java.util.Optional;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.strategy.replace.CacheAndReuseStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;

/**
 * Calculates a result for merged-pairs based upon combining the calculations from several
 * calculators
 *
 * @author Owen Feehan
 */
class CombinedCalculator {

    private final MergedPairsFeatures features;
    private final CreateCalculatorHelper cc;
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
            CreateCalculatorHelper cc,
            MergedPairsInclude include,
            ImageInitParams soImage)
            throws InitException {
        super();
        this.cc = cc;
        this.features = features;
        this.include = include;

        calculatorImage =
                features.createImageSession(cc, soImage, CachingStrategies.cacheAndReuse());

        BoundReplaceStrategy<
                        FeatureInputSingleObject, CacheAndReuseStrategy<FeatureInputSingleObject>>
                cachingStrategyFirstSecond = CachingStrategies.cacheAndReuse();

        calculatorFirstSecond = createFirstAndSecond(soImage, cachingStrategyFirstSecond);

        BoundReplaceStrategy<
                        FeatureInputSingleObject, CacheAndReuseStrategy<FeatureInputSingleObject>>
                cachingStrategyMerged = CachingStrategies.cacheAndReuse();

        calculatorMerged = createMerged(soImage, cachingStrategyMerged);

        calculatorPair = createPair(soImage, cachingStrategyFirstSecond, cachingStrategyMerged);
    }

    public ResultsVector calcForInput(
            FeatureInputPairObjects input, Optional<ErrorReporter> errorReporter)
            throws FeatureCalcException {

        ResultsVectorBuilder helper = new ResultsVectorBuilder(sizeFeatures(), errorReporter);

        // First we calculate the Image features (we rely on the NRG stack being added by the
        // calculator)
        // These are identical and are cached in the background, to avoid being repeatedly
        // calculated.
        helper.calcAndInsert(new FeatureInputStack(), calculatorImage);

        // First features
        if (include.includeFirst()) {
            helper.calcAndInsert(
                    input, FeatureInputPairObjects::getFirst, calculatorFirstSecond.get() // NOSONAR
                    );
        }

        // Second features
        if (include.includeSecond()) {
            helper.calcAndInsert(
                    input,
                    FeatureInputPairObjects::getSecond,
                    calculatorFirstSecond.get() // NOSONAR
                    );
        }

        // Merged.
        if (include.includeMerged()) {
            helper.calcAndInsert(
                    input, FeatureInputPairObjects::getMerged, calculatorMerged.get() // NOSONAR
                    );
        }

        // Pair features
        helper.calcAndInsert(input, calculatorPair);

        return helper.getResultsVector();
    }

    public int sizeFeatures() {

        // Number of times we use the listSingle
        int numSingle =
                (1
                        + integerFromBoolean(include.includeFirst())
                        + integerFromBoolean(include.includeSecond()));

        return (features.numImageFeatures()
                + features.numPairFeatures()
                + (numSingle * features.numSingleFeatures()));
    }

    private Optional<FeatureCalculatorMulti<FeatureInputSingleObject>> createFirstAndSecond(
            ImageInitParams soImage,
            BoundReplaceStrategy<
                            FeatureInputSingleObject,
                            CacheAndReuseStrategy<FeatureInputSingleObject>>
                    cachingStrategyFirstSecond)
            throws InitException {
        if (include.includeFirstOrSecond()) {
            return Optional.of(features.createSingle(cc, soImage, cachingStrategyFirstSecond));
        } else {
            return Optional.empty();
        }
    }

    private Optional<FeatureCalculatorMulti<FeatureInputSingleObject>> createMerged(
            ImageInitParams soImage,
            BoundReplaceStrategy<
                            FeatureInputSingleObject,
                            CacheAndReuseStrategy<FeatureInputSingleObject>>
                    cachingStrategyMerged)
            throws InitException {
        if (include.includeMerged()) {
            return Optional.of(features.createSingle(cc, soImage, cachingStrategyMerged));
        } else {
            return Optional.empty();
        }
    }

    private FeatureCalculatorMulti<FeatureInputPairObjects> createPair(
            ImageInitParams soImage,
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
                cc,
                soImage,
                TransferSourceHelper.createTransferSource(
                        cachingStrategyFirstSecond, cachingStrategyMerged));
    }

    /**
     * Integer value from boolean
     *
     * @param b
     * @return 0 for FALSE, 1 for TRUE
     */
    private static int integerFromBoolean(boolean b) {
        return b ? 1 : 0;
    }
}
