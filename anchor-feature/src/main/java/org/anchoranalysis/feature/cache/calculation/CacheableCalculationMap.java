/* (C)2020 */
package org.anchoranalysis.feature.cache.calculation;

import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Similar to a {@link CacheableCalculation} but stores several evaluations parameterised by a key
 *
 * @author Owen Feehan
 * @param <S> result-type
 * @param <T> feature input-type
 * @param <U> key-type
 * @param <E> an exception thrown if something goes wrong during the calculation
 */
public abstract class CacheableCalculationMap<S, T extends FeatureInput, U, E extends Exception>
        implements ResettableCalculation {
    /**
     * Executes the operation and returns a result, either by doing the calculation, or retrieving a
     * cached-result from previously.
     *
     * @param If there is no cached-value, and the calculation occurs, these parameters are used.
     *     Otherwise ignored.
     * @return the result of the calculation
     * @throws E if the calculation cannot finish, for whatever reason
     */
    public abstract S getOrCalculate(T input, U key) throws E;

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();
}
