package org.anchoranalysis.anchor.mpp.feature.bean.cfg;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputCfgDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputCfgDescriptor instance = new FeatureInputCfgDescriptor();
	
	private FeatureInputCfgDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}


}
