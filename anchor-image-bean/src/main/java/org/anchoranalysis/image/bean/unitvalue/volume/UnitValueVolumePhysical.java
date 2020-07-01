package org.anchoranalysis.image.bean.unitvalue.volume;

import java.util.Optional;

/*
 * #%L
 * anchor-image-bean
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
import org.anchoranalysis.core.unit.SpatialConversionUtilities;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.image.bean.nonbean.error.UnitValueException;
import org.anchoranalysis.image.convert.ImageUnitConverter;
import org.anchoranalysis.image.extent.ImageResolution;

// Measures either area or volume (depending if the do3D flag is employed)
public class UnitValueVolumePhysical extends UnitValueVolume {

	// START VALUE
	@BeanField
	private double value;	// value in metres
	
	@BeanField
	private String unitType = "";
	// END VALUE
	
	@Override
	public double rslv(Optional<ImageResolution> res) throws UnitValueException {
		if (!res.isPresent()) {
			throw new UnitValueException("An image resolution is required to calculate physical-volume but it is missing");
		}
		
		UnitSuffix unitPrefix = SpatialConversionUtilities.suffixFromMeterString(unitType);
		
		double valueAsBase = SpatialConversionUtilities.convertFromUnits(value, unitPrefix);
		
		return ImageUnitConverter.convertFromPhysicalVolume(valueAsBase, res.get());
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}


	
	@Override
	public String toString() {
		if (unitType!=null && !unitType.isEmpty()) {
			return String.format("%.2f%s",value,unitType);
		} else {
			return String.format("%.2f",value);
		}
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

}
