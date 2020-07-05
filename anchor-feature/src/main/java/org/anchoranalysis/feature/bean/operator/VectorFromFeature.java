package org.anchoranalysis.feature.bean.operator;

import java.util.Arrays;

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
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;
import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptorUtilities;

/**
 * Calculates a vector of results, based upon 3 features that define the x, y and z components of the vector
 * 
 * @author Owen Feehan
 */
public class VectorFromFeature<T extends FeatureInput> extends FeatureBase<T> {

	// START BEAN PROPERTIES
	@BeanField
	private Feature<T> x;
	
	@BeanField
	private Feature<T> y;
	
	@BeanField
	private Feature<T> z;
	// END BEAN PROPERTIES
	
	public Vector3d calc( SessionInput<T> input ) throws FeatureCalcException {
		return new Vector3d(
			input.calc(x),
			input.calc(y),
			input.calc(z)
		);
	}
	
	/** A list of the features for all dimensions */
	public FeatureList<T> allFeatures() {
		return new FeatureList<>(
			Arrays.asList(x,y,z)
		);
	}
	
	@Override
	public FeatureInputDescriptor inputDescriptor() {
		return FeatureInputDescriptorUtilities.paramTypeForThree(x,y,z);
	}

	public Feature<T> getX() {
		return x;
	}

	public void setX(Feature<T> x) {
		this.x = x;
	}

	public Feature<T> getY() {
		return y;
	}

	public void setY(Feature<T> y) {
		this.y = y;
	}

	public Feature<T> getZ() {
		return z;
	}

	public void setZ(Feature<T> z) {
		this.z = z;
	}
}
