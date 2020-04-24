package org.anchoranalysis.image.feature.stack;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputStackDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputStackDescriptor instance = new FeatureInputStackDescriptor();
	
	private FeatureInputStackDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}


}
