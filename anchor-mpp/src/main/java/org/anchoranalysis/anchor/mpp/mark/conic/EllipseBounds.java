package org.anchoranalysis.anchor.mpp.mark.conic;

import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.bean.bound.BoundUnitless;

/*
 * #%L
 * anchor-mpp
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
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.orientation.Orientation;
import org.anchoranalysis.image.orientation.Orientation2D;

public class EllipseBounds extends EllipseBoundsWithoutRotation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5833714580114414447L;
	
	// START BEAN PROPERTIES
	@BeanField
	private Bound rotationAngle;
	// END BEAN PROPERTIES
	
	public EllipseBounds() {
		super();
		rotationAngle = null;
	}

	// Constructor
	public EllipseBounds(Bound radiusBnd) {
		super(radiusBnd);
		rotationAngle = new BoundUnitless(0, 2 * Math.PI  );
	}
	
	// Copy Constructor
	public EllipseBounds( EllipseBounds src ) {
		super(src);
		rotationAngle = src.rotationAngle.duplicate();
	}
	
	@Override
	public String getBeanDscr() {
		return String.format("%s, radius=(%s), rotation=(%s)", getBeanName(), getRadius().toString(), rotationAngle.toString() );
	}

	public Bound getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(Bound rotationAngle) {
		this.rotationAngle = rotationAngle;
	}

	@Override
	public Orientation randomOrientation(RandomNumberGenerator re, ImageRes res) {
		return new Orientation2D ( getRotationAngle().rslv(res, false).randOpen(re) );
	}
}
