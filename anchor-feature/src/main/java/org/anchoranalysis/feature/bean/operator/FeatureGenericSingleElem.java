package org.anchoranalysis.feature.bean.operator;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

/**
 * A single-element feature that accepts the most generic of parameters {#link {@link FeatureCalcParams}}
 * 
 * @author owen
 * @params feature-calc-params
 */
public abstract class FeatureGenericSingleElem<T extends FeatureCalcParams> extends FeatureSingleElem<T, T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FeatureGenericSingleElem() {
		super();
	}

	public FeatureGenericSingleElem(Feature<T> feature) {
		super(feature);
	}
}
