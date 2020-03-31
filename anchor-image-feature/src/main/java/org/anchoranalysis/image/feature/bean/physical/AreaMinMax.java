package org.anchoranalysis.image.feature.bean.physical;

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
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.bean.unitvalue.area.UnitValueArea;
import org.anchoranalysis.image.bean.unitvalue.area.UnitValueAreaPixels;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.unitvalue.UnitValueException;

public class AreaMinMax extends FeatureNRGRange {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private UnitValueArea min = new UnitValueAreaPixels( 0 );
	
	@BeanField
	private UnitValueArea max = new UnitValueAreaPixels( Double.MAX_VALUE );
	// END BEAN PROPERTIES

	@Override
	protected double calcWithRes(double value, ImageRes res) throws FeatureCalcException {
		try {
			double minVoxels = min.rslv(res);
			double maxVoxels = max.rslv(res);
			
			return multiplexNRG(value >= minVoxels && value <= maxVoxels);
		} catch (UnitValueException e) {
			throw new FeatureCalcException(e);
		}
	}
	
	@Override
	public String getParamDscr() {
		return String.format("min=%s max=%s", min.toString(), max.toString(), paramDscrNRG());
	}

	public UnitValueArea getMin() {
		return min;
	}

	public void setMin(UnitValueArea min) {
		this.min = min;
	}

	public UnitValueArea getMax() {
		return max;
	}

	public void setMax(UnitValueArea max) {
		this.max = max;
	}
}
