package org.anchoranalysis.feature.cache.calculation;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

/**
 * Like a {@link CachedCalculation} but has been resolved against a cache to ensure its unique (singular).
 * 
 * <p>This operation should always occur before a cached-calculation is used</p>
 * 
 * @author Owen Feehan
 *
 * @param <S>
 * @param <T>
 */
public class RslvdCachedCalculation<S, T extends FeatureCalcParams> {

	private CachedCalculation<S, T> cachedCalculation;

	public RslvdCachedCalculation(CachedCalculation<S, T> cachedCalculation) {
		super();
		this.cachedCalculation = cachedCalculation;
	}
	
	/**
	 * Executes the operation and returns a result, either by doing the calculation, or retrieving
	 *   a cached-result from previously.
	 * 
	 * @param params If there is no existing cached-value, and the calculation occurs, these parameters are used. Otherwise ignored.
	 * @return the result of the calculation
	 * @throws ExecuteException if the calculation cannot finish, for whatever reason
	 */
	public S getOrCalculate( T params) throws ExecuteException {
		return cachedCalculation.getOrCalculate(params);
	}
	
	// We delegate to the CachedCalculation to check equality. Needed for the search.
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RslvdCachedCalculation) {
			return ((RslvdCachedCalculation<S, T>) obj).cachedCalculation.equals(
				cachedCalculation
			);
		} else {
			return false;
		}
	}

	// We delegate to the CachedCalculation to check hashCode. Needed for the search.
	@Override
	public int hashCode() {
		return cachedCalculation.hashCode();
	}
}
