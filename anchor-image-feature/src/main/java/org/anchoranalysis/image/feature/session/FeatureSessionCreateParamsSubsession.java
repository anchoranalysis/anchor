package org.anchoranalysis.image.feature.session;

/*-
 * #%L
 * anchor-image-feature
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

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.Subsession;

public class FeatureSessionCreateParamsSubsession {

	private Subsession subsession;
	private FeatureSessionCreateParams.ParamsFactory paramsFactory;
	private FeatureCalcParams params;
	
	public FeatureSessionCreateParamsSubsession(Subsession subsession, FeatureSessionCreateParams.ParamsFactory paramsFactory, FeatureCalcParams params) {
		super();
		this.subsession = subsession;
		this.paramsFactory = paramsFactory;
		this.params = params;
	}

	public Subsession getSubsession() {
		return subsession;
	}

	public FeatureSessionCreateParams.ParamsFactory getParamsFactory() {
		return paramsFactory;
	}

	public ResultsVector calcSubset( FeatureList features ) throws FeatureCalcException {
		return subsession.calcSubset(features, params);
	}

	public double calc( Feature feature )
			throws FeatureCalcException {
		return subsession.calc(feature, params);
	}

	public void beforeNewCalc() {
		subsession.beforeNewCalc();
	}
	
	
}
