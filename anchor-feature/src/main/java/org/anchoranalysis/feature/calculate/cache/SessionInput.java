/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.calculate.cache;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calculate.FeatureCalculation;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;

/**
 * Encapsulates a feature-input in the context of a particular session.
 *
 * @param <T> underlying feature-input type
 * @author Owen Feehan
 */
public interface SessionInput<T extends FeatureInput> {

    /** Returns the underlying feature-input (independent of the session) */
    T get();

    /**
     * Calculates the result of a feature using this input
     *
     * @param feature the feature to calculate with
     * @return the result of the calculation
     */
    double calculate(Feature<T> feature) throws FeatureCalculationException;

    /**
     * Calculates the results of several features using this input
     *
     * @param features features to calculate with
     * @return the results of each feature's calculation respectively
     */
    ResultsVector calculate(FeatureList<T> features) throws NamedFeatureCalculateException;

    /**
     * Calculates a feature-calculation after resolving it against the main cache
     *
     * @param <S> return-type of the calculation
     * @param calculation the feature-calculation to resolve
     * @return the result of the calculation
     * @throws FeatureCalculationException
     */
    <S> S calculate(FeatureCalculation<S, T> calculation) throws FeatureCalculationException;

    /**
     * Calculates a resolved Feature-calculation
     *
     * @param <S> return-type of the calculation
     * @param calculation the feature-calculation to resolve
     * @return the result of the calculation
     * @throws FeatureCalculationException
     */
    <S> S calculate(ResolvedCalculation<S, T> calculation) throws FeatureCalculationException;

    /**
     * Returns a resolver for calculations
     *
     * @return
     */
    CalculationResolver<T> resolver();

    /**
     * Performs calculations not on the main cache, but on a child cache
     *
     * @return
     */
    CalculateForChild<T> forChild();

    /**
     * Calculates a feature if only an symbol (ID/name) is known, which refers to another feature.
     *
     * @return
     */
    FeatureSymbolCalculator<T> bySymbol();

    public abstract FeatureSessionCache<T> getCache();
}
