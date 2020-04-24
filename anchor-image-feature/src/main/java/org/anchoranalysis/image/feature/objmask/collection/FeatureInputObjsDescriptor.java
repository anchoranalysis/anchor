package org.anchoranalysis.image.feature.objmask.collection;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputObjsDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputObjsDescriptor instance = new FeatureInputObjsDescriptor();
	
	private FeatureInputObjsDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}


}
