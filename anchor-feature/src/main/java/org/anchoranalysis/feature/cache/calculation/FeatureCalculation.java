package org.anchoranalysis.feature.cache.calculation;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

public abstract class FeatureCalculation<S, T extends FeatureInput> extends CacheableCalculation<S, T, FeatureCalcException> {

}
