package org.anchoranalysis.feature.cache.calculation;

import org.anchoranalysis.feature.calc.params.FeatureInput;


/**
 * Given a CacheableCalculation, a resolver searches among a cache to see if an existing calculation already exists, and if so reuses.
 * 
 * <p>The purpose is to avoid repeating calculations that may be shared by more than one feature, or more than one parameterization of a feature</p>
 * 
 * @author Owen Feehan
 *
 * @param <T> feature input-type that also provides an input to the calculation
 */
public interface CalculationResolver<T extends FeatureInput> {

	/**
	 * 
	 * Returns an equivalent CachedCalculation to what is passed. Different caches implement different
	 * strategies.
	 * 
	 * @param cc the cached-calculation to find an equivalent for
	 * @return
	 */
	<S> ResolvedCalculation<S,T> search( CacheableCalculation<S,T> cc );

	
	/**
	 * 
	 * Returns an equivalent CachedCalculationMap to what is passed. Different caches implement different
	 * strategies.
	 * 
	 * @param cc the cached-calculation map to find an equivalent for
	 * @return
	 */
	<S,U> ResolvedCalculationMap<S,T,U> search( CacheableCalculationMap<S,T,U> cc );
}
