package org.anchoranalysis.anchor.mpp.feature.bean.mark;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputMarkDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputMarkDescriptor instance = new FeatureInputMarkDescriptor();
	
	private FeatureInputMarkDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}

}
