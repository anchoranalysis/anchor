package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.chnl.Chnl;

public abstract class ChnlProviderOne extends ChnlProvider {

	// START BEAN PROPERTIES
	@BeanField
	private ChnlProvider chnlProvider;
	// END BEAN PROPERTIES
	
	@Override
	public Chnl create() throws CreateException {
		return createFromChnl(
			chnlProvider.create()	
		);
	}
	
	protected abstract Chnl createFromChnl(Chnl chnl) throws CreateException;

	public ChnlProvider getChnlProvider() {
		return chnlProvider;
	}

	public void setChnlProvider(ChnlProvider chnlProvider) {
		this.chnlProvider = chnlProvider;
	}
}
