package org.anchoranalysis.anchor.mpp.feature.input.memo;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputPairMemoDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputPairMemoDescriptor instance = new FeatureInputPairMemoDescriptor();
	
	private FeatureInputPairMemoDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}

}
