package org.anchoranalysis.image.feature.bean.physical;

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
import org.anchoranalysis.feature.bean.operator.FeatureGenericSingleElem;
import org.anchoranalysis.feature.bean.operator.FeatureSingleElem;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.calc.params.FeatureCalcParamsWithRes;
import org.anchoranalysis.image.extent.ImageRes;

public abstract class FeatureSingleElemWithRes<T extends FeatureCalcParams> extends FeatureGenericSingleElem<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FeatureSingleElemWithRes() {
		
	}

	public FeatureSingleElemWithRes( Feature<T> feature  ) {
		super(feature);
	}
	
	@Override
	public final double calc(CacheableParams<T> params) throws FeatureCalcException {
		
		if (!(params.getParams() instanceof FeatureCalcParamsWithRes)) {
			throw new FeatureCalcException("Requires " + FeatureCalcParamsWithRes.class.getSimpleName() );
		}
		
		FeatureCalcParamsWithRes paramsCast = (FeatureCalcParamsWithRes) params.getParams();
		
		if (paramsCast.getRes()==null) {
			throw new FeatureCalcException("A resolution is required for this feature");
		}
		
		double value = params.calc( getItem() );
		
		return calcWithRes(value, paramsCast.getRes() );
	}
	
	protected abstract double calcWithRes( double value, ImageRes res ) throws FeatureCalcException;
}
