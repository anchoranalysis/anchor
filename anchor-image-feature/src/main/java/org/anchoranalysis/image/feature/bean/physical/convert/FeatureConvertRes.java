package org.anchoranalysis.image.feature.bean.physical.convert;

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
import org.anchoranalysis.core.unit.SpatialConversionUtilities;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.feature.bean.physical.FeatureSingleElemWithRes;

public abstract class FeatureConvertRes<T extends FeatureCalcParams> extends FeatureSingleElemWithRes<T> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private String unitType;
	// END BEAN PROPERTIES
	
	public FeatureConvertRes() {
		
	}
	
	public FeatureConvertRes( Feature<T> feature, UnitSuffix unitType ) {
		super(feature);
		this.unitType = SpatialConversionUtilities.unitMeterStringDisplay(unitType);
	}

	@Override
	protected double calcWithRes(double value, ImageRes res) throws FeatureCalcException {
		double valuePhysical = convertToPhysical( value, res );
		return convertToUnits(valuePhysical);
	}
	
	protected abstract double convertToPhysical( double value, ImageRes res ) throws FeatureCalcException;

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
		
	private double convertToUnits( double valuePhysical ) {
		SpatialConversionUtilities.UnitSuffix prefixType = SpatialConversionUtilities.suffixFromMeterString(unitType);
		return SpatialConversionUtilities.convertToUnits( valuePhysical, prefixType );
	}

}
