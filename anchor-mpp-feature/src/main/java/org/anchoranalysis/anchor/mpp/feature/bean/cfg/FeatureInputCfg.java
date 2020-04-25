package org.anchoranalysis.anchor.mpp.feature.bean.cfg;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.feature.input.FeatureInputWithRes;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;

public class FeatureInputCfg extends FeatureInputWithRes {

	private Cfg cfg;
	private Optional<ImageDim> dim;
	
	public FeatureInputCfg(Cfg cfg, Optional<ImageDim> dim) {
		super();
		this.cfg = cfg;
		this.dim = dim;
	}

	@Override
	public Optional<ImageRes> getResOptional() {
		return dim.map( ImageDim::getRes );
	}

	public Cfg getCfg() {
		return cfg;
	}

	public void setCfg(Cfg cfg) {
		this.cfg = cfg;
	}

	public Optional<ImageDim> getDimensions() {
		return dim;
	}
}
