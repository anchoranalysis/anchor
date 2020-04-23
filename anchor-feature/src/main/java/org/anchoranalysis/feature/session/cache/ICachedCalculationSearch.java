package org.anchoranalysis.feature.session.cache;

import org.anchoranalysis.feature.cache.calculation.CachedCalculation;
import org.anchoranalysis.feature.cache.calculation.RslvdCachedCalculation;
import org.anchoranalysis.feature.cache.calculation.map.CachedCalculationMap;
import org.anchoranalysis.feature.cache.calculation.map.RslvdCachedCalculationMap;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

public interface ICachedCalculationSearch<T extends FeatureCalcParams> {

	/**
	 * 
	 * Returns an equivalent CachedCalculation to what is passed. Different caches implement different
	 * strategies.
	 * 
	 * @param cc the cached-calculation to find an equivalent for
	 * @return
	 */
	<S> RslvdCachedCalculation<S,T> search( CachedCalculation<S,T> cc );

	
	/**
	 * 
	 * Returns an equivalent CachedCalculationMap to what is passed. Different caches implement different
	 * strategies.
	 * 
	 * @param cc the cached-calculation map to find an equivalent for
	 * @return
	 */
	<S,U> RslvdCachedCalculationMap<S,T,U> search( CachedCalculationMap<S,T,U> cc );
}
