/* (C)2020 */
package org.anchoranalysis.feature.session.cache.horizontal;

import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CacheableCalculation;
import org.anchoranalysis.feature.cache.calculation.CacheableCalculationMap;
import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCacheCalculator;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculation;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculationMap;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

class ResettableCachedCalculator<T extends FeatureInput>
        implements FeatureSessionCacheCalculator<T> {

    private ResettableSet<FeatureCalculation<?, T>> setCalculation = new ResettableSet<>(false);
    private ResettableSet<CacheableCalculationMap<?, T, ?, FeatureCalcException>>
            setCalculationMap = new ResettableSet<>(false);

    private Logger logger;
    private SharedFeatureSet<T> sharedFeatures;

    public ResettableCachedCalculator(SharedFeatureSet<T> sharedFeatures) {
        super();
        this.sharedFeatures = sharedFeatures;
    }

    public void init(Logger logger) {
        this.logger = logger;
    }

    @Override
    public double calc(Feature<T> feature, SessionInput<T> input) throws FeatureCalcException {
        double val = feature.calcCheckInit(input);
        if (Double.isNaN(val)) {
            logger.messageLogger()
                    .logFormatted(
                            "WARNING: NaN returned from feature %s", feature.getFriendlyName());
        }
        return val;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> ResolvedCalculation<U, T> search(FeatureCalculation<U, T> calculation) {
        return new ResolvedCalculation<>(
                (CacheableCalculation<U, T, FeatureCalcException>)
                        setCalculation.findOrAdd(calculation, null));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S, U> ResolvedCalculationMap<S, T, U> search(
            CacheableCalculationMap<S, T, U, FeatureCalcException> calculation) {
        return new ResolvedCalculationMap<>(
                (CacheableCalculationMap<S, T, U, FeatureCalcException>)
                        setCalculationMap.findOrAdd(calculation, null));
    }

    @Override
    public double calcFeatureByID(String id, SessionInput<T> input) throws FeatureCalcException {
        try {
            Feature<T> feature = sharedFeatures.getException(id);
            return calc(feature, input);
        } catch (NamedProviderGetException e) {
            throw new FeatureCalcException(
                    String.format("Cannot locate feature with resolved-ID: %s", id), e.summarize());
        }
    }

    public void invalidate() {
        setCalculation.invalidate();
        setCalculationMap.invalidate();
    }

    @Override
    public String resolveFeatureID(String id) {
        return id;
    }
}
