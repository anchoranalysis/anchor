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

package org.anchoranalysis.image.feature.session.merged;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.NamedFeatureCalculationException;
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
public class PairsTableCalculator
        implements FeatureTableCalculator<FeatureInputPairObjects> {

    // START REQUIRED ARGUMENTS
    private final MergedPairsFeatures features;
    private final MergedPairsInclude include;
    private final boolean suppressErrors;
    // END REQUIRED ARGUMENTS

    private CombinedCalculator calculator;

    public PairsTableCalculator(MergedPairsFeatures features) {
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
    public ResultsVector calc(FeatureInputPairObjects input) throws NamedFeatureCalculationException {
        return calculator.calcForInput(input, Optional.empty());
    }

    @Override
    public ResultsVector calc(
            FeatureInputPairObjects input, FeatureList<FeatureInputPairObjects> featuresSubset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultsVector calcSuppressErrors(
            FeatureInputPairObjects input, ErrorReporter errorReporter) {

        try {
            return calculator.calcForInput(input, Optional.of(errorReporter));
        } catch (NamedFeatureCalculationException e) {
            errorReporter.recordError(PairsTableCalculator.class, e);

            ResultsVector rv = new ResultsVector(sizeFeatures());
            rv.setErrorAll(e);
            return rv;
        }
    }

    public ResultsVector calcMaybeSuppressErrors(
            FeatureInputPairObjects input, ErrorReporter errorReporter)
            throws NamedFeatureCalculationException {
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
        return new PairsTableCalculator(features.duplicate(), include, suppressErrors);
    }
}