package org.anchoranalysis.feature.resultsvectorcollection;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputResultsDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputResultsDescriptor instance = new FeatureInputResultsDescriptor();
	
	private FeatureInputResultsDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}

}
