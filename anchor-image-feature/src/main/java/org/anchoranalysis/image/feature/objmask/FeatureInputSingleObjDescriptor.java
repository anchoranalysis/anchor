package org.anchoranalysis.image.feature.objmask;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputSingleObjDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputSingleObjDescriptor instance = new FeatureInputSingleObjDescriptor();
	
	private FeatureInputSingleObjDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}

}
