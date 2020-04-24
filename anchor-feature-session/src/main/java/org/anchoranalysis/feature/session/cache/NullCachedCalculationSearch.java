package org.anchoranalysis.feature.session.cache;

import org.anchoranalysis.feature.cache.calculation.CacheableCalculation;
import org.anchoranalysis.feature.cache.calculation.CacheableCalculationMap;
import org.anchoranalysis.feature.cache.calculation.CalculationResolver;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculation;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculationMap;
import org.anchoranalysis.feature.calc.params.FeatureInput;

public class NullCachedCalculationSearch<T extends FeatureInput> implements CalculationResolver<T> {

	private static NullCachedCalculationSearch<?> instance = new NullCachedCalculationSearch<>();
	
	private NullCachedCalculationSearch() {
		
	}
	
	@Override
	public <S> ResolvedCalculation<S,T> search(CacheableCalculation<S,T> cc) {
		return new ResolvedCalculation<>(cc);
	}

	@Override
	public <S,U> ResolvedCalculationMap<S,T,U> search(
			CacheableCalculationMap<S,T,U> cc) {
		return new ResolvedCalculationMap<>(cc);
	}

	@SuppressWarnings("unchecked")
	public static <U extends FeatureInput> NullCachedCalculationSearch<U> getInstance() {
		return (NullCachedCalculationSearch<U>) instance;
	}

}
