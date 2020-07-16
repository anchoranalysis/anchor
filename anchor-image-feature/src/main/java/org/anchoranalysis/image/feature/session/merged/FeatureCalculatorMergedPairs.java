/* (C)2020 */
package org.anchoranalysis.image.feature.session.merged;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.session.FeatureTableCalculator;

/**
 * A particular type of feature-session where successive pairs of objects are evaluated by features
 * in five different ways:
 *
 * <p><div>
 *
 * <ul>
 *   <li>the image in which the object exists (on {code listImage}) i.e. the nrg-stack.
 *   <li>the left-object in the pair (on {@code listSingle})
 *   <li>the right-object in the pair (on {@code listSingle})
 *   <li>the pair (on {code listPair})
 *   <li>both objects merged together (on {code listSingle}}
 * </ul>
 *
 * </div>
 *
 * <p>Due to the predictable pattern, feature-calculations can be cached predictably and
 * appropriately to avoid redundancies.
 *
 * <p><div> Two types of caching are applied to avoid redundancy
 * <li>The internal calculation caches of first/second/merged are reused as the internal calculation
 *     sub-caches in pair.
 * <li>The entire results are cached (as a function of the input) for first/second, as the same
 *     inputs reappear multiple times. </div>
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class FeatureCalculatorMergedPairs
        implements FeatureTableCalculator<FeatureInputPairObjects> {

    // START REQUIRED ARGUMENTS
    private final MergedPairsFeatures features;
    private final MergedPairsInclude include;
    private final boolean suppressErrors;
    // END REQUIRED ARGUMENTS

    private CombinedCalculator calculator;

    public FeatureCalculatorMergedPairs(MergedPairsFeatures features) {
        this(features, new MergedPairsInclude(), true);
    }

    @Override
    public void start(ImageInitParams soImage, Optional<NRGStackWithParams> nrgStack, Logger logger)
            throws InitException {

        calculator =
                new CombinedCalculator(
                        features, new CreateCalculatorHelper(nrgStack, logger), include, soImage);
    }

    @Override
    public ResultsVector calc(FeatureInputPairObjects input) throws FeatureCalcException {
        return calculator.calcForInput(input, Optional.empty());
    }

    @Override
    public ResultsVector calc(
            FeatureInputPairObjects input, FeatureList<FeatureInputPairObjects> featuresSubset)
            throws FeatureCalcException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultsVector calcSuppressErrors(
            FeatureInputPairObjects input, ErrorReporter errorReporter) {

        try {
            return calculator.calcForInput(input, Optional.of(errorReporter));
        } catch (FeatureCalcException e) {
            errorReporter.recordError(FeatureCalculatorMergedPairs.class, e);

            ResultsVector rv = new ResultsVector(sizeFeatures());
            rv.setErrorAll(e);
            return rv;
        }
    }

    public ResultsVector calcMaybeSuppressErrors(
            FeatureInputPairObjects input, ErrorReporter errorReporter)
            throws FeatureCalcException {
        if (suppressErrors) {
            return calcSuppressErrors(input, errorReporter);
        } else {
            return calc(input);
        }
    }

    @Override
    public FeatureNameList createFeatureNames() {
        FeatureNameList out = new FeatureNameList();

        out.addCustomNamesWithPrefix("image.", features.getImage());

        if (include.includeFirst()) {
            out.addCustomNamesWithPrefix("first.", features.getSingle());
        }

        if (include.includeSecond()) {
            out.addCustomNamesWithPrefix("second.", features.getSingle());
        }

        if (include.includeMerged()) {
            out.addCustomNamesWithPrefix("merged.", features.getSingle());
        }

        out.addCustomNamesWithPrefix("pair.", features.getPair());

        return out;
    }

    @Override
    public int sizeFeatures() {
        return calculator.sizeFeatures();
    }

    @Override
    public FeatureTableCalculator<FeatureInputPairObjects> duplicateForNewThread() {
        return new FeatureCalculatorMergedPairs(features.duplicate(), include, suppressErrors);
    }
}
