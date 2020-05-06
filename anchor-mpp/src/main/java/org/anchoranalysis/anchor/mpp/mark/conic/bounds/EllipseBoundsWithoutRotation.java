package org.anchoranalysis.anchor.mpp.mark.conic.bounds;

import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.bean.bound.OrientableBounds;

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
import org.anchoranalysis.image.extent.ImageRes;

public abstract class EllipseBoundsWithoutRotation extends OrientableBounds {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1109615535786453388L;
	
	// START BEAN PROPERTIES
	@BeanField
	private Bound radius;
	// END BEAN PROPERTIES
	
	public EllipseBoundsWithoutRotation() {
		super();
		radius = null;
	}
	
	// Constructor
	public EllipseBoundsWithoutRotation(Bound radiusBound) {
		super();
		radius = radiusBound;
	}
	
	// Copy Constructor
	public EllipseBoundsWithoutRotation( EllipseBoundsWithoutRotation src ) {
		super();
		radius = src.radius.duplicate();
	}
	
	public Bound getRadius() {
		return radius;
	}

	public void setRadius(Bound radiusX) {
		this.radius = radiusX;
	}
	
	// NB objects are scaled in pre-rotated position i.e. when aligned to axes
	public void scaleXY( double mult_factor ) {
		this.radius.scale(mult_factor);
	}
	
	@Override
	public double getMinRslvd( ImageRes sr, boolean do3D ) {
		return radius.getMinRslvd(sr, do3D);
	}
	
	@Override
	public double getMaxRslvd( ImageRes sr, boolean do3D ) {
		return radius.getMaxRslvd(sr, do3D);
	}
	

}
