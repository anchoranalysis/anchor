package org.anchoranalysis.image.feature.objmask.pair.merged;

import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;
import org.anchoranalysis.image.feature.objmask.pair.FeatureInputPairObjsDescriptor;

public class FeatureInputPairObjsMergedDescriptor extends FeatureInputDescriptor {

	public static final FeatureInputPairObjsMergedDescriptor instance = new FeatureInputPairObjsMergedDescriptor();
	
	private FeatureInputPairObjsMergedDescriptor() {
		
	}
	
	@Override
	public boolean isCompatibleWithEverything() {
		return false;
	}

	@Override
	public FeatureInputDescriptor preferTo(FeatureInputDescriptor dscr) {

		if (dscr==FeatureInputPairObjsDescriptor.instance) {
			return this;
		}
		
		return super.preferTo(dscr);
	}
	
}
