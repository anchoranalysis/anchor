package org.anchoranalysis.feature.cache.calculation;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Like a {@link CacheableCalculation} but has been resolved against a cache to ensure its unique (singular).
 * 
 * <p>This operation should always occur before a cached-calculation is used</p>
 * 
 * @author Owen Feehan
 *
 * @param <S> result-type of the calculation
 * @param <T> feature input-type
 * @param <E> exception that is thrown if something goes wrong during calculation
 */
public class ResolvedCalculation<S, T extends FeatureInput> {

	private CacheableCalculation<S, T, FeatureCalcException> calc;

	/**
	 * Constructor
	 * 
	 * @param calc the cacheable-calculation that is now considered resolved
	 */
	public ResolvedCalculation(CacheableCalculation<S, T, FeatureCalcException> calc) {
		super();
		this.calc = calc;
	}
	
	/**
	 * Executes the operation and returns a result, either by doing the calculation, or retrieving
	 *   a cached-result from previously.
	 * 
	 * @param input If there is no existing cached-value, and the calculation occurs, these parameters are used. Otherwise ignored.
	 * @return the result of the calculation
	 * @throws ExecuteException if the calculation cannot finish, for whatever reason
	 */
	public S getOrCalculate( T input) throws FeatureCalcException {
		return calc.getOrCalculate(input);
	}
	
	// We delegate to the CachedCalculation to check equality. Needed for the search.
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ResolvedCalculation) {
			return ((ResolvedCalculation<S, T>) obj).calc.equals(calc);
		} else {
			return false;
		}
	}

	// We delegate to the CachedCalculation to check hashCode. Needed for the search.
	@Override
	public int hashCode() {
		return calc.hashCode();
	}
}
