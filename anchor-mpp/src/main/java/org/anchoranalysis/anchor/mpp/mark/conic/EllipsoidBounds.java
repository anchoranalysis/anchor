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

public class EllipsoidBounds extends EllipseBoundsWithoutRotation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2134174080057660104L;
	
	// START BEAN PROPERTIES
	@BeanField
	private Bound rotationX;
	
	@BeanField
	private Bound rotationY;
	
	@BeanField
	private Bound rotationZ;
	// END BEAN PROPERTIES
	
	public EllipsoidBounds() {
		super();
		rotationX = null;
		rotationY = null;
		rotationZ = null;
	}

	// Constructor
	public EllipsoidBounds(Bound radiusBnd) {
		super(radiusBnd);
		rotationX = new BoundUnitless(0, 2 * Math.PI  );
		rotationY = new BoundUnitless(0, 2 * Math.PI );
		rotationZ = new BoundUnitless(0, 2 * Math.PI );
	}
	
	// Copy Constructor
	public EllipsoidBounds( EllipsoidBounds src ) {
		super(src);
		rotationX = src.rotationX.duplicate();
		rotationY = src.rotationY.duplicate();
		rotationZ = src.rotationZ.duplicate();
	}
	

	public Bound getRotation(int dimNum) {
		switch (dimNum) {
		case 0:
			return getRotationX();
		case 1:
			return getRotationY();
		case 2:
			return getRotationZ();
		default:
			assert false;
			return null;
		}
	}
	
	@Override
	public String getBeanDscr() {
		return String.format("%s, radius=(%s), rotation=(%f,%f,%f)", getBeanName(), getRadius().toString(), getRotationX(), getRotationY(), rotationZ );
	}

	public Bound getRotationX() {
		return rotationX;
	}

	public void setRotationX(Bound rotationX) {
		this.rotationX = rotationX;
	}

	public Bound getRotationY() {
		return rotationY;
	}

	public void setRotationY(Bound rotationY) {
		this.rotationY = rotationY;
	}

	public Bound getRotationZ() {
		return rotationZ;
	}

	public void setRotationZ(Bound rotationZ) {
		this.rotationZ = rotationZ;
	}

	@Override
	public Orientation randomOrientation(RandomNumberGenerator re, ImageRes res) {
		return EllipsoidRandomizer.randomizeRot(this, re, res);
	}
	
	
}