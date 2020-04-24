package org.anchoranalysis.anchor.mpp.feature.input.memo;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputAllMemoDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputAllMemoDescriptor instance = new FeatureInputAllMemoDescriptor();
	
	private FeatureInputAllMemoDescriptor() {}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}

}
