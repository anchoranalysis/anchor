/* (C)2020 */
package org.anchoranalysis.feature.session.strategy.replace;

import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

public interface ReplaceStrategy<T extends FeatureInput> {

    SessionInput<T> createOrReuse(T input) throws FeatureCalcException;
}
