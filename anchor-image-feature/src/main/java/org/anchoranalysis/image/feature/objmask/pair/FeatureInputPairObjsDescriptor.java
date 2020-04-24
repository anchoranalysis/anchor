package org.anchoranalysis.image.feature.objmask.pair;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputPairObjsDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputPairObjsDescriptor instance = new FeatureInputPairObjsDescriptor();
	
	private FeatureInputPairObjsDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}


}
