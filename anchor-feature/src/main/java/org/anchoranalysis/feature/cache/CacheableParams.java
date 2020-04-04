package org.anchoranalysis.feature.cache;

import java.util.List;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculation;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheFactory;
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
	private FeatureSessionCacheFactory factory;

	public CacheableParams(T params, FeatureSessionCacheFactory factory) {
		this.params = params;
		this.factory = factory;
		//this.cacheSession = factory.create(namedFeatures, sharedFeatures);
	}
	
	private CacheableParams(T params, CacheSession cacheSession) {
		this.params = params;
		this.cacheSession = cacheSession;
	}
	
	public FeatureSessionCacheRetriever getCacheSession() {
		return cacheSession.main();
	}
	
	public FeatureSessionCacheRetriever cacheFor(String sessionName) {
		return cacheSession.additional(0);
	}

	public T getParams() {
		return params;
	}

	public <S> CachedCalculation<S> search(CachedCalculation<S> cc) {
		return cacheSession.search(cc);
	}

	public CachedCalculation<FeatureSessionCacheRetriever> initThroughSubcacheSession(String subCacheName,
			FeatureInitParams params, Feature item, LogErrorReporter logger) throws InitException {
		return cacheSession.initThroughSubcacheSession(subCacheName, params, item, logger);
	}
	
	@SuppressWarnings("unchecked")
	public double calc(Feature feature)
			throws FeatureCalcException {
		return cacheSession.calc(feature, (CacheableParams<? extends FeatureCalcParams>) this);
	}

	@SuppressWarnings("unchecked")
	public ResultsVector calc(List<Feature> features)
			throws FeatureCalcException {
		return cacheSession.calc(features, (CacheableParams<? extends FeatureCalcParams>) this );
	}

	public <S> CacheableParams<S> changeParams( S paramsNew ) {
		return new CacheableParams<S>(paramsNew, cacheSession);
	}
	
	public <S extends FeatureCalcParams> double calcChangeParams(Feature feature, S params, String sessionName) throws FeatureCalcException {
		return cacheSession.calc(
			feature,
			changeParams(params)
		);
	}
}
