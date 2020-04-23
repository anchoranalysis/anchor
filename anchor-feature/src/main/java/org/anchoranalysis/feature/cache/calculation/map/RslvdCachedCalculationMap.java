package org.anchoranalysis.feature.cache.calculation.map;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureInput;

public class RslvdCachedCalculationMap<S, T extends FeatureInput,U> {

	private CachedCalculationMap<S,T,U> cachedCalculationMap;

	public RslvdCachedCalculationMap(CachedCalculationMap<S, T, U> cachedCalculationMap) {
		super();
		this.cachedCalculationMap = cachedCalculationMap;
	}
	
	public S getOrCalculate(T params, U key) throws FeatureCalcException {
		return cachedCalculationMap.getOrCalculate(params, key);
	}
	
	// We delegate to the CachedCalculationMap to check equality. Needed for the search.
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RslvdCachedCalculationMap) {
			return ((RslvdCachedCalculationMap<S, T, U>) obj).cachedCalculationMap.equals(
				cachedCalculationMap
			);
		} else {
			return false;
		}
	}

	// We delegate to the CachedCalculationMap to check hashCode. Needed for the search.
	@Override
	public int hashCode() {
		return cachedCalculationMap.hashCode();
	}
}
