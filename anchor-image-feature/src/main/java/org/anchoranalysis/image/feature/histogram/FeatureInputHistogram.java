package org.anchoranalysis.image.feature.histogram;

import java.util.Optional;

import org.anchoranalysis.feature.input.FeatureInputWithRes;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.histogram.Histogram;

public class FeatureInputHistogram extends FeatureInputWithRes {

	private Histogram histogram;
	private Optional<ImageRes> res;

	/**
	 * Constructor
	 * 
	 * @param histogram
	 * @param res can be NULL so features should expect it
	 */
	public FeatureInputHistogram(Histogram histogram, Optional<ImageRes> res) {
		super();
		this.histogram = histogram;
		this.res = res;
	}

	@Override
	public Optional<ImageRes> getResOptional() {
		return res;
	}

	public Histogram getHistogram() {
		return histogram;
	}

	public void setHistogram(Histogram histogram) {
		this.histogram = histogram;
	}

	public void setRes(ImageRes res) {
		this.res = Optional.of(res);
	}
}
