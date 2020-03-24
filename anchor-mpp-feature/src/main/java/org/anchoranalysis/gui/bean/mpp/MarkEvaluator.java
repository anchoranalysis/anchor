package org.anchoranalysis.gui.bean.mpp;



/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.define.Define;
import org.anchoranalysis.feature.bean.FeatureBean;

import ch.ethz.biol.cell.mpp.cfg.CfgGen;
import ch.ethz.biol.cell.mpp.nrg.nrgscheme.creator.NRGSchemeCreator;

public class MarkEvaluator extends FeatureBean<MarkEvaluator> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8930110016806371922L;

	// START BEAN PROPERTIES
	@BeanField
	private CfgGen cfgGen;
	
	@BeanField
	private Define namedDefinitions;
	
	@BeanField
	private NRGSchemeCreator nrgSchemeCreator;
	// END BEAN PROPERTIES

	public Define getNamedDefinitions() {
		return namedDefinitions;
	}

	public void setNamedDefinitions(Define namedDefinitions) {
		this.namedDefinitions = namedDefinitions;
	}

	public CfgGen getCfgGen() {
		return cfgGen;
	}

	public void setCfgGen(CfgGen cfgGen) {
		this.cfgGen = cfgGen;
	}

	public NRGSchemeCreator getNrgSchemeCreator() {
		return nrgSchemeCreator;
	}

	public void setNrgSchemeCreator(NRGSchemeCreator nrgSchemeCreator) {
		this.nrgSchemeCreator = nrgSchemeCreator;
	}
}
