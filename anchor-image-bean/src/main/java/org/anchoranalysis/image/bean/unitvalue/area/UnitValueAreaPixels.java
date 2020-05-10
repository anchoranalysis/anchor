package org.anchoranalysis.image.bean.unitvalue.area;

import java.util.Optional;

import org.anchoranalysis.image.extent.ImageRes;

/**
 * Area expressed as square pixels
 * 
 * @author Owen Feehan
 *
 */
public class UnitValueAreaPixels extends UnitValueArea {

	public UnitValueAreaPixels() {
		// Standard bean constructor
	}
	
	public UnitValueAreaPixels(double value) {
		super(value);
	}

	@Override
	public double rslv(Optional<ImageRes> res) {
		return getValue();
	}

	@Override
	public String toString() {
		return String.format("%.2f",getValue());
	}
}
