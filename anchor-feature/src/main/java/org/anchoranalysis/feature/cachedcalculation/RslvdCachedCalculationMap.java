package org.anchoranalysis.feature.cachedcalculation;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

public class RslvdCachedCalculationMap<S, T extends FeatureCalcParams,U> {

	private CachedCalculationMap<S,T,U> cachedCalculationMap;

	public RslvdCachedCalculationMap(CachedCalculationMap<S, T, U> cachedCalculationMap) {
		super();
		this.cachedCalculationMap = cachedCalculationMap;
	}
	
	public S getOrCalculate(T params, U key) throws FeatureCalcException {
		return cachedCalculationMap.getOrCalculate(params, key);
	}
}
