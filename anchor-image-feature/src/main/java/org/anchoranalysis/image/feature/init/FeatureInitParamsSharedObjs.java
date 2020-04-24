package org.anchoranalysis.image.feature.init;

import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.image.init.ImageInitParams;

/**
 * Extension of typical feature initialization, that also sets shared objects
 * 
 * @author owen
 *
 */
public class FeatureInitParamsSharedObjs extends FeatureInitParams {

	private ImageInitParams sharedObjects;
	
	public FeatureInitParamsSharedObjs( ImageInitParams so ) {
		super();
		this.sharedObjects = so;
	}
	
	private FeatureInitParamsSharedObjs( FeatureInitParams parent, ImageInitParams so ) {
		super(parent);
		this.sharedObjects = so;
	}

	public ImageInitParams getSharedObjects() {
		return sharedObjects;
	}

	@Override
	public FeatureInitParams duplicate() {
		return new FeatureInitParamsSharedObjs(super.duplicate(), sharedObjects);
	}
	
	
}
