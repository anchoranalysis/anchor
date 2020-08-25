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

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.strategy.child.CacheTransferSourceCollection;
import org.anchoranalysis.feature.session.strategy.replace.CacheAndReuseStrategy;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;
import lombok.Getter;

/**
 * The list of features that can be used in a {@link PairsTableCalculator}
 *
 * @author Owen Feehan
 */
public class MergedPairsFeatures {

    @Getter private FeatureList<FeatureInputStack> image;

    @Getter private FeatureList<FeatureInputSingleObject> single;
    
    @Getter private FeatureList<FeatureInputPairObjects> pair;

    /**
     * Creates to only calculate pair features.
     *
     * @param pair features for a pair of objects
     */
    public MergedPairsFeatures(FeatureList<FeatureInputPairObjects> pair) {
        this(FeatureListFactory.empty(), FeatureListFactory.empty(), pair);
    }

    /**
     * Creates with features for single, pair and image as a whole.
     *
     * @param image features for image as a whole
     * @param single features for single-objects
     * @param pair features for a pair of objects
     */
    public MergedPairsFeatures(
            FeatureList<FeatureInputStack> image,
            FeatureList<FeatureInputSingleObject> single,
            FeatureList<FeatureInputPairObjects> pair) {
        super();
        this.image = image;
        this.single = single;
        this.pair = pair;
    }

    /** Immutably creates entirely new duplicated features */
    public MergedPairsFeatures duplicate() {
        return new MergedPairsFeatures(
                image.duplicateBean(), single.duplicateBean(), pair.duplicateBean());
    }

    public int numberImageFeatures() {
        return image.size();
    }

    public int numberSingleFeatures() {
        return single.size();
    }

    public int numberPairFeatures() {
        return pair.size();
    }

    public FeatureCalculatorMulti<FeatureInputStack> createCalculator(
            CreateCalculatorHelper cc,
            ImageInitParams soImage,
            BoundReplaceStrategy<FeatureInputStack, ? extends ReplaceStrategy<FeatureInputStack>>
                    cachingStrategy)
            throws InitException {
        return cc.createCached(getImage(), soImage, cachingStrategy);
    }

    public FeatureCalculatorMulti<FeatureInputSingleObject> createSingle(
            CreateCalculatorHelper cc,
            ImageInitParams soImage,
            BoundReplaceStrategy<
                            FeatureInputSingleObject,
                            CacheAndReuseStrategy<FeatureInputSingleObject>>
                    cachingStrategy)
            throws InitException {
        return cc.createCached(getSingle(), soImage, cachingStrategy);
    }

    public FeatureCalculatorMulti<FeatureInputPairObjects> createPair(
            CreateCalculatorHelper cc,
            ImageInitParams soImage,
            CacheTransferSourceCollection cacheTransferSource)
            throws InitException {
        return cc.createPair(getPair(), soImage, cacheTransferSource);
    }
}
