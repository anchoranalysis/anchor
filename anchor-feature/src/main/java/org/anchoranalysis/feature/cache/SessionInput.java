/* (C)2020 */
package org.anchoranalysis.feature.cache;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.calculation.CalcForChild;
import org.anchoranalysis.feature.cache.calculation.CalculationResolver;
import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculation;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Encapsulates a feature-input in the context of a particular session.
 *
 * @param T underlying feature-input type
 * @author Owen Feehan
 */
public interface SessionInput<T extends FeatureInput> {

    /** Returns the underlying feature-input (independent of the session) */
    T get();

    /**
     * Calculates the result of a feature using this input
     *
     * @param feature the feature to calculate with
     * @return the result of the calculation
     */
    double calc(Feature<T> feature) throws FeatureCalcException;

    /**
     * Calculates the results of several features using this input
     *
     * @param features features to calculate with
     * @return the results of each feature's calculation respectively
     * @throws FeatureCalcException
     */
    ResultsVector calc(FeatureList<T> features) throws FeatureCalcException;

    /**
     * Calculates a feature-calculation after resolving it against the main cache
     *
     * @param <S> return-type of the calculation
     * @param calculation the feature-calculation to resolve
     * @return the result of the calculation
     * @throws FeatureCalcException
     */
    <S> S calc(FeatureCalculation<S, T> calculation) throws FeatureCalcException;

    /**
     * Calculates a resolved Feature-calculation
     *
     * @param <S> return-type of the calculation
     * @param cc the feature-calculation to resolve
     * @return the result of the calculation
     * @throws FeatureCalcException
     */
    <S> S calc(ResolvedCalculation<S, T> cc) throws FeatureCalcException;

    /**
     * Returns a resolver for calculations
     *
     * @return
     */
    CalculationResolver<T> resolver();

    /**
     * Performs calculations not on the main cache, but on a child cache
     *
     * @return
     */
    CalcForChild<T> forChild();

    /**
     * Calculates a feature if only an symbol (ID/name) is known, which refers to another feature.
     *
     * @return
     */
    FeatureSymbolCalculator<T> bySymbol();

    public abstract FeatureSessionCache<T> getCache();
}
