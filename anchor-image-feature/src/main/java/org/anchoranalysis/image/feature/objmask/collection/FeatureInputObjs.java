package org.anchoranalysis.image.feature.objmask.collection;

import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.image.objectmask.ObjectMaskCollection;

public class FeatureInputObjs extends FeatureInputNRGStack {

	private ObjectMaskCollection objMaskCollection;
	
	public FeatureInputObjs(ObjectMaskCollection objMaskCollection) {
		this.objMaskCollection = objMaskCollection;
	}

	public ObjectMaskCollection getObjMaskCollection() {
		return objMaskCollection;
	}

	public void setObjMaskCollection(ObjectMaskCollection objMaskCollection) {
		this.objMaskCollection = objMaskCollection;
	}
}
