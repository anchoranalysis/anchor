package org.anchoranalysis.image.bean.unitvalue.distance;

/*-
 * #%L
 * anchor-image-bean
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

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.orientation.DirectionVector;

public abstract class UnitValueDistance extends AnchorBean<UnitValueDistance> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Uses the direction between two points to resolve the distance.
	// NB the magnitude of the distance between these two points is not considered, only the direction
	public double rslv( ImageRes res, Point3d pnt1, Point3d pnt2 ) {
		DirectionVector dirVector = DirectionVector.createBetweenTwoPoints( pnt1, pnt2 );
		return rslv(res,dirVector);
	}
	
	// Uses the direction between two points to resolve the distance.
	// NB the magnitude of the distance between these two points is not considered, only the direction
	public double rslv( ImageRes res, Point3i pnt1, Point3i pnt2 ) {
		DirectionVector dirVector = DirectionVector.createBetweenTwoPoints( pnt1, pnt2 );
		return rslv(res,dirVector);
	}
	
	// Returns value in voxels
	public abstract double rslv( ImageRes res, DirectionVector dirVector );
}
