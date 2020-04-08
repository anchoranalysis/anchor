package org.anchoranalysis.feature.bean.operator;

/*
 * #%L
 * anchor-feature
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
import org.anchoranalysis.core.geometry.Vector3d;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.FeatureBase;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.params.FeatureParamsDescriptor;
import org.anchoranalysis.feature.params.ParamTypeUtilities;

/**
 * Calculates a vector of results, based upon 3 features that define the x, y and z components of the vector
 * 
 * @author Owen Feehan
 */
public class VectorFromFeature extends FeatureBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private Feature<FeatureCalcParams> x;
	
	@BeanField
	private Feature<FeatureCalcParams> y;
	
	@BeanField
	private Feature<FeatureCalcParams> z;
	// END BEAN PROPERTIES
	
	public VectorFromFeature() {
		super();
	}
	
	public Vector3d calc( CacheableParams<FeatureCalcParams> params  ) throws FeatureCalcException {
		
		double valX = params.calc(x);
		double valY = params.calc(y);
		double valZ = params.calc(z);
		
		return new Vector3d(valX,valY,valZ);
	}

	public Feature<FeatureCalcParams> getX() {
		return x;
	}

	public void setX(Feature<FeatureCalcParams> x) {
		this.x = x;
	}

	public Feature<FeatureCalcParams> getY() {
		return y;
	}

	public void setY(Feature<FeatureCalcParams> y) {
		this.y = y;
	}

	public Feature<FeatureCalcParams> getZ() {
		return z;
	}

	public void setZ(Feature<FeatureCalcParams> z) {
		this.z = z;
	}

	@Override
	public FeatureParamsDescriptor paramType() throws FeatureCalcException {
		return ParamTypeUtilities.paramTypeForThree(x,y,z);
	}
}
