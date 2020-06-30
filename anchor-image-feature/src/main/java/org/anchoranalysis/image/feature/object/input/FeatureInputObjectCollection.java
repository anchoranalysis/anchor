package org.anchoranalysis.image.feature.object.input;

import java.util.Optional;

import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.objectmask.ObjectCollection;

public class FeatureInputObjectCollection extends FeatureInputNRGStack {

	private ObjectCollection objMaskCollection;
	
	public FeatureInputObjectCollection(ObjectCollection objMaskCollection) {
		this.objMaskCollection = objMaskCollection;
	}
	
	public FeatureInputObjectCollection(ObjectCollection objMaskCollection, Optional<NRGStackWithParams> nrgStack) {
		super(nrgStack);
		this.objMaskCollection = objMaskCollection;
	}

	public ObjectCollection getObjMaskCollection() {
		return objMaskCollection;
	}

	public void setObjMaskCollection(ObjectCollection objMaskCollection) {
		this.objMaskCollection = objMaskCollection;
	}
}
