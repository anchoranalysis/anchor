package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.histogram.Histogram;

public abstract class HistogramProviderOne extends HistogramProvider {

	// START BEAN PROPERTIES
	@BeanField
	private HistogramProvider histogramProvider;
	// END BEAN PROPERTIES
	
	@Override
	public Histogram create() throws CreateException {
		Histogram hist = histogramProvider.create();
		return createFromHistogram(hist);
	}
	
	protected abstract Histogram createFromHistogram( Histogram hist ) throws CreateException;
	
	public HistogramProvider getHistogramProvider() {
		return histogramProvider;
	}

	public void setHistogramProvider(HistogramProvider histogramProvider) {
		this.histogramProvider = histogramProvider;
	}
}
