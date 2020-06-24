package org.anchoranalysis.image.feature.objmask.collection;

import java.util.Optional;

import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.objectmask.ObjectCollection;

public class FeatureInputObjs extends FeatureInputNRGStack {

	private ObjectCollection objMaskCollection;
	
	public FeatureInputObjs(ObjectCollection objMaskCollection) {
		this.objMaskCollection = objMaskCollection;
	}
	
	public FeatureInputObjs(ObjectCollection objMaskCollection, Optional<NRGStackWithParams> nrgStack) {
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
