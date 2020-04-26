package org.anchoranalysis.feature.cache.calculation;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * A {@link CalculationMap} that has been resolved against a cache.
 * 
 * @author Owen Feehan
 *
 * @param <S> result-type
 * @param <T> feature input-type
 * @param <U> key-type
 */
public class ResolvedCalculationMap<S, T extends FeatureInput,U> {

	private CacheableCalculationMap<S,T,U,FeatureCalcException> map;

	/**
	 * Constructor
	 * 
	 * @param map the cacheable-calculation map that is now considered resolved
	 */
	public ResolvedCalculationMap(CacheableCalculationMap<S,T,U,FeatureCalcException> map) {
		super();
		this.map = map;
	}
	
	public S getOrCalculate(T params, U key) throws FeatureCalcException {
		return map.getOrCalculate(params, key);
	}
	
	// We delegate to the CachedCalculationMap to check equality. Needed for the search.
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ResolvedCalculationMap) {
			return ((ResolvedCalculationMap<S, T, U>) obj).map.equals(map);
		} else {
			return false;
		}
	}

	// We delegate to the CachedCalculationMap to check hashCode. Needed for the search.
	@Override
	public int hashCode() {
		return map.hashCode();
	}
}
