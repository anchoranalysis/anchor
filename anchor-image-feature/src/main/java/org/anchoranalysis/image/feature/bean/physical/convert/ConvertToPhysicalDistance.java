package org.anchoranalysis.image.feature.bean.physical.convert;

/*
 * #%L
 * anchor-image-feature
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
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParamsWithRes;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.image.bean.orientation.DirectionVectorBean;
import org.anchoranalysis.image.convert.ImageUnitConverter;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.orientation.DirectionVector;

// 
public class ConvertToPhysicalDistance<T extends FeatureCalcParamsWithRes> extends FeatureConvertRes<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	
	// Defaults to a unit vector in the Z-plane along the X-axis
	@BeanField
	private DirectionVectorBean directionVector = new DirectionVectorBean(1.0, 0, 0);
	// END BEAN PROPERTIES
	
	private DirectionVector dv;

	public ConvertToPhysicalDistance() {
		
	}
	
	public ConvertToPhysicalDistance( Feature<T> feature, UnitSuffix unitType, DirectionVector directionVector ) {
		super(feature, unitType);
		this.directionVector = new DirectionVectorBean(directionVector);
	}
	
	@Override
	public void beforeCalc(FeatureInitParams params) throws InitException {
		super.beforeCalc(params);
		dv = directionVector.createVector();
	}
	
	@Override
	protected double convertToPhysical(double value, ImageRes res) throws FeatureCalcException {
		// We use arbitrary direction as everything should be the same in a isometric XY plane
		return ImageUnitConverter.convertToPhysicalDistance(value, res, dv );
	}

	public DirectionVectorBean getDirectionVector() {
		return directionVector;
	}

	public void setDirectionVector(DirectionVectorBean directionVector) {
		this.directionVector = directionVector;
	}



}
