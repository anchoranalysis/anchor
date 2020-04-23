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
import java.util.function.Function;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.calculation.CachedCalculation;
import org.anchoranalysis.feature.cache.calculation.RslvdCachedCalculation;
import org.anchoranalysis.feature.cache.calculation.map.CachedCalculationMap;
import org.anchoranalysis.feature.cache.calculation.map.RslvdCachedCalculationMap;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureInput;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheCalculator;
import org.anchoranalysis.feature.session.cache.ICachedCalculationSearch;

/**
 * Wraps a params with a structure for adding cachable objects
 * 
 * @param T feature-input
 * 
 * @author owen
 *
 */
public class CacheableParams<T extends FeatureInput> implements ICachedCalculationSearch<T> {

	private FeatureSessionCache<T> cache;
		
	private T params;
	private CacheCreator factory;
	
	public CacheableParams(T params, CacheCreator factory) {
		this.params = params;
		this.factory = factory;
		
		// Deliberately two lines, as it needs an explicitly declared type for the template type inference to work
		this.cache = factory.create( params.getClass() ); 
	}
	
	private CacheableParams(T params, FeatureSessionCache<T> cache, CacheCreator factory) {
		this.params = params;
		this.factory = factory;
		this.cache = cache;
	}
	
	
	/** 
	 * Replaces existing params with new params
	 * 
	 * @param params new parameters which will replace existing ones
	 **/
	public void replaceParams(T params) {
		cache.invalidate();
		this.params = params;
	}
	
	/**
	 * Gets/creates a child-cache for a given name
	 * 
	 * <p>This function trusts the caller to use the correct type for the child-cache.</p>
	 * 
	 * @param <V> params-type of the child cache to found
	 * @param childName name of the child-cache
	 * @param paramsType the type of V
	 * @return the existing or new child cache of the given name
	 */
	public <V extends FeatureInput> FeatureSessionCacheCalculator<V> cacheFor(String childName, Class<?> paramsType) {
		FeatureSessionCache<V> cache = cacheForInternal(childName, paramsType);
		return cache.calculator();
	}
	
	private <V extends FeatureInput> FeatureSessionCache<V> cacheForInternal(String childName, Class<?> paramsType) {
		return cache.childCacheFor(childName, paramsType, factory);
	}

	public T getParams() {
		return params;
	}

	public <S> RslvdCachedCalculation<S, T> search(CachedCalculation<S, T> cc) {
		return cache.calculator().search(cc);
	}
	
	@Override
	public <S, U> RslvdCachedCalculationMap<S, T, U> search(CachedCalculationMap<S, T, U> cc) {
		return cache.calculator().search(cc);
	}
	
	public double calc(Feature<T> feature)
			throws FeatureCalcException {
		return cache.calculator().calc(feature, this);
	}

	public ResultsVector calc(List<Feature<T>> features)
			throws FeatureCalcException {
		return cache.calculator().calc(features, this );
	}
	
	public <S> S calc(CachedCalculation<S,T> cc) throws FeatureCalcException {
		try {
			RslvdCachedCalculation<S,T> ccAfterSearch = search(cc); 
			return ccAfterSearch.getOrCalculate(params);
		} catch (ExecuteException e) {
			throw new FeatureCalcException(e.getCause());
		}
	}
	
	public <S> S calc(RslvdCachedCalculation<S,T> cc) throws FeatureCalcException {
		try {
			// No need to search as it's already resolved
			return cc.getOrCalculate(params);
		} catch (ExecuteException e) {
			throw new FeatureCalcException(e.getCause());
		}			
	}
	
	@SuppressWarnings("unchecked")
	public <S extends FeatureInput> CacheableParams<FeatureInput> upcastParams() throws FeatureCalcException {
		return (CacheableParams<FeatureInput>)(this);
	}
	
	/**
	 * Maps the parameters to a new type, which also leads to being assigned a new child-cache.
	 *  
	 * @param <S> the type of the new parameters
	 * @param deriveParamsFunc derives new parameters from existing
	 * @param childName unique name to use in the cache (other features can also reference the same childName)
	 * @return a new CacheableParams with derived-parameters and a cache that is a child of the existing cache
	 */
	public <S extends FeatureInput> CacheableParams<S> mapParams( Function<T,S> deriveParamsFunc, String childName ) {
		S paramsDerived = deriveParamsFunc.apply(params);
		return mapParamsSpecific(paramsDerived, childName);
	}
	
	private <S extends FeatureInput> CacheableParams<S> mapParamsSpecific( S paramsNew, String childName ) {
		return new CacheableParams<S>(
			paramsNew,
			cacheForInternal(childName, paramsNew.getClass()),
			factory
		);
	}
	
	public <S extends FeatureInput> double calcChangeParams(Feature<S> feature, Function<T,S> deriveParamsFunc, String childName) throws FeatureCalcException {
		S paramsDerived = deriveParamsFunc.apply(params);
		return calcChangeParamsDirect(feature, paramsDerived, childName);
	}

	public <S extends FeatureInput> double calcChangeParamsDirect(Feature<S> feature, CachedCalculation<S,T> cc, String childName) throws FeatureCalcException {
		return calcChangeParamsDirect(
			feature,
			calc(cc),
			childName
		);
	}
	
	public <S extends FeatureInput> double calcChangeParamsDirect(Feature<S> feature, S paramsDerived, String childName) throws FeatureCalcException {
		
		FeatureSessionCache<S> child = cacheForInternal(childName, paramsDerived.getClass()); 
		return child.calculator().calc(
			feature,
			new CacheableParams<S>(
				paramsDerived,
				child,
				factory
			)
		);
	}
	
	public String resolveFeatureID(String id) {
		return cache.calculator().resolveFeatureID(id);
	}

	public double calcFeatureByID(String resolvedID, CacheableParams<T> params)
			throws FeatureCalcException {
		return cache.calculator().calcFeatureByID(resolvedID, params);
	}
}
