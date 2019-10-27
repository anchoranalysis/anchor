package ch.ethz.biol.cell.mpp.nrg.feature.session;

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

import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.feature.session.FeatureSessionCreateParamsSingle;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.pxlmark.memo.PxlMarkMemo;
import ch.ethz.biol.cell.mpp.nrg.feature.session.FeatureSessionCreateParamsMPP.ParamsFactory;

public class FeatureSessionCreateParamsMPPSingle {


	/**
	 * From where the single feature is calculated
	 */
	private FeatureSessionCreateParamsSingle delegate;
	
	private ParamsFactory paramsFactory;
	
	public FeatureSessionCreateParamsMPPSingle( FeatureSessionCreateParamsSingle delegate, NRGStack nrgStack, KeyValueParams keyValueParams ) {
		this.delegate = delegate;
		delegate.setRes( nrgStack.getDimensions().getRes() );
		paramsFactory = new ParamsFactory(delegate.getDelegate(), nrgStack, keyValueParams);
	}
	
	public double calc() throws FeatureCalcException {
		return delegate.calc();
	}
	
	public double calc(FeatureCalcParams params)
			throws FeatureCalcException {
		return delegate.calc(params);
	}
	
	public String featureName() {
		return delegate.featureName();
	}

	public Feature getFeature() {
		return delegate.getFeature();
	}

	public FeatureInitParams getParamsInit() {
		return delegate.getParamsInit();
	}

	public SharedFeatureSet getSharedFeatures() {
		return delegate.getSharedFeatures();
	}
		
	public double calc(Cfg cfg) throws FeatureCalcException {
		return delegate.calc( paramsFactory.createParams(cfg) );
	}
	
	
	public double calc(Mark mark) throws FeatureCalcException {
		return delegate.calc( paramsFactory.createParams(mark) );
	}	

	public double calc(PxlMarkMemo mark1, PxlMarkMemo mark2, ImageDim sd)
			throws FeatureCalcException {
		return delegate.calc( paramsFactory.createParams(mark1, mark2, sd) );
	}
}
