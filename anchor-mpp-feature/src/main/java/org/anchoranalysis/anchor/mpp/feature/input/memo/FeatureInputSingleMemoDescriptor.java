package org.anchoranalysis.anchor.mpp.feature.input.memo;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputSingleMemoDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputSingleMemoDescriptor instance = new FeatureInputSingleMemoDescriptor();
	
	private FeatureInputSingleMemoDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}

}
