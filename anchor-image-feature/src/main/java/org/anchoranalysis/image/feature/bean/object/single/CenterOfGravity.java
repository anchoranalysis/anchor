package org.anchoranalysis.image.feature.bean.object.single;

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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.axis.AxisTypeConverter;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;

public class CenterOfGravity extends FeatureSingleObject {

	// START BEAN PROPERTIES
	@BeanField
	private String axis = "x";
	
	@BeanField
	private double emptyValue = 0;
	// END BEAN PROPERTIES
	
	/** Standard bean constructor */
	public CenterOfGravity() {}
	
	/**
	 * Constructor - create for a specific axis
	 * 
	 * @param axis axis
	 */
	public CenterOfGravity( AxisType axis ) {
		this.axis = axis.toString().toLowerCase();
	}
	
	@Override
	public double calc(SessionInput<FeatureInputSingleObject> input) {
		
		FeatureInputSingleObject params = input.get();
		
		double val = params.getObjectMask().centerOfGravity(
			axisType()
		);
		
		if (Double.isNaN(val)) {
			return emptyValue;
		}
		
		return val;
	}

	public String getAxis() {
		return axis;
	}

	public void setAxis(String axis) {
		this.axis = axis;
	}

	public double getEmptyValue() {
		return emptyValue;
	}

	public void setEmptyValue(double emptyValue) {
		this.emptyValue = emptyValue;
	}
	
	private AxisType axisType() {
		return AxisTypeConverter.createFromString(axis);
	}
}