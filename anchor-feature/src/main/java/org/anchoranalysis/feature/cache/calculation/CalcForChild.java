/* (C)2020 */
package org.anchoranalysis.feature.cache.calculation;

import java.util.function.Function;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Performs calculations using child-caches instead of the main cache
 *
 * @author Owen Feehan
 * @param <T>
 */
public interface CalcForChild<T extends FeatureInput> {

    /**
     * Calculates a feature in a child-cache
     *
     * @param <S> input-type for feature to calculate
     * @param feature feature to calculate with
     * @param input input for feature
     * @param childCacheName a unique-name for a child-cache to use for the feature-calculation
     * @return the result of the feature calculation
     * @throws FeatureCalcException
     */
    <S extends FeatureInput> double calc(Feature<S> feature, S input, ChildCacheName childCacheName)
            throws FeatureCalcException;

    /**
     * Calculates a feature in a child-cache using a new input created from a {@link
     * FeatureCalculation}
     *
     * @param <S> input-type for feature to calculate
     * @param feature feature to calculate with
     * @param calculation feature-calculation to generate input for the feature
     * @param childCacheName a unique-name for a child-cache to use for the feature-calculation
     * @return the result of the feature calculation
     * @throws FeatureCalcException
     */
    <S extends FeatureInput> double calc(
            Feature<S> feature, FeatureCalculation<S, T> calculation, ChildCacheName childCacheName)
            throws FeatureCalcException;

    /**
     * Calculates a {@link FeatureCalculation} in a child-cache
     *
     * @param <S> input-type for feature to calculate
     * @param <U> return-type of Feature-Calculation
     * @param childCacheName name of child cache
     * @param input input to be used for calculation
     * @param funcResolve returns the resolved Feature-Calculation to be used (given a resolver)
     * @return the restulf othe calculation
     * @throws FeatureCalcException
     */
    <S extends FeatureInput, U> U calc(
            ChildCacheName childCacheName,
            S input,
            Function<CalculationResolver<S>, ResolvedCalculation<U, S>> funcResolve)
            throws FeatureCalcException;
}
