package org.anchoranalysis.feature.cache;

/*-
 * #%L
 * anchor-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import java.util.List;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.calculation.CalculationResolver;
import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculation;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Encapsulates a feature-input in the context of a particular session.
 * 
 * @param T underlying feature-input type
 * 
 * @author owen
 *
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
	double calc(Feature<T> feature) throws FeatureCalcException;
	
	
	/**
	 * Calculates the results of several features using this input
	 * 
	 * @param features features to calculate with
	 * @return the results of each feature's calculation respectively
	 * @throws FeatureCalcException
	 */
	ResultsVector calc(List<Feature<T>> features) throws FeatureCalcException;
		
	/**
	 * Calculates a feature in a child-cache
	 * 
	 * @param <S> input-type for feature to calculate
	 * @param feature feature to calculate with
	 * @param input input for feature
	 * @param childCacheName a unique-name for a child-cache to use for the feature-calculation
	 * @return the result of the feature calculation
	 * @throws FeatureCalcException
	 */
	<S extends FeatureInput> double calcChild(Feature<S> feature, S input, String childCacheName) throws FeatureCalcException;
	
	
	/**
	 * Calculates a feature in a child-cache using a new input created from a {@link #CachedCalculation}
	 * 
	 * @param <S> input-type for feature to calculate
	 * @param feature feature to calculate with
	 * @param calculation feature-calculation to generate input for the feature
	 * @param childCacheName a unique-name for a child-cache to use for the feature-calculation
	 * @return the result of the feature calculation
	 * @throws FeatureCalcException
	 */
	<S extends FeatureInput> double calcChild(Feature<S> feature, FeatureCalculation<S,T> calculation, String childCacheName) throws FeatureCalcException;
	
	
	/**
	 * Calculates a feature-calculation after resolving it against the main cache
	 * 
	 * @param <S> return-type of the calculation
	 * @param calculation the feature-calculation to resolve
	 * @return the result of the calculation
	 * @throws FeatureCalcException
	 */
	<S> S calc(FeatureCalculation<S,T> calculation) throws FeatureCalcException;
	
	
	/**
	 * Calculates a resolved Feature-calculation
	 * @param <S> return-type of the calculation
	 * @param cc the feature-calculation to resolve
	 * @return the result of the calculation
	 * @throws FeatureCalcException
	 */
	<S> S calc(ResolvedCalculation<S,T> cc) throws FeatureCalcException;
		
	
	/**
	 * Returns a resolver for calculations
	 * 
	 * @return
	 */
	CalculationResolver<T> resolver();
	
	/**
	 * A resolver associated with a particular child-cache
	 * 
	 * <p>This function trusts the caller to use the correct type associated with the child-cache.</p>
	 * 
	 * @param <V> params-type of the child cache to found
	 * @param childCacheName name of the child-cache
	 * @param paramsType the type of V
	 * @return the existing or new child cache of the given name
	 */
	<V extends FeatureInput> CalculationResolver<V> resolverForChild(String childCacheName, Class<?> paramsType);
	
	
	/**
	 * Calculates a feature if only an symbol (ID/name) is known, which refers to another feature.
	 * @return
	 */
	FeatureSymbolCalculator<T> bySymbol();
}