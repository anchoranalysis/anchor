package org.anchoranalysis.feature.bean.operator;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Base class for features that broadly accept any type of feature-input.
 * 
 * @author Owen Feehan
 *
 * @param <T>
 */
public abstract class FeatureOperator<T extends FeatureInput> extends Feature<T> {

	@Override
	public Class<? extends FeatureInput> inputType() {
		return FeatureInput.class;
	}
}
