package org.anchoranalysis.image.bean.unitvalue.area;

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

/**
 * Area expressed in physical coordinates
 * 
 * @author Owen Feehan
 *
 */
public class UnitValueAreaPhysical extends UnitValueArea {

	// START BEAN PROPERTIES
	@BeanField
	private String prefix = "";
	// END BEAN PROPERTIES
	
	@Override
	public double resolveToVoxels(Optional<ImageResolution> resolution) throws UnitValueException {
		
		if (!resolution.isPresent()) {
			throw new UnitValueException("An image resolution is required to calculate physical-area but it is missing");
		}
		
		UnitSuffix unitPrefix = SpatialConversionUtilities.suffixFromMeterString(prefix);
		
		double valueAsBase = SpatialConversionUtilities.convertFromUnits(getValue(), unitPrefix);
		
		return ImageUnitConverter.convertFromPhysicalArea(valueAsBase, resolution.get());
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String toString() {
		if (prefix!=null && !prefix.isEmpty()) {
			return String.format("%.2f%s", getValue(),prefix);
		} else {
			return String.format("%.2f", getValue());
		}
	}

}
