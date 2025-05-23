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

import lombok.Getter;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.cache.finder.CacheTransferSourceCollection;
import org.anchoranalysis.feature.session.replace.BoundReplaceStrategy;
import org.anchoranalysis.feature.session.replace.CacheAndReuseStrategy;
import org.anchoranalysis.feature.session.replace.ReplaceStrategy;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitialization;
import org.anchoranalysis.image.feature.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.input.FeatureInputSingleObject;
import org.anchoranalysis.image.feature.input.FeatureInputStack;

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

    /**
     * Creates a duplicate of this MergedPairsFeatures instance.
     *
     * @return A new MergedPairsFeatures object with duplicated feature lists
     */
    public MergedPairsFeatures duplicate() {
        return new MergedPairsFeatures(
                image.duplicateBean(), single.duplicateBean(), pair.duplicateBean());
    }

    /**
     * Gets the number of image features.
     *
     * @return The number of features in the image feature list
     */
    public int numberImageFeatures() {
        return image.size();
    }

    /**
     * Gets the number of single object features.
     *
     * @return The number of features in the single object feature list
     */
    public int numberSingleFeatures() {
        return single.size();
    }

    /**
     * Gets the number of pair features.
     *
     * @return The number of features in the pair feature list
     */
    public int numberPairFeatures() {
        return pair.size();
    }

    /**
     * Creates a feature calculator for stack features.
     *
     * @param calculatorCreator Helper for creating calculators
     * @param initialization Image initialization parameters
     * @param cachingStrategy Strategy for caching and replacing feature calculations
     * @return A multi-feature calculator for stack features
     * @throws InitializeException If initialization fails
     */
    public FeatureCalculatorMulti<FeatureInputStack> createCalculator(
            CreateCalculatorHelper calculatorCreator,
            ImageInitialization initialization,
            BoundReplaceStrategy<FeatureInputStack, ? extends ReplaceStrategy<FeatureInputStack>>
                    cachingStrategy)
            throws InitializeException {
        return calculatorCreator.createCached(getImage(), initialization, cachingStrategy);
    }

    /**
     * Creates a feature calculator for single object features.
     *
     * @param calculatorCreator Helper for creating calculators
     * @param initialization Image initialization parameters
     * @param cachingStrategy Strategy for caching and replacing feature calculations
     * @return A multi-feature calculator for single object features
     * @throws InitializeException If initialization fails
     */
    public FeatureCalculatorMulti<FeatureInputSingleObject> createSingle(
            CreateCalculatorHelper calculatorCreator,
            ImageInitialization initialization,
            BoundReplaceStrategy<
                            FeatureInputSingleObject,
                            CacheAndReuseStrategy<FeatureInputSingleObject>>
                    cachingStrategy)
            throws InitializeException {
        return calculatorCreator.createCached(getSingle(), initialization, cachingStrategy);
    }

    /**
     * Creates a feature calculator for pair object features.
     *
     * @param calculatorCreator Helper for creating calculators
     * @param initialization Image initialization parameters
     * @param cacheTransferSource Collection of cache transfer sources
     * @return A multi-feature calculator for pair object features
     * @throws InitializeException If initialization fails
     */
    public FeatureCalculatorMulti<FeatureInputPairObjects> createPair(
            CreateCalculatorHelper calculatorCreator,
            ImageInitialization initialization,
            CacheTransferSourceCollection cacheTransferSource)
            throws InitializeException {
        return calculatorCreator.createPair(getPair(), initialization, cacheTransferSource);
    }
}
