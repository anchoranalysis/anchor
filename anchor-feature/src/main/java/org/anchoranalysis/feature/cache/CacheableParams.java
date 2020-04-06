package org.anchoranalysis.feature.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculation;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculationMap;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.session.cache.ICachedCalculationSearch;

/**
 * Wraps a params with a structure for adding cachable objects
 * 
 * @param T type of params
 * 
 * @author owen
 *
 */
public class CacheableParams<T extends FeatureCalcParams> implements ICachedCalculationSearch {

	private FeatureSessionCacheRetriever cache;
	private Map<String, FeatureSessionCacheRetriever> children = new HashMap<>();
	
	private T params;
	private Supplier<FeatureSessionCacheRetriever> factory;
	
	public CacheableParams(T params, Supplier<FeatureSessionCacheRetriever> factory) {
		this.params = params;
		this.factory = factory;
		this.cache = factory.get();
	}
	
	private CacheableParams(T params, FeatureSessionCacheRetriever cache, Supplier<FeatureSessionCacheRetriever> factory) {
		this.params = params;
		this.cache = cache;
		this.factory = factory;
	}
	
	public FeatureSessionCacheRetriever getCacheSession() {
		return cache;
	}
	
	public FeatureSessionCacheRetriever cacheFor(String childName) {
		return children.computeIfAbsent(childName, s -> factory.get() );
	}

	public T getParams() {
		return params;
	}

	public <S> CachedCalculation<S> search(CachedCalculation<S> cc) {
		return cache.search(cc);
	}
	
	@Override
	public <S, U> CachedCalculationMap<S, U> search(CachedCalculationMap<S, U> cc) {
		return cache.search(cc);
	}
	
	public double calc(Feature feature)
			throws FeatureCalcException {
		return cache.calc(feature, (CacheableParams<? extends FeatureCalcParams>) this);
	}

	public ResultsVector calc(List<Feature> features)
			throws FeatureCalcException {
		return cache.calc(features, (CacheableParams<? extends FeatureCalcParams>) this );
	}
	
	public <S> S calc(CachedCalculation<S> cc) throws ExecuteException {
		return search(cc).getOrCalculate(params);
	}
	
	public <S> S calc(CachedCalculation<S> cc, String childName) throws ExecuteException {
		return cacheFor(childName).search(cc).getOrCalculate(params);
	}

	public <S extends FeatureCalcParams> CacheableParams<S> changeParams( S paramsNew ) {
		return new CacheableParams<S>(
			paramsNew,
			cache,
			factory
		);
	}
	
	public <S extends FeatureCalcParams> CacheableParams<S> changeParams( S paramsNew, String childName ) {
		return new CacheableParams<S>(
			paramsNew,
			cacheFor(childName),
			factory
		);
	}
	
	public <S extends FeatureCalcParams> double calcChangeParams(Feature feature, S params, String childName) throws FeatureCalcException {
		FeatureSessionCacheRetriever child = cacheFor(childName); 
		return child.calc(
			feature,
			new CacheableParams<S>(
				params,
				child,
				factory
			)
		);
	}

	public String resolveFeatureID(String id) {
		return cache.resolveFeatureID(id);
	}

	public double calcFeatureByID(String resolvedID, CacheableParams<? extends FeatureCalcParams> params)
			throws FeatureCalcException {
		return cache.calcFeatureByID(resolvedID, params);
	}
}
