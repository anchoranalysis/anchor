package org.anchoranalysis.anchor.mpp.feature.bean.nrg.elem;

import org.anchoranalysis.anchor.mpp.feature.nrg.elem.NRGElemPairCalcParams;
import org.anchoranalysis.anchor.mpp.feature.nrg.elem.NRGElemPairCalcParamsDescriptor;

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

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.params.FeatureParamsDescriptor;

public abstract class NRGElemPair extends Feature {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	@Override
	public double calc( FeatureCalcParams params ) throws FeatureCalcException {
		
		if (params instanceof NRGElemPairCalcParams) {
			return calcCast( (NRGElemPairCalcParams) params );
		} else {
			throw new FeatureCalcException("Requires NRGElemPairCalcParams");
		}
	}
	
	public abstract double calcCast( NRGElemPairCalcParams params ) throws FeatureCalcException;
	
	@Override
	public FeatureParamsDescriptor paramType()
			throws FeatureCalcException {
		return NRGElemPairCalcParamsDescriptor.instance;
	}
}
