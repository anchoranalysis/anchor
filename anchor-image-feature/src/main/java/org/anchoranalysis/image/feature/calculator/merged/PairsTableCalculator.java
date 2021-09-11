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
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.feature.calculator.FeatureTableCalculator;
import org.anchoranalysis.image.feature.input.FeatureInputPairObjects;

/**
 * A feature-session to evaluate pairs of objects.
 *
 * <p>Successive pairs are evaluated in five different ways:
 *
 * <ul>
 *   <li>the image in which the object exists (on {code listImage}) i.e. the energy-stack.
 *   <li>the left-object in the pair (on {@code listSingle})
 *   <li>the right-object in the pair (on {@code listSingle})
 *   <li>the pair (on {code listPair})
 *   <li>both objects merged together (on {code listSingle}}
 * </ul>
 *
 * <p>Due to the predictable pattern, feature-calculations can be cached predictably and
 * appropriately to avoid redundancies.
 *
 * <p>Two types of caching are applied to avoid redundancy:
 *
 * <ul>
 *   <li>The internal calculation caches of first/second/merged are reused as the internal
 *       calculation sub-caches in pair.
 *   <li>The entire results are cached (as a function of the input) for first/second, as the same
 *       inputs reappear multiple times.
 * </ul>
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class PairsTableCalculator implements FeatureTableCalculator<FeatureInputPairObjects> {

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
    public void start(
            ImageInitialization initializtion, Optional<EnergyStack> energyStack, Logger logger)
            throws InitializeException {

        calculator =
                new CombinedCalculator(
                        features,
                        new CreateCalculatorHelper(energyStack, logger),
                        include,
                        initializtion);
    }

    @Override
    public ResultsVector calculate(FeatureInputPairObjects input)
            throws NamedFeatureCalculateException {
        return calculator.calculateForInput(input, Optional.empty());
    }

    @Override
    public ResultsVector calculate(
            FeatureInputPairObjects input, FeatureList<FeatureInputPairObjects> featuresSubset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultsVector calculateSuppressErrors(
            FeatureInputPairObjects input, ErrorReporter errorReporter) {

        try {
            return calculator.calculateForInput(input, Optional.of(errorReporter));
        } catch (NamedFeatureCalculateException e) {
            errorReporter.recordError(PairsTableCalculator.class, e);

            ResultsVector results = new ResultsVector(sizeFeatures());
            results.setErrorAll(e);
            return results;
        }
    }

    public ResultsVector calculateMaybeSuppressErrors(
            FeatureInputPairObjects input, ErrorReporter errorReporter)
            throws NamedFeatureCalculateException {
        if (suppressErrors) {
            return calculateSuppressErrors(input, errorReporter);
        } else {
            return calculate(input);
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
