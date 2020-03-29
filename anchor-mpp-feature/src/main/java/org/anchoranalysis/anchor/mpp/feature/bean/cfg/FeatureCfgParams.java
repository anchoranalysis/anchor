package org.anchoranalysis.anchor.mpp.feature.bean.cfg;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;

/*-
 * #%L
 * anchor-mpp-feature
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import org.anchoranalysis.feature.calc.params.FeatureCalcParamsWithRes;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;

public class FeatureCfgParams extends FeatureCalcParamsWithRes {

	private Cfg cfg;
	private ImageDim dim;
	
	public FeatureCfgParams(Cfg cfg, ImageDim dim) {
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
