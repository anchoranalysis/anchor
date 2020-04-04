package org.anchoranalysis.feature.cache;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculation;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculationMap;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;

/**
 * Wraps a params with a structure for adding cachable objects
 * 
 * @param T type of params
 * 
 * @author owen
 *
 */
public class CacheableParams<T> {

	private CacheSession cacheSession;
	private T params;

	public CacheableParams(T params) {
		this.params = params;
	}
	
	private CacheableParams(T params, CacheSession cacheSession) {
		this.params = params;
		this.cacheSession = cacheSession;
	}
	
	public CacheSession getCacheSession() {
		return cacheSession;
	}

	public void setCacheSession(CacheSession cacheSession) {
		this.cacheSession = cacheSession;
	}

	public T getParams() {
		return params;
	}

	public <S> CachedCalculation<S> search(CachedCalculation<S> cc) {
		return cacheSession.search(cc);
	}

	public <S, U> CachedCalculationMap<S, U> search(CachedCalculationMap<S, U> cc) {
		return cacheSession.search(cc);
	}

	public CachedCalculation<FeatureSessionCacheRetriever> initThroughSubcacheSession(String subCacheName,
			FeatureInitParams params, Feature item, LogErrorReporter logger) throws InitException {
		return cacheSession.initThroughSubcacheSession(subCacheName, params, item, logger);
	}

	public void initThroughSubcache(FeatureSessionCacheRetriever subCache, FeatureInitParams params, Feature item,
			LogErrorReporter logger) throws InitException {
		cacheSession.initThroughSubcache(subCache, params, item, logger);
	}
	
	public <S> CacheableParams<S> changeParams( S paramsNew ) {
		return new CacheableParams<S>(paramsNew, cacheSession);
	}
}
