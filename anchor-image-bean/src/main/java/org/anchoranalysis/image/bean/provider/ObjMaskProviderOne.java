package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

public abstract class ObjMaskProviderOne extends ObjMaskProvider {

	// START BEAN PROPERTIES
	@BeanField
	private ObjMaskProvider objs;
	// END BEAN PROPERTIES
	
	@Override
	public ObjMaskCollection create() throws CreateException {
		return createFromObjs(
			objs.create()
		);
	}
	
	protected abstract ObjMaskCollection createFromObjs( ObjMaskCollection objsCollection ) throws CreateException;
	
	public ObjMaskProvider getObjs() {
		return objs;
	}

	public void setObjs(ObjMaskProvider objs) {
		this.objs = objs;
	}
}
