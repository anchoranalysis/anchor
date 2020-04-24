package org.anchoranalysis.feature.input.descriptor;

/**
 * For features that are compatible with all inut-types...... so long as they have key-value-params.
 * @author Owen Feehan
 *
 */
public class FeatureInputParamsDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputParamsDescriptor instance = new FeatureInputParamsDescriptor();
	
	private FeatureInputParamsDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}

}
