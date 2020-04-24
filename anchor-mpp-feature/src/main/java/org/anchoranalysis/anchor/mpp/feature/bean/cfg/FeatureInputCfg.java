package org.anchoranalysis.anchor.mpp.feature.bean.cfg;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.feature.input.FeatureInputWithRes;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;

public class FeatureInputCfg extends FeatureInputWithRes {

	private Cfg cfg;
	private ImageDim dim;
	
	public FeatureInputCfg(Cfg cfg, ImageDim dim) {
		super();
		this.cfg = cfg;
		this.dim = dim;
	}

	@Override
	public ImageRes getRes() {
		return dim.getRes();
	}

	public Cfg getCfg() {
		return cfg;
	}

	public void setCfg(Cfg cfg) {
		this.cfg = cfg;
	}

	public ImageDim getDimensions() {
		return dim;
	}
}
