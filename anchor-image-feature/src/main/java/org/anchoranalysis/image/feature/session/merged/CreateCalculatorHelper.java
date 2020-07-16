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
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMultiChangeInput;
import org.anchoranalysis.feature.session.calculator.cached.FeatureCalculatorCachedMulti;
import org.anchoranalysis.feature.session.strategy.child.CacheTransferSourceCollection;
import org.anchoranalysis.feature.session.strategy.child.CheckCacheForSpecificChildren;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.strategy.replace.ReuseSingletonStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.feature.session.InitParamsHelper;

@RequiredArgsConstructor
class CreateCalculatorHelper {

    // Prefixes that are ignored
    private final Optional<NRGStackWithParams> nrgStack;
    private final Logger logger;

    public <T extends FeatureInputNRG> FeatureCalculatorMulti<T> create(
            FeatureList<T> features,
            ImageInitParams soImage,
            BoundReplaceStrategy<T, ? extends ReplaceStrategy<T>> replacePolicyFactory)
            throws InitException {
        return wrapWithNrg(createWithoutNrg(features, soImage, replacePolicyFactory));
    }

    public <T extends FeatureInputNRG> FeatureCalculatorMulti<T> createCached(
            FeatureList<T> features,
            ImageInitParams soImage,
            BoundReplaceStrategy<T, ? extends ReplaceStrategy<T>> replacePolicyFactory)
            throws InitException {
        return wrapWithNrg(
                new FeatureCalculatorCachedMulti<>(
                        createWithoutNrg(features, soImage, replacePolicyFactory)));
    }

    /**
     * Create a pair-calculator. We want to substitute existing caches where they exist for specific
     * sub-caches of Pair features
     *
     * <p>This is to reduce calculation, as they've already been calculated for the "single"
     * features.
     *
     * @param <T>
     * @param features
     * @param soImage
     * @return
     * @throws InitException
     */
    public <T extends FeatureInputNRG> FeatureCalculatorMulti<T> createPair(
            FeatureList<T> features,
            ImageInitParams soImage,
            CacheTransferSourceCollection cacheTransferSource)
            throws InitException {

        BoundReplaceStrategy<T, ReplaceStrategy<T>> replaceStrategy =
                new BoundReplaceStrategy<>(
                        cacheCreator ->
                                new ReuseSingletonStrategy<>(
                                        cacheCreator,
                                        new CheckCacheForSpecificChildren(
                                                FeatureInputSingleObject.class,
                                                cacheTransferSource)));

        return wrapWithNrg(createWithoutNrg(features, soImage, replaceStrategy));
    }

    private <T extends FeatureInputNRG> FeatureCalculatorMulti<T> createWithoutNrg(
            FeatureList<T> features,
            ImageInitParams soImage,
            BoundReplaceStrategy<T, ? extends ReplaceStrategy<T>> replacePolicyFactory)
            throws InitException {

        try {
            return FeatureSession.with(
                    features,
                    createInitParams(soImage),
                    Optional.empty(),
                    logger,
                    replacePolicyFactory);

        } catch (FeatureCalcException e) {
            throw new InitException(e);
        }
    }

    /** Ensures any input-parameters have the NRG-stack attached */
    private <T extends FeatureInputNRG> FeatureCalculatorMulti<T> wrapWithNrg(
            FeatureCalculatorMulti<T> calculator) {
        return new FeatureCalculatorMultiChangeInput<>(
                calculator, input -> input.setNrgStack(nrgStack));
    }

    private FeatureInitParams createInitParams(ImageInitParams soImage) {
        return InitParamsHelper.createInitParams(Optional.of(soImage.getSharedObjects()), nrgStack);
    }
}
