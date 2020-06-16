package org.anchoranalysis.image.feature.objmask.collection;

import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.image.objectmask.ObjectCollection;

public class FeatureInputObjs extends FeatureInputNRGStack {

	private ObjectCollection objMaskCollection;
	
	public FeatureInputObjs(ObjectCollection objMaskCollection) {
		this.objMaskCollection = objMaskCollection;
	}

	public ObjectCollection getObjMaskCollection() {
		return objMaskCollection;
	}

	public void setObjMaskCollection(ObjectCollection objMaskCollection) {
		this.objMaskCollection = objMaskCollection;
	}
}
