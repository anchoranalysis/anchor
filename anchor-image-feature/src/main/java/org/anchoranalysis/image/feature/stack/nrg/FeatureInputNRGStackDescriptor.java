package org.anchoranalysis.image.feature.stack.nrg;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;

public class FeatureInputNRGStackDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputNRGStackDescriptor instance = new FeatureInputNRGStackDescriptor();
	
	private FeatureInputNRGStackDescriptor() {
		
	}
	
	// TODO it's not really compatible with Params that don't have an NRG stack. We should refine this interface
	@Override
	public boolean isCompatibleWithEverything() {
		return true;
	}

}
