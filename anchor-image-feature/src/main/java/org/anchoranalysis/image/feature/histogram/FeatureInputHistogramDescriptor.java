package org.anchoranalysis.image.feature.histogram;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputHistogramDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputHistogramDescriptor instance = new FeatureInputHistogramDescriptor();
	
	private FeatureInputHistogramDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}

}
