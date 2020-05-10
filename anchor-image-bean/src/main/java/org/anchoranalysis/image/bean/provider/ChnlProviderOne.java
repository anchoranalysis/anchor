package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.chnl.Chnl;

public abstract class ChnlProviderOne extends ChnlProvider {

	// START BEAN PROPERTIES
	@BeanField
	private ChnlProvider chnl;
	// END BEAN PROPERTIES
	
	@Override
	public Chnl create() throws CreateException {
		return createFromChnl(
			chnl.create()	
		);
	}
	
	protected abstract Chnl createFromChnl(Chnl chnl) throws CreateException;

	public ChnlProvider getChnl() {
		return chnl;
	}

	public void setChnl(ChnlProvider chnl) {
		this.chnl = chnl;
	}
}
