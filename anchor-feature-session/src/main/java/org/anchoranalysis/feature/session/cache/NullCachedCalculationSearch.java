package org.anchoranalysis.feature.session.cache;

import org.anchoranalysis.feature.cache.calculation.CachedCalculation;
import org.anchoranalysis.feature.cache.calculation.RslvdCachedCalculation;
import org.anchoranalysis.feature.cache.calculation.map.CachedCalculationMap;
import org.anchoranalysis.feature.cache.calculation.map.RslvdCachedCalculationMap;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.cache.ICachedCalculationSearch;

public class NullCachedCalculationSearch<T extends FeatureCalcParams> implements ICachedCalculationSearch<T> {

	private static NullCachedCalculationSearch<?> instance = new NullCachedCalculationSearch<>();
	
	private NullCachedCalculationSearch() {
		
	}
	
	@Override
	public <S> RslvdCachedCalculation<S,T> search(CachedCalculation<S,T> cc) {
		return new RslvdCachedCalculation<>(cc);
	}

	@Override
	public <S,U> RslvdCachedCalculationMap<S,T,U> search(
			CachedCalculationMap<S,T,U> cc) {
		return new RslvdCachedCalculationMap<>(cc);
	}

	@SuppressWarnings("unchecked")
	public static <U extends FeatureCalcParams> NullCachedCalculationSearch<U> getInstance() {
		return (NullCachedCalculationSearch<U>) instance;
	}

}
