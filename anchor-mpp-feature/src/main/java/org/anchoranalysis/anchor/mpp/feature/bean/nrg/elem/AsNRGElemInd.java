package org.anchoranalysis.anchor.mpp.feature.bean.nrg.elem;

import org.anchoranalysis.anchor.mpp.feature.nrg.elem.NRGElemIndCalcParams;
import org.anchoranalysis.anchor.mpp.feature.nrg.elem.NRGElemPairCalcParams;
import org.anchoranalysis.anchor.mpp.feature.nrg.elem.NRGElemPairCalcParamsDescriptor;

/*
 * #%L
 * anchor-mpp
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
import org.anchoranalysis.feature.bean.operator.FeatureSingleElem;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.params.FeatureParamsDescriptor;

public class AsNRGElemInd extends FeatureSingleElem<NRGElemPairCalcParams,NRGElemIndCalcParams> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	// START BEAN PROPERTIES
	@BeanField
	private boolean first = true;		// If it's true, we take the first object.  Otherwise the second
	// END BEAN PROPERTIES
	
	@Override
	public double calc(CacheableParams<NRGElemPairCalcParams> params) throws FeatureCalcException {
		return params
			.calcChangeParams(
				getItem(),
				p -> extractParams(p),
				"ind"
			);		
	}
	
	private NRGElemIndCalcParams extractParams( NRGElemPairCalcParams params ) {
		return new NRGElemIndCalcParams(
			first ? params.getObj1() : params.getObj2(),
			params.getNrgStack()
		);
	}

	// We change the default behaviour, as we don't want to give the same paramsFactory
	//   as the item we pass to
	@Override
	public FeatureParamsDescriptor paramType()
			throws FeatureCalcException {
		return NRGElemPairCalcParamsDescriptor.instance;
	}

	@Override
	public String getParamDscr() {
		return getItem().getParamDscr();
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}


}
