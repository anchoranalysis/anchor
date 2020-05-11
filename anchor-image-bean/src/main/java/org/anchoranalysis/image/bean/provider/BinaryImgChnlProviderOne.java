package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.BinaryChnl;

public abstract class BinaryImgChnlProviderOne extends BinaryChnlProvider {

	// START BEAN PROPERTIES
	@BeanField
	private BinaryChnlProvider binaryImgChnlProvider;
	// END BEAN PROPERTIES
	
	@Override
	public BinaryChnl create() throws CreateException {
		return createFromChnl(
			binaryImgChnlProvider.create()
		);
	}
	
	protected abstract BinaryChnl createFromChnl( BinaryChnl chnl ) throws CreateException;

	public BinaryChnlProvider getBinaryImgChnlProvider() {
		return binaryImgChnlProvider;
	}

	public void setBinaryImgChnlProvider(BinaryChnlProvider binaryImgChnlProvider) {
		this.binaryImgChnlProvider = binaryImgChnlProvider;
	}
}
